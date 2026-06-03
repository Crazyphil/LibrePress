package it.kapfer.librepress.server.xml.request;

import it.kapfer.librepress.server.xml.Request;
import it.kapfer.librepress.server.xml.authentication.ActivationAuthentication;

public class GetClientListRequest extends Request {
    public GetClientListRequest(ActivationAuthentication authentication) {
        super("get-client-list", authentication);
    }
}
