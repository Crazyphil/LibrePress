package it.kapfer.librepress.server.xml.request;

import it.kapfer.librepress.server.xml.Request;
import it.kapfer.librepress.server.xml.authentication.UsernamePasswordClientAuthentication;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RegisterClientRequest extends Request {
    @JacksonXmlProperty(localName = "service-name")
    public String serviceName;

    @JacksonXmlProperty(localName = "client-name")
    public String clientName;
    
    @JacksonXmlProperty(localName = "resend-issues")
    public int resendIssues = 0;

    public RegisterClientRequest(UsernamePasswordClientAuthentication authentication, String serviceName, String clientName) {
        super("universal-register", authentication);

        this.serviceName = serviceName;
        this.clientName = clientName;
    }
}
