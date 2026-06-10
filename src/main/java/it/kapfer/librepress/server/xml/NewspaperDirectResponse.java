package it.kapfer.librepress.server.xml;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * This class is a Jackson object representation of the {@code nd} element in this XML structure:
 * <pre>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <nd version="1.0">
 *   <versions>
 *     <required-version>5.0.8.503</required-version>
 *     <latest-version>6.20.1118</latest-version>
 *   </versions>
 *   <response id="0">
 *     <!-- ... -->
 *   </response>
 *   <user-profile>
 *     <status>1</status>
 *     <print-options>2</print-options>
 *     <first-name>Libre</first-name>
 *     <last-name>Press</last-name>
 *     <user-name>user@example.com</user-name>
 *     <logon-name>user@example.com</logon-name>
 *     <account-number>110101100</account-number>
 *   </user-profile>
 * </nd>
 * </pre>
 */
@JsonRootName("nd")
public class NewspaperDirectResponse {
    @JacksonXmlProperty(isAttribute = true)
    public String version;

    public Versions versions;

    public Response response;

    @JacksonXmlProperty(localName = "user-profile")
    public UserProfile userProfile;

    public Response getResponse() {
        return response;
    }

    public static class Versions {
        @JacksonXmlProperty(localName = "required-version")
        public String requiredVersion;

        @JacksonXmlProperty(localName = "latest-version")
        public String latestVersion;
    }

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
