package it.kapfer.librepress.integration;

import it.kapfer.librepress.integration.httpclient.DelegatingHttpClient;
import it.kapfer.librepress.integration.httpclient.FixedResponseHttpClient;
import it.kapfer.librepress.server.*;
import it.kapfer.librepress.server.exception.NewspaperActivationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class ActivationServiceIntegrationTest {
    private static final DeviceRegistration VALID_REGISTRATION = SecretsProvider.get().getDeviceRegistration();
    private static final DeviceRegistration INVALID_REGISTRATION = new DeviceRegistration("1d35fd1b-6535-438e-87f2-eebb213ecb2c", 987654321);
    private static final String LICENSE_URL = SecretsProvider.get().getLicenseUrl();
    private static final NewspaperActivation NEWSPAPER_ACTIVATION = new NewspaperActivation(1234, "Brain Games", "sfdy2019021000000000001001", LICENSE_URL);

    private ActivationService activationService;

    @BeforeEach
    void beforeEach() {
        activationService = ActivationService.createService();
    }

    /*
     * Response:
     * <pre>
     * <?xml version="1.0" encoding="utf-8"?>
     * <ActivationResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://tempuri.org/">
     *     <GotoUrl>http://www.pressdisplay.com/pressdisplay/viewer.aspx</GotoUrl>
     *     <IssueId>0</IssueId>
     *     <StatusCode>AuthenticationFailed</StatusCode>
     * </ActivationResponse>
     * </pre>
     */
    @Test
    void getPushedNewspapers_withInvalidRegistration_throwsException() {
        var activatePushedNewspaperRequest = activationService.activatePushedNewspaper(INVALID_REGISTRATION, NEWSPAPER_ACTIVATION);
        CompletionException e = assertThrows(CompletionException.class, activatePushedNewspaperRequest::join);

        assertInstanceOf(NewspaperActivationException.class, e.getCause());
        NewspaperActivationException cause = (NewspaperActivationException) e.getCause();
        assertEquals("AuthenticationFailed", cause.getErrorCode());
    }

    /*
     * Response:
     * <pre>
     * <?xml version="1.0" encoding="utf-8"?>
     * <ActivationResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://tempuri.org/">
     *     <CertificateId>57d04954-d9a2-4451-aee4-636d3367deec</CertificateId>
     *     <DownloadUrls>
     *         <Url>http://cdn.ndcds.net/cds/files?***</Url>
     *         <Url>http://cdn.ndcds.net/cds/files?***</Url>
     *         <Url>http://cds212.ndcds.net/cds/files?***</Url>
     *         <Url>http://cds211.ndcds.net/cds/files?***</Url>
     *         <Url>http://cds202.ndcds.net/cds/files?***</Url>
     *     </DownloadUrls>
     *     <EncryptionType>2</EncryptionType>
     *     <ExpirationDate>2053-10-18</ExpirationDate>
     *     <ExpirationTime>2053-10-18T00:00:00.000Z</ExpirationTime>
     *     <ExpungeVersion>0</ExpungeVersion>
     *     <GotoUrl>http://www.pressdisplay.com/pressdisplay/viewer.aspx</GotoUrl>
     *     <Issue>sfdy2019021000000000001001</Issue>
     *     <IssueId>5990991</IssueId>
     *     <LayoutVersion>0</LayoutVersion>
     *     <StatusCode>ServerError</StatusCode>
     *     <ThumbnailUrls>
     *         <Url>https://t.prcdn.co/img</Url>
     *     </ThumbnailUrls>
     *     <ThumbnailUrlsByHeight>
     *         <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds212.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds211.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds202.ndcds.net/cds/files?thumbs%253a***</Url>
     *     </ThumbnailUrlsByHeight>
     *     <ThumbnailUrlsByWidth>
     *         <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds212.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds211.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds202.ndcds.net/cds/files?thumbs%253a***</Url>
     *     </ThumbnailUrlsByWidth>
     *     <UrlExpirationTime>2026-07-02 23:59:59</UrlExpirationTime>
     *     <UrlTTL>43885</UrlTTL>
     *     <ZoomUrls>
     *         <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds211.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds212.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds202.ndcds.net/cds/files?thumbs%253a***</Url>
     *     </ZoomUrls>
     *     <ZoomUrls2>
     *         <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds212.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds211.ndcds.net/cds/files?thumbs%253a***</Url>
     *         <Url>http://cds202.ndcds.net/cds/files?thumbs%253a***</Url>
     *     </ZoomUrls2>
     * </ActivationResponse>
     * </pre>
     */
    @Test
    void getPushedNewspapers_withValidRegistrationAndAlreadyActivatedIssue_throwsException() {
        var activatePushedNewspaperRequest = activationService.activatePushedNewspaper(VALID_REGISTRATION, NEWSPAPER_ACTIVATION);
        CompletionException e = assertThrows(CompletionException.class, activatePushedNewspaperRequest::join);

        assertInstanceOf(NewspaperActivationException.class, e.getCause());
        NewspaperActivationException cause = (NewspaperActivationException) e.getCause();
        assertEquals("ServerError", cause.getErrorCode());
    }

    @Nested
    class WithValidCertificate {

        private DelegatingHttpClient httpClientDelegate;

        @BeforeEach
        void beforeEach() {
            httpClientDelegate = new DelegatingHttpClient(null);
            MockRequestExecutor mockRequestExecutor = new MockRequestExecutor(httpClientDelegate, XmlMapper.shared());
            activationService = ActivationServiceTestInitializer.withCustomRequestExecutor(mockRequestExecutor);
        }

        @ParameterizedTest
        @ValueSource(strings = {"brain-games", "guardian-weekly-2021-30"})
        void testRealActivation(String testParameterFilePrefix) throws IOException {
            try (InputStream propertiesFile = getClass().getResourceAsStream(testParameterFilePrefix + ".properties")) {
                try (InputStream activationFile = getClass().getResourceAsStream(testParameterFilePrefix + ".activation")) {
                    assumeFalse(propertiesFile == null, "Properties file not found in resources");
                    assumeFalse(activationFile == null, "Activation file not found in resources");

                    Properties properties = new Properties();
                    properties.load(propertiesFile);
                    executeRealActivation(properties, activationFile);
                }
            }
        }

        void executeRealActivation(Properties testProperties, InputStream activationFile) throws IOException {
            byte[] responseBodyBytes = activationFile.readAllBytes();
            HttpHeaders httpHeaders = HttpHeaders.of(Map.of(), (k, v) -> true);
            HttpClient httpClient = FixedResponseHttpClient.createClientFrom(HttpClient.newBuilder(), 200, httpHeaders, responseBodyBytes);
            httpClientDelegate.setHttpClient(httpClient);
            DeviceRegistration deviceRegistration = new DeviceRegistration(UUID.randomUUID().toString(), Integer.parseInt(testProperties.getProperty("clientNumber")));
            NewspaperActivation newspaperActivation = new NewspaperActivation(0, "Test Issue", "2026061000000000001001", LICENSE_URL);

            NewspaperIssue newspaperIssue = activationService.activatePushedNewspaper(deviceRegistration, newspaperActivation).join();

            assertEquals(testProperties.getProperty("title"), newspaperIssue.title());
            assertEquals(testProperties.getProperty("issue"), newspaperIssue.issue());
            assertEquals(testProperties.getProperty("issue"), newspaperIssue.issue());
            assertEquals(testProperties.getProperty("downloadUrl"), newspaperIssue.downloadUrls().getFirst());
            assertEquals(LocalDateTime.parse(testProperties.getProperty("urlExpirationTime")), newspaperIssue.urlExpirationTime());
            assertArrayEquals(Base64.getDecoder().decode(testProperties.getProperty("encryptionKey")), newspaperIssue.encryptionKey());
        }

        private void assertArrayEquals(byte[] expected, byte[] actual) {
            assertEquals(expected.length, actual.length, "Arrays have different lengths");
            for (int i = 0; i < expected.length; i++) {
                assertEquals(expected[i], actual[i], "Arrays differ at index " + i);
            }
        }
    }
}
