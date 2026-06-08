package it.kapfer.librepress.server;

import tools.jackson.dataformat.xml.XmlMapper;

import java.net.http.HttpClient;

public class MockRequestExecutor extends RequestExecutor {
    public MockRequestExecutor(HttpClient httpClient, XmlMapper xmlMapper) {
        super(httpClient, xmlMapper);
    }
}
