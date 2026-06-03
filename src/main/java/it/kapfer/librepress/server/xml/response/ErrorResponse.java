package it.kapfer.librepress.server.xml.response;

import it.kapfer.librepress.server.xml.Response;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * This class represents an error response for a command that failed to execute (but the HTTP server returned code 200 regardless).
 * <p>
 * It is a Jackson object representation of the {@code response} element in this XML structure:
 * <pre>
 *     <response id="0">
 *         <error-code>401</error-code>
 *         <error-message>The login name or password you entered is incorrect</error-message>
 *         <error-help-url />
 *     </response>
 * </pre>
 */
public class ErrorResponse extends Response {
    @JacksonXmlProperty(localName = "error-code")
    public Integer errorCode;
    @JacksonXmlProperty(localName = "error-message")
    public String errorMessage;
    @JacksonXmlProperty(localName = "error-help-url")
    public String errorHelpUrl;
}
