package it.kapfer.librepress.server.xml.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import it.kapfer.librepress.server.xml.Request;
import it.kapfer.librepress.server.xml.authentication.UsernameActivationAuthentication;

public class UnregisterClientRequest extends Request {
    @JacksonXmlProperty(localName = "client-name")
    public String clientName;

    public UnregisterClientRequest(UsernameActivationAuthentication authentication) {
        super("unregister", authentication);
    }
}
