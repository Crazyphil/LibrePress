package it.kapfer.librepress.server;

import it.kapfer.librepress.drm.EncryptionKeyProvider;
import it.kapfer.librepress.pdf.NewspaperReader;
import it.kapfer.librepress.server.exception.NewspaperActivationException;
import it.kapfer.librepress.server.xml.ActivationResponse;
import it.kapfer.librepress.server.xml.ActivationResponse.DocumentInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivationServiceTest {

    private static final DeviceRegistration REGISTRATION = new DeviceRegistration("deadbeef-15f2-4681-aa47-06d1521bf4ff", 1234567890);
    private static final NewspaperActivation ACTIVATION = new NewspaperActivation(
            123, "Brain Games", "sfdy2019021000000000001001", "https://secure.example.com/activate?issue=sfdy2019021000000000001001&certificateid=12345678");

    @Mock
    private RequestExecutor requestExecutor;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<String> activationNumberCaptor;

    @Captor
    private ArgumentCaptor<Integer> clientNumberCaptor;

    private ActivationService activationService;

    @BeforeEach
    void setUp() {
        activationService = new ActivationService(requestExecutor);
    }

    @Nested
    class ActivatePushedNewspaper {

        @Test
        void passesActivationUrlTokenAndClientIdToExecutor() {
            when(requestExecutor.executeActivationRequest(anyString(), anyString(), anyInt()))
                    .thenReturn(CompletableFuture.completedFuture(createErrorResponse("AuthenticationFailed")));

            activationService.activatePushedNewspaper(REGISTRATION, ACTIVATION);

            verify(requestExecutor).executeActivationRequest(
                    urlCaptor.capture(), activationNumberCaptor.capture(), clientNumberCaptor.capture());
            assertEquals(ACTIVATION.licenseUrl(), urlCaptor.getValue());
            assertEquals(REGISTRATION.activationToken(), activationNumberCaptor.getValue());
            assertEquals(REGISTRATION.clientId(), clientNumberCaptor.getValue());
        }

        @Test
        void mapsResponseToNewspaperIssue() {
            var mockEncryptionKey = new byte[]{1, 2, 3, 4};
            try (MockedConstruction<EncryptionKeyProvider> ignored = mockConstruction(EncryptionKeyProvider.class,
                    (mock, context) -> when(mock.getEncryptionKey()).thenReturn(mockEncryptionKey))) {

                var response = createOkResponse();
                when(requestExecutor.executeActivationRequest(anyString(), anyString(), anyInt()))
                        .thenReturn(CompletableFuture.completedFuture(response));

                NewspaperIssue issue = activationService.activatePushedNewspaper(REGISTRATION, ACTIVATION).join();

                assertEquals("sfdy2019021000000000001001", issue.issue());
                assertEquals("Brain Games", issue.title());
                assertEquals(LocalDateTime.of(2026, 7, 2, 23, 59, 59), issue.urlExpirationTime());
                assertEquals(5, issue.downloadUrls().size());
                assertArrayEquals(mockEncryptionKey, issue.encryptionKey());
            }
        }

        @Test
        void throwsNewspaperActivationExceptionForAuthenticationFailed() {
            var response = createErrorResponse("AuthenticationFailed");
            when(requestExecutor.executeActivationRequest(anyString(), anyString(), anyInt()))
                    .thenReturn(CompletableFuture.completedFuture(response));

            var activateCall = activationService.activatePushedNewspaper(REGISTRATION, ACTIVATION);

            CompletionException e = assertThrows(CompletionException.class, activateCall::join);
            assertInstanceOf(NewspaperActivationException.class, e.getCause());
            NewspaperActivationException cause = (NewspaperActivationException) e.getCause();
            assertEquals(response.statusCode, cause.getErrorCode());
        }

        @Test
        void throwsNewspaperActivationExceptionForServerError() {
            var response = createErrorResponse("ServerError");
            response.statusMessage = "Issue already activated";
            when(requestExecutor.executeActivationRequest(anyString(), anyString(), anyInt()))
                    .thenReturn(CompletableFuture.completedFuture(response));

            var activateCall = activationService.activatePushedNewspaper(REGISTRATION, ACTIVATION);

            CompletionException e = assertThrows(CompletionException.class, activateCall::join);
            assertInstanceOf(NewspaperActivationException.class, e.getCause());
            NewspaperActivationException cause = (NewspaperActivationException) e.getCause();
            assertEquals(response.statusCode, cause.getErrorCode());
            assertEquals(response.statusMessage, cause.getErrorMessage());
        }

        @Test
        void wrapsExecutorException() {
            when(requestExecutor.executeActivationRequest(anyString(), anyString(), anyInt()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("network error")));

            var activateCall = activationService.activatePushedNewspaper(REGISTRATION, ACTIVATION);

            CompletionException e = assertThrows(CompletionException.class, activateCall::join);
            assertEquals("network error", e.getCause().getMessage());
        }

        private ActivationResponse createOkResponse() {
            var response = new ActivationResponse();
            response.certificate = "dGVzdA==";
            response.issue = "sfdy2019021000000000001001";
            response.statusCode = "Ok";
            response.urlExpirationTime = LocalDateTime.of(2026, 7, 2, 23, 59, 59);

            var documentInfo = new DocumentInfo();
            documentInfo.title = "Brain Games";
            response.documentInfo = documentInfo;

            var downloadUrls = List.of(
                    "http://cdn.example.com/cds/files?1",
                    "http://cdn.example.com/cds/files?2",
                    "http://cds212.example.com/cds/files?3",
                    "http://cds211.example.com/cds/files?4",
                    "http://cds202.example.com/cds/files?5");
            response.downloadUrls = downloadUrls;

            return response;
        }

        private ActivationResponse createErrorResponse(String statusCode) {
            var response = new ActivationResponse();
            response.statusCode = statusCode;
            return response;
        }
    }

    @Nested
    class OpenNewspaper {
        private static final NewspaperIssue NEWSPAPER_ISSUE = new NewspaperIssue(
                "test-issue", "Test Newspaper", LocalDateTime.now(),
                List.of("http://example.com/file.pdf"),
                new byte[]{1, 2, 3, 4});

        @Test
        void delegatesToExecutorAndConstructsReader() {
            when(requestExecutor.executeDownloadRequest(NEWSPAPER_ISSUE.downloadUrls()))
                    .thenReturn(CompletableFuture.completedFuture(new ByteArrayInputStream(new byte[0])));

            try (MockedConstruction<NewspaperReader> mocked = mockConstruction(NewspaperReader.class)) {
                NewspaperReader reader = activationService.openNewspaper(NEWSPAPER_ISSUE).join();
                assertNotNull(reader);
                assertEquals(1, mocked.constructed().size());
            }
        }

        @Test
        void delegatesDownloadAndWritesToFile() throws Exception {
            File tempFile = File.createTempFile("test", ".pdf");
            tempFile.deleteOnExit();

            byte[] pdfContent = {1, 2, 3, 4};
            when(requestExecutor.executeDownloadRequest(NEWSPAPER_ISSUE.downloadUrls()))
                    .thenReturn(CompletableFuture.completedFuture(new ByteArrayInputStream(pdfContent)));

            try (MockedConstruction<NewspaperReader> mocked = mockConstruction(NewspaperReader.class)) {
                NewspaperReader reader = activationService.openNewspaper(NEWSPAPER_ISSUE, tempFile).join();
                assertNotNull(reader);
                assertArrayEquals(pdfContent, Files.readAllBytes(tempFile.toPath()));
                assertEquals(1, mocked.constructed().size());
            }
        }

        @Test
        void failsWhenExecutorFails() {
            when(requestExecutor.executeDownloadRequest(NEWSPAPER_ISSUE.downloadUrls()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("download failed")));

            var call = activationService.openNewspaper(NEWSPAPER_ISSUE);
            CompletionException e = assertThrows(CompletionException.class, call::join);
            assertEquals("download failed", e.getCause().getMessage());
        }

        @Test
        void throwsWhenOutputFileIsDirectory() {
            File dir = new File(System.getProperty("java.io.tmpdir"));
            assertThrows(IllegalArgumentException.class,
                    () -> activationService.openNewspaper(NEWSPAPER_ISSUE, dir));
        }
    }
}
