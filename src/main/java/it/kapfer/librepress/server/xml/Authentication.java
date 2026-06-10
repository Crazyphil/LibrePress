package it.kapfer.librepress.server.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Authentication {
    @JacksonXmlProperty(localName = "user-name")
    public String userName;
    @JacksonXmlProperty(localName = "user-password")
    public String userPassword;
    @JacksonXmlProperty(localName = "client-number")
    public Integer clientNumber;
    @JacksonXmlProperty(localName = "client-id")
    public String clientId;
    @JacksonXmlProperty(localName = "device-id")
    public Integer deviceId;
    @JacksonXmlProperty(localName = "activation-number")
    public String activationNumber;

    protected Authentication() {
    }
}