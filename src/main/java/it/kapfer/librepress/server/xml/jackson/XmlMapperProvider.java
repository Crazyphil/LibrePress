package it.kapfer.librepress.server.xml.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class XmlMapperProvider {
    private XmlMapperProvider() {
        // utility class
    }

    public static XmlMapper createXmlMapper() {
        return XmlMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .disable(ToXmlGenerator.Feature.WRITE_STANDALONE_YES_TO_XML_DECLARATION)
                .disable(ToXmlGenerator.Feature.WRITE_NULLS_AS_XSI_NIL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .defaultPropertyInclusion(JsonInclude.Value.ALL_NON_NULL)
                .addModule(new JavaTimeModule())
                .build();
    }
}
