package it.kapfer.librepress.server.xml.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import it.kapfer.librepress.server.xml.Response;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.List;

/**
 * This class is a Jackson object representation of the {@code response} element in this XML structure:
 * <pre>
 *     <response id="0">
 *         <activation-number>9b1869f4-6c13-47e8-bad3-7f7c14d7d4b5</activation-number>
 *         <service-name>PressDisplay.com</service-name>
 *         <display-service-name>PressReader</display-service-name>
 *         <service-url>http://www.pressdisplay.com/</service-url>
 *         <baseapplication-url>http://www.pressdisplay.com/pressdisplay/</baseapplication-url>
 *         <activation-id>192384756</activation-id>
 *         <user-profile>
 *             <status>1</status>
 *             <print-options>2</print-options>
 *             <first-name>Libre</first-name>
 *             <last-name>Press</last-name>
 *             <user-name>user@example.com</user-name>
 *             <logon-name>user@example.com</logon-name>
 *             <account-number>110101100</account-number>
 *         </user-profile>
 *     </response>
 * </pre>
 */
public class GetServicesResponse extends Response {
    @JacksonXmlProperty(localName = "service-name")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonFormat(with = Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<ServiceName> services;

    public static class ServiceName {
        @JacksonXmlText
        public String id;
        @JacksonXmlProperty(isAttribute = true, localName = "display-service-name")
        public String displayName;
    }
}
