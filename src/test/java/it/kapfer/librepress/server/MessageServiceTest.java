package it.kapfer.librepress.server;

import it.kapfer.librepress.server.exception.HttpException;
import it.kapfer.librepress.server.xml.Request;
import it.kapfer.librepress.server.xml.authentication.ActivationAuthentication;
import it.kapfer.librepress.server.xml.message.NewspaperMessage;
import it.kapfer.librepress.server.xml.request.DeleteMessagesRequest;
import it.kapfer.librepress.server.xml.request.GetMessagesRequest;
import it.kapfer.librepress.server.xml.response.EmptyResponse;
import it.kapfer.librepress.server.xml.response.ErrorResponse;
import it.kapfer.librepress.server.xml.response.GetMessagesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    private static final String ACTIVATION_TOKEN = "deadbeef-15f2-4681-aa47-75551b48f42c";
    private static final int CLIENT_ID = 12345678;
    private static final DeviceRegistration REGISTRATION = new DeviceRegistration(ACTIVATION_TOKEN, CLIENT_ID);

    @Mock
    private RequestExecutor requestExecutor;

    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(requestExecutor);
    }

    @Nested
    class GetNewspaperDownloadRequests {

        @Test
        void mapsNewspaperMessagesCorrectly() {
            var newspaperMessage = new NewspaperMessage();
            newspaperMessage.id = 1366495904;
            newspaperMessage.title = "Daily News";
            newspaperMessage.issueId = "issue001";
            newspaperMessage.getLicenseUrl = "https://example.com/activate?issue=issue001&certificateid=12345678";

            var response = new GetMessagesResponse();
            response.messages = List.of(newspaperMessage);

            when(requestExecutor.executeRequest(any(), eq(GetMessagesResponse.class)))
                    .thenReturn(CompletableFuture.completedFuture(response));

            List<NewspaperActivation> result = messageService.getPushedNewspapers(REGISTRATION).join();

            assertEquals(1, result.size());
            NewspaperActivation request = result.get(0);
            assertEquals(newspaperMessage.title, request.title());
            assertEquals(newspaperMessage.issueId, request.issueId());
            assertEquals(newspaperMessage.getLicenseUrl, request.licenseUrl());
        }

        @Test
        void filtersOutNonNewspaperMessages() {
            var newspaperMessage = new NewspaperMessage();
            newspaperMessage.id = 1;
            newspaperMessage.title = "Daily News";
            newspaperMessage.issueId = "issue001";
            newspaperMessage.getLicenseUrl = "https://example.com/license";

            var response = new GetMessagesResponse();
            // Include a non-newspaper message (mock with different type)
            var otherMessage = mock(it.kapfer.librepress.server.xml.ResponseMessage.class);
            response.messages = List.of(newspaperMessage, otherMessage);

            when(requestExecutor.executeRequest(any(), eq(GetMessagesResponse.class)))
                    .thenReturn(CompletableFuture.completedFuture(response));

            List<NewspaperActivation> result = messageService.getPushedNewspapers(REGISTRATION).join();

            assertEquals(1, result.size());
            assertEquals("Daily News", result.get(0).title());
        }

        @Test
        void returnsEmptyListForNullResponse() {
            when(requestExecutor.executeRequest(any(), eq(GetMessagesResponse.class)))
                    .thenReturn(CompletableFuture.completedFuture(null));

            List<NewspaperActivation> result = messageService.getPushedNewspapers(REGISTRATION).join();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void returnsEmptyListForEmptyMessages() {
            var response = new GetMessagesResponse();
            response.messages = List.of();

            when(requestExecutor.executeRequest(any(), eq(GetMessagesResponse.class)))
                    .thenReturn(CompletableFuture.completedFuture(response));

            List<NewspaperActivation> result = messageService.getPushedNewspapers(REGISTRATION).join();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void usesActivationAuthentication() {
            var response = new GetMessagesResponse();
            response.messages = List.of();

            when(requestExecutor.executeRequest(any(), eq(GetMessagesResponse.class)))
                    .thenReturn(CompletableFuture.completedFuture(response));

            messageService.getPushedNewspapers(REGISTRATION).join();

            verify(requestExecutor).executeRequest(requestCaptor.capture(), eq(GetMessagesResponse.class));
            var request = requestCaptor.getValue();
            assertInstanceOf(ActivationAuthentication.class, request.authentication);
            var auth = (ActivationAuthentication) request.authentication;
            assertEquals(CLIENT_ID, auth.clientNumber.intValue());
            assertEquals(ACTIVATION_TOKEN, auth.activationNumber);
        }

        @Test
        void usesGetMessagesRequest() {
            var response = new GetMessagesResponse();
            response.messages = List.of();

            when(requestExecutor.executeRequest(any(), eq(GetMessagesResponse.class)))
                    .thenReturn(CompletableFuture.completedFuture(response));

            messageService.getPushedNewspapers(REGISTRATION).join();

            verify(requestExecutor).executeRequest(requestCaptor.capture(), eq(GetMessagesResponse.class));
            assertInstanceOf(GetMessagesRequest.class, requestCaptor.getValue());
        }

        @Test
        void propagatesErrorResponse() {
            var errorResponse = new ErrorResponse();
            errorResponse.errorCode = 203;
            errorResponse.errorMessage = "Unable to find activation";

            when(requestExecutor.executeRequest(any(), eq(GetMessagesResponse.class)))
                    .thenReturn(CompletableFuture.failedFuture(new HttpException(errorResponse)));

            var getNewspaperDownloadRequestsCall = messageService.getPushedNewspapers(REGISTRATION);

            CompletionException e = assertThrows(CompletionException.class, getNewspaperDownloadRequestsCall::join);
            assertInstanceOf(HttpException.class, e.getCause());
            assertTrue(e.getCause().getMessage().contains("Unable to find activation"));
        }

        @Test
        void wrapsExecutorException() {
            when(requestExecutor.executeRequest(any(), eq(GetMessagesResponse.class)))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("network error")));

            var getNewspaperDownloadRequestsCall = messageService.getPushedNewspapers(REGISTRATION);

            CompletionException e = assertThrows(CompletionException.class, getNewspaperDownloadRequestsCall::join);
            assertEquals("network error", e.getCause().getMessage());
        }
    }

    @Nested
    class DeleteMessage {

        @Test
        void returnsVoidOnSuccess() {
            when(requestExecutor.executeRequest(any(), eq(EmptyResponse.class)))
                    .thenReturn(CompletableFuture.completedFuture(new EmptyResponse()));

            DeletableMessage message = mock(DeletableMessage.class);
            when(message.messageId()).thenReturn(1);

            var deleteMessageCall = messageService.deleteMessages(REGISTRATION, List.of(message));
            assertDoesNotThrow(deleteMessageCall::join);
        }

        @Test
        void returnsCompletedFutureForEmptyList() {
            var deleteMessageCall = messageService.deleteMessages(REGISTRATION, List.of());
            assertDoesNotThrow(deleteMessageCall::join);
        }

        @Test
        void usesActivationAuthentication() {
            when(requestExecutor.executeRequest(any(), eq(EmptyResponse.class)))
                    .thenReturn(CompletableFuture.completedFuture(new EmptyResponse()));

            DeletableMessage message = mock(DeletableMessage.class);
            when(message.messageId()).thenReturn(1);

            messageService.deleteMessages(REGISTRATION, List.of(message)).join();

            verify(requestExecutor).executeRequest(requestCaptor.capture(), eq(EmptyResponse.class));
            var request = requestCaptor.getValue();
            assertInstanceOf(ActivationAuthentication.class, request.authentication);
            var auth = (ActivationAuthentication) request.authentication;
            assertEquals(CLIENT_ID, auth.clientNumber.intValue());
            assertEquals(ACTIVATION_TOKEN, auth.activationNumber);
        }

        @Test
        void usesDeleteMessagesRequest() {
            when(requestExecutor.executeRequest(any(), eq(EmptyResponse.class)))
                    .thenReturn(CompletableFuture.completedFuture(new EmptyResponse()));

            DeletableMessage message = mock(DeletableMessage.class);
            when(message.messageId()).thenReturn(1);

            messageService.deleteMessages(REGISTRATION, List.of(message)).join();

            verify(requestExecutor).executeRequest(requestCaptor.capture(), eq(EmptyResponse.class));
            assertInstanceOf(DeleteMessagesRequest.class, requestCaptor.getValue());
        }

        @Test
        void passesMessageIdsToRequest() {
            when(requestExecutor.executeRequest(any(), eq(EmptyResponse.class)))
                    .thenReturn(CompletableFuture.completedFuture(new EmptyResponse()));

            DeletableMessage message1 = mock(DeletableMessage.class);
            when(message1.messageId()).thenReturn(10);
            DeletableMessage message2 = mock(DeletableMessage.class);
            when(message2.messageId()).thenReturn(20);

            messageService.deleteMessages(REGISTRATION, List.of(message1, message2)).join();

            verify(requestExecutor).executeRequest(requestCaptor.capture(), eq(EmptyResponse.class));
            var request = (DeleteMessagesRequest) requestCaptor.getValue();
            assertEquals(2, request.messages.size());
            assertEquals(10, request.messages.get(0).id);
            assertEquals(20, request.messages.get(1).id);
        }

        @Test
        void propagatesErrorResponse() {
            var errorResponse = new ErrorResponse();
            errorResponse.errorCode = 203;
            errorResponse.errorMessage = "Unable to find activation";

            when(requestExecutor.executeRequest(any(), eq(EmptyResponse.class)))
                    .thenReturn(CompletableFuture.failedFuture(new HttpException(errorResponse)));

            DeletableMessage message = mock(DeletableMessage.class);
            when(message.messageId()).thenReturn(1);

            var deleteMessageCall = messageService.deleteMessages(REGISTRATION, List.of(message));

            CompletionException e = assertThrows(CompletionException.class, deleteMessageCall::join);
            assertInstanceOf(HttpException.class, e.getCause());
            assertTrue(e.getCause().getMessage().contains("Unable to find activation"));
        }

        @Test
        void wrapsExecutorException() {
            when(requestExecutor.executeRequest(any(), eq(EmptyResponse.class)))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("network error")));

            DeletableMessage message = mock(DeletableMessage.class);
            when(message.messageId()).thenReturn(1);

            var deleteMessageCall = messageService.deleteMessages(REGISTRATION, List.of(message));

            CompletionException e = assertThrows(CompletionException.class, deleteMessageCall::join);
            assertEquals("network error", e.getCause().getMessage());
        }
    }
}
