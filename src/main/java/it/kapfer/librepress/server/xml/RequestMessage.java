package it.kapfer.librepress.server.xml;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RequestMessage {
    @JacksonXmlProperty(isAttribute = true)
    public int id;

    public RequestMessage(int id) {
        this.id = id;
    }
}
