package it.kapfer.librepress.integration;

import it.kapfer.librepress.server.DeletableMessage;
import it.kapfer.librepress.server.DeviceRegistration;
import it.kapfer.librepress.server.MessageService;
import it.kapfer.librepress.server.NewspaperActivation;
import it.kapfer.librepress.server.exception.HttpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MessageServiceIntegrationTest {
    private static final DeviceRegistration VALID_REGISTRATION = SecretsProvider.get().getDeviceRegistration();
    private static final DeviceRegistration INVALID_REGISTRATION = new DeviceRegistration("1d35fd1b-6535-438e-87f2-eebb213ecb2c", 987654321);

    private MessageService messageService;

    @BeforeEach
    void beforeEach() {
        messageService = MessageService.createService();
    }

    /*
     * Response:
     * <pre>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <nd version="1.0">
     *     <versions>
     *         <required-version>5.0.8.503</required-version>
     *         <latest-version>6.20.1118</latest-version>
     *     </versions>
     *     <response id="0">
     *         <error-code>203</error-code>
     *         <error-message>Unable to find activation</error-message>
     *         <error-help-url />
     *     </response>
     * </nd>
     * </pre>
     */
    @Test
    void getPushedNewspapers_withInvalidRegistration_throwsException() {
        var newspaperDownloadRequestsRequest = messageService.getPushedNewspapers(INVALID_REGISTRATION);

        CompletionException e = assertThrows(CompletionException.class, newspaperDownloadRequestsRequest::join);

        assertInstanceOf(HttpException.class, e.getCause());
        HttpException httpException = (HttpException) e.getCause();
        assertEquals("Unable to find activation", httpException.getErrorMessage());
    }

    /*
     * Response:
     * <pre>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <nd version="1.0">
     *     <versions>
     *         <required-version>5.10.0426.0</required-version>
     *         <latest-version>6.20.1118</latest-version>
     *     </versions>
     *     <response id="0">
     *         <message id="12345678" type="newspaper" sent-time="01/06/2026 12:34:56">
     *             <issue-id>sfdy2019021000000000001001</issue-id>
     *             <title>Brain Games</title>
     *             <pages>28</pages>
     *             <enable-smart>false</enable-smart>
     *             <country>United States</country>
     *             <language>English</language>
     *             <country-code>us</country-code>
     *             <language-code>en</language-code>
     *             <get-license-url>https://secure.newspaperdirect.com/dq/services/content-activation/activate?issue=sfdy2019021000000000001001&amp;certificateid=12345678</get-license-url>
     *             <get-license-url2>https://secure.newspaperdirect.com/dq/services/content-activation/activate2?issue=sfdy2019021000000000001001&amp;certificateid=12345678</get-license-url2>
     *             <get-license-url3>https://secure.newspaperdirect.com/dq/services/content-activation/activate3?issue=sfdy2019021000000000001001&amp;certificateid=12345678</get-license-url3>
     *             <sound-disabled>0</sound-disabled>
     *             <smartflow-disabled>0</smartflow-disabled>
     *             <replica-disabled>0</replica-disabled>
     *             <bookmarks-restricted>0</bookmarks-restricted>
     *             <page-printing-disabled>0</page-printing-disabled>
     *             <article-printing-disabled>0</article-printing-disabled>
     *             <comments-disabled>0</comments-disabled>
     *             <article-email-sharing-disabled>0</article-email-sharing-disabled>
     *             <diggit-disabled>0</diggit-disabled>
     *             <delicious-disabled>0</delicious-disabled>
     *             <facebook-disabled>0</facebook-disabled>
     *             <twitter-disabled>0</twitter-disabled>
     *             <livejournal-disabled>0</livejournal-disabled>
     *             <wordpress-disabled>0</wordpress-disabled>
     *             <screenshot-disabled>0</screenshot-disabled>
     *             <translation-disabled>0</translation-disabled>
     *             <translation-disabled-by-publisher>0</translation-disabled-by-publisher>
     *             <copy-article-disabled>0</copy-article-disabled>
     *             <instapaper-disabled>0</instapaper-disabled>
     *             <evernote-disabled>0</evernote-disabled>
     *             <onenote-disabled>0</onenote-disabled>
     *             <vote-disabled>0</vote-disabled>
     *             <expiration-date>2292-12-31</expiration-date>
     *         </message>
     *     </response>
     *     <user-profile>
     *         <status>1</status>
     *         <print-options>2</print-options>
     *         <first-name>Libre</first-name>
     *         <last-name>Press</last-name>
     *         <user-name>user@example.com</user-name>
     *         <logon-name>user@example.com</logon-name>
     *         <account-number>110101100</account-number>
     *     </user-profile>
     * </nd>
     * </pre>
     */
    @Test
    void getPushedNewspapers_withValidRegistration_returnsRequest() {
        List<NewspaperActivation> newspaperActivations = messageService.getPushedNewspapers(VALID_REGISTRATION)
                .join();

        assertEquals(1, newspaperActivations.size());
        NewspaperActivation newspaperProvider = newspaperActivations.getFirst();
        assertEquals("Brain Games", newspaperProvider.title());
        assertEquals("sfdy2019021000000000001001", newspaperProvider.issueId());
        assertFalse(newspaperProvider.licenseUrl().isBlank());
    }

    /*
     * Response:
     * <pre>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <nd version="1.0">
     *     <versions>
     *         <required-version>5.0.8.503</required-version>
     *         <latest-version>6.20.1118</latest-version>
     *     </versions>
     *     <response id="0">
     *         <error-code>203</error-code>
     *         <error-message>Unable to find activation</error-message>
     *         <error-help-url />
     *     </response>
     * </nd>
     * </pre>
     */
    @Test
    void deleteMessages_withInvalidRegistration_throwsException() {
        DeletableMessage message = mock(DeletableMessage.class);
        when(message.messageId()).thenReturn(1);

        var deleteMessageRequest = messageService.deleteMessages(INVALID_REGISTRATION, List.of(message));
        CompletionException e = assertThrows(CompletionException.class, deleteMessageRequest::join);

        assertInstanceOf(HttpException.class, e.getCause());
        HttpException httpException = (HttpException) e.getCause();
        assertTrue(httpException.getErrorMessage().contains("Unable to find activation"));
    }

    /*
     * Response
     * <pre>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <nd version="1.0">
     *     <versions>
     *         <required-version>5.10.0426.0</required-version>
     *         <latest-version>6.20.1118</latest-version>
     *     </versions>
     *     <response id="0" />
     * </nd>
     * </pre>
     */
    @Test
    void deleteMessages_withValidRegistration_returnsEmptyResponse() {
        DeletableMessage message = mock(DeletableMessage.class);
        when(message.messageId()).thenReturn(1);

        var deleteMessageRequest = messageService.deleteMessages(VALID_REGISTRATION, List.of(message));
        assertDoesNotThrow(deleteMessageRequest::join);
    }
}
