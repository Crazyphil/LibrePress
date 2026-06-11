package it.kapfer.librepress.drm.xml;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This class is a Jackson object representation of the {@code documentInfo} element in this XML structure:
 * <pre>
 * <documentInfo version="1" documentId="sfdy2019021000000000001001" activationId="58f4f9dc-2825-4037-8f79-e1cd183096ef">
 *   <encryption cryptVersion="2" encryptionKey="dGVzdA=="/>
 *   <permissions docPermissions="286" pagePermissions="0" linkPermissions="0" bookmarkPermissions="0" thumbPermissions="0" annotPermissions="0" formPermissions="0" signPermissions="0" expiration="2048-12-31" updateOnOpen="false"/>
 * 	 <status activated="2026-06-01 12:34:56"/>
 * </documentInfo>
 * </pre>
 */
@JacksonXmlRootElement(localName = "documentInfo")
@SuppressWarnings({"unused", "java:S1104"})
public class Certificate {

    @JacksonXmlProperty(isAttribute = true)
    public int version;

    @JacksonXmlProperty(isAttribute = true)
    public String documentId;

    @JacksonXmlProperty(isAttribute = true)
    public String activationId;

    public Encryption encryption;

    public Permissions permissions;

    public Status status;

    public static class Encryption {
        @JacksonXmlProperty(isAttribute = true)
        public int cryptVersion;

        @JacksonXmlProperty(isAttribute = true)
        public String encryptionKey;
    }

    public static class Permissions {
        @JacksonXmlProperty(isAttribute = true)
        public int docPermissions;

        @JacksonXmlProperty(isAttribute = true)
        public int pagePermissions;

        @JacksonXmlProperty(isAttribute = true)
        public int linkPermissions;

        @JacksonXmlProperty(isAttribute = true)
        public int bookmarkPermissions;

        @JacksonXmlProperty(isAttribute = true)
        public int thumbPermissions;

        @JacksonXmlProperty(isAttribute = true)
        public int annotPermissions;

        @JacksonXmlProperty(isAttribute = true)
        public int formPermissions;

        @JacksonXmlProperty(isAttribute = true)
        public int signPermissions;

        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = "yyyy-MM-dd")
        public LocalDate expiration;

        @JacksonXmlProperty(isAttribute = true)
        public boolean updateOnOpen;
    }

    public static class Status {
        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        public LocalDateTime activated;
    }
}
