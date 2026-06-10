package it.kapfer.librepress.server.xml;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import it.kapfer.librepress.server.xml.message.NewspaperMessage;
import it.kapfer.librepress.server.xml.message.NewspaperUpdateMessage;

import java.time.LocalDateTime;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = NewspaperMessage.class, name = "newspaper"),
        @Type(value = NewspaperUpdateMessage.class, name = "newspaper-update")
})
public abstract class ResponseMessage {
    @JacksonXmlProperty(isAttribute = true)
    public int id;

    @JacksonXmlProperty(isAttribute = true)
    public String type;

    @JacksonXmlProperty(isAttribute = true, localName = "sent-time")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    public LocalDateTime sentTime;
}
