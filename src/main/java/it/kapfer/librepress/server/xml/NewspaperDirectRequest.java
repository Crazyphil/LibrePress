package it.kapfer.librepress.server.xml;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import it.kapfer.librepress.server.Constants;

/**
 * This class is a Jackson object representation of the {@code nd} element in this XML structure:
 * <pre>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <nd version='1.0'>
 *   <client-info>
 *     <software>ND Lite</software>
 *     <version>6.20.1118.0</version>
 *     <client-type>PC</client-type>
 *     <default-service-name>PressDisplay.com</default-service-name>
 *     <system-manufacturer>Microsoft Corporation</system-manufacturer>
 *     <system-model>Virtual Machine</system-model>
 *     <installkey1></installkey1>
 *     <installkey2></installkey2>
 *   </client-info>
 *   <request type='get-base-urls' id='0'></request>
 * </nd>
 * </pre>
 */
@JsonRootName("nd")
public class NewspaperDirectRequest {
    @JacksonXmlProperty(isAttribute = true)
    public final String version = "1.0";

    @JacksonXmlProperty(localName = "client-info")
    public ClientInfo clientInfo;

    public final Request request;

    public NewspaperDirectRequest(Request request) {
        this.clientInfo = new ClientInfo();
        this.request = request;
    }

    public static class ClientInfo {
        public final String software = Constants.APPLICATION_STRING;

        public final String version = Constants.APPLICATION_VERSION;

        @JacksonXmlProperty(localName = "client-type")
        public String clientType;

        @JacksonXmlProperty(localName = "default-service-name")
        public String defaultServiceName;

        @JacksonXmlProperty(localName = "system-manufacturer")
        public String systemManufacturer;

        @JacksonXmlProperty(localName = "system-model")
        public String systemModel;

        @JacksonXmlProperty(localName = "installkey1")
        public String installKey1;

        @JacksonXmlProperty(localName = "installkey2")
        public String installKey2;
    }
}
