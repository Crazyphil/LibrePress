package it.kapfer.librepress.server.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RequestMessage {
    @JacksonXmlProperty(isAttribute = true)
    public int id;

    public RequestMessage(int id) {
        this.id = id;
    }
}
