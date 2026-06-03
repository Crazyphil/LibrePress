package it.kapfer.librepress.server.xml.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.kapfer.librepress.server.xml.Response;
import it.kapfer.librepress.server.xml.ResponseMessage;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * This class is a Jackson object representation of the {@code response} element in this XML structure:
 * <pre>
 *     <response id="0">
 *         <message id="12345678" type="newspaper" sent-time="01/06/2026 12:34:56">
 *             <!-- ... -->
 *         </message>
 *     </response>
 * </pre>
 */
public class GetMessagesResponse extends Response {
    @JacksonXmlProperty(localName = "message")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<ResponseMessage> messages;
}
