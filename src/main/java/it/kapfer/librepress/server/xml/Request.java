package it.kapfer.librepress.server.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public abstract class Request {
    @JacksonXmlProperty(isAttribute = true)
    public String type;

    @JacksonXmlProperty(isAttribute = true)
    public int id = 0;

    public final Authentication authentication;

    protected Request(String type, Authentication authentication) {
        this.type = type;
        this.authentication = authentication;
    }
}