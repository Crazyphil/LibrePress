package it.kapfer.librepress.server.xml.request;

import it.kapfer.librepress.server.xml.Request;
import it.kapfer.librepress.server.xml.authentication.UsernameActivationAuthentication;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class UnregisterClientRequest extends Request {
    @JacksonXmlProperty(localName = "client-name")
    public String clientName;

    public UnregisterClientRequest(UsernameActivationAuthentication authentication) {
        super("unregister", authentication);
    }
}
