package it.kapfer.librepress.server;

import com.fasterxml.jackson.annotation.JsonInclude.Value;
import it.kapfer.librepress.server.exception.HttpException;
import it.kapfer.librepress.server.xml.*;
import it.kapfer.librepress.server.xml.response.EmptyResponse;
import it.kapfer.librepress.server.xml.response.ErrorResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.xml.XmlWriteFeature;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

class RequestExecutor {
    private static final URI ENDPOINT_URL = URI.create("https://secure.newspaperdirect.com/epaper/services/DeliveryQueue.ashx");

    private final HttpClient client;
    private final XmlMapper xmlMapper;

    public RequestExecutor() {
        this(HttpClient.newBuilder().build(), createXmlMapper());
    }

    RequestExecutor(HttpClient client, XmlMapper xmlMapper) {
        this.client = client;
        this.xmlMapper = xmlMapper;
    }

    private static XmlMapper createXmlMapper() {
        return XmlMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(XmlWriteFeature.WRITE_XML_DECLARATION)
                .disable(XmlWriteFeature.WRITE_STANDALONE_YES_TO_XML_DECLARATION)
                .disable(XmlWriteFeature.WRITE_NULLS_AS_XSI_NIL)
                .changeDefaultPropertyInclusion(v -> Value.ALL_NON_NULL)
                .build();
    }

    public <T extends Response> CompletableFuture<T> executeRequest(Request request, Class<T> expectedResponseType) {
        NewspaperDirectRequest ndRequest = new NewspaperDirectRequest(request);

        String xml;
        try {
            xml = xmlMapper.writeValueAsString(ndRequest);
        } catch (JacksonException e) {
            return CompletableFuture.failedFuture(new HttpException("Could not serialize request as XML", e));
        }

        HttpRequest httpRequest = prepareHttpRequest(ENDPOINT_URL)
                .POST(BodyPublishers.ofString(xml))
                .build();

        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(this::checkStatusCode)
                .thenApply(HttpResponse::body)
                .thenApply(b -> parseResponse(b, NewspaperDirectResponse.class))
                .thenApply(NewspaperDirectResponse::getResponse)
                .thenApply(r -> mapToResponse(expectedResponseType, r));

    }

    private HttpRequest.Builder prepareHttpRequest(URI url) {
        return HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("Accept", "text/xml")
                .header("User-Agent", Constants.APPLICATION_STRING);
    }

    private HttpResponse<InputStream> checkStatusCode(HttpResponse<InputStream> httpResponse) {
        if (httpResponse.statusCode() == 200) {
            return httpResponse;
        }

        try (var bodyStream = httpResponse.body()) {
            String body = new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8);
            throw new HttpException("HTTP request failed with status code " + httpResponse.statusCode() + ":\n" + body);
        } catch (IOException e) {
            throw new HttpException("Could not evaluate HTTP response", e);
        }
    }

    private <T> T parseResponse(InputStream response, Class<T> expectedType) {
        try {
            return xmlMapper.readValue(response, expectedType);
        } catch (JacksonException e) {
            throw new HttpException("Could not parse HTTP response as XML", e);
        }
    }

    private <T extends Response> T mapToResponse(Class<T> responseType, Response response) {
        if (responseType.isInstance(response)) {
            return (T) response;
        } else if (response instanceof ErrorResponse errorResponse) {
            throw new HttpException(errorResponse);
        } else if (response instanceof EmptyResponse) {
            return null;
        } else {
            throw new HttpException("Unexpected response type: " + response.getClass().getSimpleName() + " (expected " + responseType.getSimpleName() + ")");
        }
    }

    public CompletableFuture<ActivationResponse> executeActivationRequest(String activationUrl, String activationNumber, int clientNumber) {
        String actualUrl = activationUrl + "&activationNumber=" + activationNumber + "&ClientNumber=" + clientNumber;
        HttpRequest httpRequest = prepareHttpRequest(URI.create(actualUrl))
                .GET()
                .build();

        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(this::checkStatusCode)
                .thenApply(HttpResponse::body)
                .thenApply(b -> parseResponse(b, ActivationResponse.class));
    }
}
