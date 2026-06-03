package it.kapfer.librepress.server.xml.request;

import it.kapfer.librepress.server.xml.Request;
import it.kapfer.librepress.server.xml.authentication.ActivationAuthentication;

public class GetMessagesRequest extends Request {
    public GetMessagesRequest(ActivationAuthentication authentication) {
        super("get-messages", authentication);
    }
}
