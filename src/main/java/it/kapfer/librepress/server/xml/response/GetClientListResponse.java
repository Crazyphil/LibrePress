package it.kapfer.librepress.server.xml.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import it.kapfer.librepress.server.xml.Response;

import java.util.List;

/**
 * This class is a Jackson object representation of the {@code response} element in this XML structure:
 * <pre>
 *     <response id="0">
 *         <activations>
 *             <activation id="12345678" client-name="CLIENT-1" activation-number="384a82ea-438f-4f0f-b727-181708d12219" />
 *             <activation id="98765432" client-name="CLIENT-2" activation-number="f6a843e8-0df6-4fb0-a7bc-684c61ab766d" />
 *         </activations>
 *     </response>
 * </pre>
 */
public class GetClientListResponse extends Response {
    @JacksonXmlElementWrapper(localName = "activations")
    public Activations activations;

    public static class Activations {
        @JacksonXmlProperty(localName = "activation")
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public List<Activation> activations;
    }

    public static class Activation {
        @JacksonXmlProperty(isAttribute = true)
        public Integer id;

        @JacksonXmlProperty(isAttribute = true, localName = "client-name")
        public String clientName;

        @JacksonXmlProperty(isAttribute = true, localName = "activation-number")
        public String activationNumber;
    }
}
