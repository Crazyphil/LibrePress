package it.kapfer.librepress.server.xml;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import it.kapfer.librepress.server.xml.jackson.ResponseTypeResolverBuilder;
import it.kapfer.librepress.server.xml.response.*;
import tools.jackson.databind.annotation.JsonTypeResolver;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonTypeInfo(use = Id.CUSTOM)
@JsonTypeResolver(ResponseTypeResolverBuilder.class)
@JsonSubTypes({
        @Type(EmptyResponse.class),
        @Type(ErrorResponse.class),
        @Type(GetClientListResponse.class),
        @Type(GetServicesResponse.class),
        @Type(RegisterClientResponse.class)
})
public abstract class Response {
    @JacksonXmlProperty(isAttribute = true)
    public int id;
}
