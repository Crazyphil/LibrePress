package it.kapfer.librepress.server.xml.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import it.kapfer.librepress.server.xml.Request;
import it.kapfer.librepress.server.xml.RequestMessage;
import it.kapfer.librepress.server.xml.authentication.ActivationAuthentication;

import java.util.List;

public class DeleteMessagesRequest extends Request {
    @JacksonXmlProperty(localName = "message")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<RequestMessage> messages;

    public DeleteMessagesRequest(ActivationAuthentication authentication, List<RequestMessage> messages) {
        super("delete-messages", authentication);

        this.messages = messages;
    }
}
