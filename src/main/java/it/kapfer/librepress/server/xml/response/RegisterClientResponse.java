package it.kapfer.librepress.server.xml.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.kapfer.librepress.server.xml.Response;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * This class is a Jackson object representation of the {@code response} element in this XML structure:
 * <pre>
 * <response id="0">
 * 		<activation-number>2965db3f-1893-4c04-a666-35f190a6a1fc</activation-number>
 * 		<service-name>PressDisplay.com</service-name>
 * 		<display-service-name>PressReader</display-service-name>
 * 		<service-url>http://www.pressdisplay.com/</service-url>
 * 		<baseapplication-url>http://www.pressdisplay.com/pressdisplay/</baseapplication-url>
 * 		<activation-id>12345678</activation-id>
 * 		<user-profile>
 * 			<status>1</status>
 * 			<print-options>2</print-options>
 * 			<first-name>User</first-name>
 * 			<last-name>Name</last-name>
 * 			<user-name>user@example.com</user-name>
 * 			<logon-name>user@example.com</logon-name>
 * 			<account-number>98765432</account-number>
 * 		</user-profile>
 * </response>
 * </pre>
 */
public class RegisterClientResponse extends Response {
    @JsonInclude
    @JacksonXmlProperty(localName = "activation-number")
    public String activationNumber;

    @JacksonXmlProperty(localName = "service-name")
    public String serviceName;

    @JacksonXmlProperty(localName = "display-service-name")
    public String displayServiceName;

    @JacksonXmlProperty(localName = "service-url")
    public String serviceUrl;

    @JacksonXmlProperty(localName = "baseapplication-url")
    public String baseApplicationUrl;

    @JacksonXmlProperty(localName = "activation-id")
    public String activationId;

    @JacksonXmlProperty(localName = "user-profile")
    public UserProfile userProfile;

    public static class UserProfile {
        public int status;

        @JacksonXmlProperty(localName = "print-options")
        public int printOptions;

        @JacksonXmlProperty(localName = "first-name")
        public String firstName;

        @JacksonXmlProperty(localName = "last-name")
        public String lastName;

        @JacksonXmlProperty(localName = "user-name")
        public String userName;

        @JacksonXmlProperty(localName = "logon-name")
        public String logonName;

        @JacksonXmlProperty(localName = "account-number")
        public String accountNumber;
    }
}
