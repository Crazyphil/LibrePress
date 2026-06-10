package it.kapfer.librepress.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.kapfer.librepress.server.exception.HttpException;
import it.kapfer.librepress.server.xml.Response;
import it.kapfer.librepress.server.xml.authentication.UsernamePasswordAuthentication;
import it.kapfer.librepress.server.xml.request.GetServicesRequest;
import it.kapfer.librepress.server.xml.response.GetServicesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.xml.XmlWriteFeature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestExecutorTest {
    private static final String SUCCESSFUL_GET_SERVICES_RESPONSE = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nd version="1.0">
                <versions>
                    <required-version>5.0.8.503</required-version>
                    <latest-version>6.20.1118</latest-version>
                </versions>
                <response id="0">
                    <service-name display-service-name="PressReader">PressDisplay.com</service-name>
                </response>
            </nd>
            """;
    private static final String ERROR_RESPONSE = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nd version="1.0">
                <versions>
                    <required-version>5.0.8.503</required-version>
                    <latest-version>6.20.1118</latest-version>
                </versions>
                <response id="0">
                    <error-code>401</error-code>
                    <error-message>The login name or password you entered is incorrect</error-message>
                </response>
            </nd>
            """;
    private static final String EMPTY_RESPONSE = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nd version="1.0">
                <versions>
                    <required-version>5.0.8.503</required-version>
                    <latest-version>6.20.1118</latest-version>
                </versions>
                <response id="0" />
            </nd>
            """;

    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpResponse<InputStream> httpResponse;
    @Captor
    private ArgumentCaptor<HttpRequest> httpRequestCaptor;

    private RequestExecutor requestExecutor;

    @BeforeEach
    void setUp() {
        requestExecutor = new RequestExecutor(httpClient, createXmlMapper());
    }

    @Test
    void sendsExpectedHttpRequest() throws Exception {
        givenSuccessfulHttpResponse(SUCCESSFUL_GET_SERVICES_RESPONSE);

        requestExecutor.executeRequest(getServicesRequest(), GetServicesResponse.class).join();

        verify(httpClient).sendAsync(httpRequestCaptor.capture(), any());
        HttpRequest httpRequest = httpRequestCaptor.getValue();
        assertEquals("POST", httpRequest.method());
        assertEquals("https://secure.newspaperdirect.com/epaper/services/DeliveryQueue.ashx", httpRequest.uri().toString());
        assertEquals(Duration.ofMinutes(1), httpRequest.timeout().orElseThrow());
        assertEquals("text/xml; charset=utf-8", httpRequest.headers().firstValue("Content-Type").orElseThrow());
        assertEquals("text/xml", httpRequest.headers().firstValue("Accept").orElseThrow());
        assertEquals(Constants.APPLICATION_STRING, httpRequest.headers().firstValue("User-Agent").orElseThrow());

        String body = bodyAsString(httpRequest);
        assertTrue(body.contains("<nd version=\"1.0\">"));
        assertTrue(body.contains("<client-info>"));
        assertTrue(body.contains("<software>ND Lite</software>"));
        assertTrue(body.contains("<version>6.20.1118.0</version>"));
        assertTrue(body.contains("<request"));
        assertTrue(body.contains("type=\"universal-register\""));
        assertTrue(body.contains("id=\"0\""));
        assertTrue(body.contains("<authentication>"));
        assertTrue(body.contains("<user-name>user@example.com</user-name>"));
        assertTrue(body.contains("<user-password>secret</user-password>"));
        assertTrue(body.contains("<client-number>0</client-number>"));
    }

    @Test
    void mapsSuccessfulXmlResponseToExpectedResponseType() {
        givenSuccessfulHttpResponse(SUCCESSFUL_GET_SERVICES_RESPONSE);

        GetServicesResponse response = requestExecutor.executeRequest(getServicesRequest(), GetServicesResponse.class).join();

        assertEquals(1, response.services.size());
        assertEquals("PressDisplay.com", response.services.getFirst().id);
        assertEquals("PressReader", response.services.getFirst().displayName);
    }

    @Test
    void mapsErrorXmlResponseToHttpException() {
        givenSuccessfulHttpResponse(ERROR_RESPONSE);

        var getServicesCall = requestExecutor.executeRequest(getServicesRequest(), GetServicesResponse.class);
        CompletionException e = assertThrows(CompletionException.class, getServicesCall::join);

        assertInstanceOf(HttpException.class, e.getCause());
        HttpException httpException = (HttpException) e.getCause();
        assertEquals("The login name or password you entered is incorrect", httpException.getErrorMessage());
        assertEquals(401, httpException.getErrorCode());
    }

    @Test
    void mapsEmptyXmlResponseToNullValue() {
        givenSuccessfulHttpResponse(EMPTY_RESPONSE);

        Response response = requestExecutor.executeRequest(getServicesRequest(), GetServicesResponse.class).join();

        assertNull(response, "Unexpected response type: should be null");
    }

    @Test
    void failsWhenHttpStatusIsNotOk() {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpResponse.body()).thenReturn(inputStream("server exploded"));
        //noinspection unchecked
        doReturn(CompletableFuture.completedFuture(httpResponse))
                .when(httpClient).sendAsync(any(HttpRequest.class), any(BodyHandler.class));

        var getServicesCall = requestExecutor.executeRequest(getServicesRequest(), GetServicesResponse.class);
        CompletionException e = assertThrows(CompletionException.class, getServicesCall::join);

        assertInstanceOf(HttpException.class, e.getCause());
        assertEquals("HTTP request failed with status code 500:\nserver exploded", e.getCause().getMessage());
    }

    @Test
    void failsWhenResponseXmlCannotBeParsed() {
        givenSuccessfulHttpResponse("<nd><response>");

        var getServicesCall = requestExecutor.executeRequest(getServicesRequest(), GetServicesResponse.class);
        CompletionException e = assertThrows(CompletionException.class, getServicesCall::join);

        assertInstanceOf(HttpException.class, e.getCause());
        assertEquals("Could not parse HTTP response as XML", e.getCause().getMessage());
    }

    @Test
    void failsWhenRequestCannotBeSerialized() {
        XmlMapper xmlMapper = mock(XmlMapper.class);
        when(xmlMapper.writeValueAsString(any())).thenThrow(new TestJacksonException("boom"));
        requestExecutor = new RequestExecutor(httpClient, xmlMapper);

        var getServicesCall = requestExecutor.executeRequest(getServicesRequest(), GetServicesResponse.class);
        CompletionException e = assertThrows(CompletionException.class, getServicesCall::join);

        assertInstanceOf(HttpException.class, e.getCause());
        assertEquals("Could not serialize request as XML", e.getCause().getMessage());
        verifyNoInteractions(httpClient);
    }

    @Test
    void propagatesHttpClientFailure() {
        RuntimeException connectionFailure = new RuntimeException("connection refused");
        //noinspection unchecked
        doReturn(CompletableFuture.failedFuture(connectionFailure))
                .when(httpClient).sendAsync(any(HttpRequest.class), any(BodyHandler.class));

        var getServicesCall = requestExecutor.executeRequest(getServicesRequest(), GetServicesResponse.class);
        CompletionException e = assertThrows(CompletionException.class, getServicesCall::join);

        assertSame(connectionFailure, e.getCause());
    }

    private void givenSuccessfulHttpResponse(String responseXml) {
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(inputStream(responseXml));
        //noinspection unchecked
        doReturn(CompletableFuture.completedFuture(httpResponse))
                .when(httpClient).sendAsync(any(HttpRequest.class), any(BodyHandler.class));
    }

    private static GetServicesRequest getServicesRequest() {
        return new GetServicesRequest(new UsernamePasswordAuthentication("user@example.com", "secret"));
    }

    private static ByteArrayInputStream inputStream(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }

    private static XmlMapper createXmlMapper() {
        return XmlMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(XmlWriteFeature.WRITE_XML_DECLARATION)
                .disable(XmlWriteFeature.WRITE_STANDALONE_YES_TO_XML_DECLARATION)
                .disable(XmlWriteFeature.WRITE_NULLS_AS_XSI_NIL)
                .changeDefaultPropertyInclusion(v -> JsonInclude.Value.ALL_NON_NULL)
                .build();
    }

    @Nested
    class ExecuteDownloadRequest {

        @Test
        void downloadsWithCorrectHeadersAndReturnsBody() {
            InputStream expectedBody = inputStream("pdf-content");
            when(httpResponse.statusCode()).thenReturn(200);
            when(httpResponse.body()).thenReturn(expectedBody);
            //noinspection unchecked
            doReturn(CompletableFuture.completedFuture(httpResponse))
                    .when(httpClient).sendAsync(any(HttpRequest.class), any(BodyHandler.class));

            InputStream result = requestExecutor.executeDownloadRequest(
                    List.of("http://mirror.example.com/file.pdf")).join();

            assertSame(expectedBody, result);
            verify(httpClient).sendAsync(httpRequestCaptor.capture(), any());
            HttpRequest request = httpRequestCaptor.getValue();
            assertEquals("GET", request.method());
            assertEquals("http://mirror.example.com/file.pdf", request.uri().toString());
            assertEquals("application/pdf", request.headers().firstValue("Accept").orElseThrow());
            assertEquals(Constants.APPLICATION_STRING, request.headers().firstValue("User-Agent").orElseThrow());
        }

        @Test
        void retriesSecondUrlWhenFirstFails() {
            InputStream expectedBody = inputStream("pdf-content");
            when(httpResponse.statusCode()).thenReturn(200);
            when(httpResponse.body()).thenReturn(expectedBody);

            // First call fails, second succeeds
            //noinspection unchecked
            doReturn(
                    CompletableFuture.failedFuture(new RuntimeException("first mirror down")),
                    CompletableFuture.completedFuture(httpResponse))
                    .when(httpClient).sendAsync(any(HttpRequest.class), any(BodyHandler.class));

            InputStream result = requestExecutor.executeDownloadRequest(
                    List.of("http://mirror1.example.com/file.pdf", "http://mirror2.example.com/file.pdf")).join();

            assertSame(expectedBody, result);
            verify(httpClient, times(2)).sendAsync(httpRequestCaptor.capture(), any());
            var allRequests = httpRequestCaptor.getAllValues();
            assertEquals("http://mirror1.example.com/file.pdf", allRequests.get(0).uri().toString());
            assertEquals("http://mirror2.example.com/file.pdf", allRequests.get(1).uri().toString());
        }

        @Test
        void failsWithHttpExceptionWhenUrlListIsEmpty() {
            var downloadCall = requestExecutor.executeDownloadRequest(List.of());
            CompletionException e = assertThrows(CompletionException.class, downloadCall::join);
            assertInstanceOf(HttpException.class, e.getCause());
            assertEquals("No download URLs available", e.getCause().getMessage());
        }
    }

    private static String bodyAsString(HttpRequest httpRequest) throws Exception {
        var subscriber = new StringBodySubscriber();
        httpRequest.bodyPublisher().orElseThrow().subscribe(subscriber);
        return subscriber.body.get(1, TimeUnit.SECONDS);
    }

    private static class StringBodySubscriber implements Flow.Subscriber<ByteBuffer> {
        private final ByteArrayOutputStream output = new ByteArrayOutputStream();
        private final CompletableFuture<String> body = new CompletableFuture<>();

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuffer item) {
            byte[] bytes = new byte[item.remaining()];
            item.get(bytes);
            output.writeBytes(bytes);
        }

        @Override
        public void onError(Throwable throwable) {
            body.completeExceptionally(throwable);
        }

        @Override
        public void onComplete() {
            body.complete(output.toString(StandardCharsets.UTF_8));
        }
    }

    private static class TestJacksonException extends JacksonException {
        private TestJacksonException(String message) {
            super(message);
        }
    }
}
