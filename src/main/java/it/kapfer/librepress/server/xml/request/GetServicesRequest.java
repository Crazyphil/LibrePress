package it.kapfer.librepress.server.xml.request;

import it.kapfer.librepress.server.xml.Request;
import it.kapfer.librepress.server.xml.authentication.UsernamePasswordAuthentication;

public class GetServicesRequest extends Request {
    public GetServicesRequest(UsernamePasswordAuthentication authentication) {
        super("universal-register", authentication);

        // This request intentionally doesn't use UsernamePasswordClientAuthentication, even if it needs a client number, because the client number is
        // effectively disregarded
        this.authentication.clientNumber = 0;
    }
}
