package it.kapfer.librepress.drm;

import it.kapfer.librepress.xml.XmlParser;

import java.util.Base64;

public class EncryptionKeyProvider {
    private static final String ENCRYPTION_NODE = "encryption";

    private final String encodedCertificate;
    private final String activationPassword;

    private final NDCertificateEncryption encryption;

    public EncryptionKeyProvider(String encodedCertificate, int clientNumber, String clientAddress, String activationPassword) {
        this.encodedCertificate = encodedCertificate;
        this.activationPassword = activationPassword;
        this.encryption = new NDCertificateEncryption(clientNumber, clientAddress);
    }

    public byte[] getEncryptionKey() {
        byte[] certificate = Base64.getDecoder().decode(encodedCertificate);
        String certificateData = decryptCertificate(certificate);

        XmlParser parser = new XmlParser(certificateData);
        ensureCorrectEncryptionVersion(parser);

        String encryptionKey = parser.getAttribute(ENCRYPTION_NODE, "encryptionKey");
        return Base64.getDecoder().decode(encryptionKey);
    }

    private String decryptCertificate(byte[] encryptedCertificate) {
        String decryptedCertificate = encryption.decrypt(encryptedCertificate, activationPassword);
        if (decryptedCertificate == null) {
            throw new IllegalArgumentException("Could not decrypt certificate, check clientNumber, clientAddress and activationPassword");
        }
        return decryptedCertificate;
    }

    private void ensureCorrectEncryptionVersion(XmlParser parser) {
        String encryptionVersionString = parser.getAttribute(ENCRYPTION_NODE, "cryptVersion");
        int encryptionVersion = intFromString(encryptionVersionString);
        if (encryptionVersion != 2) {
            throw new IllegalArgumentException("Only encryption version 2 is supported, certificate has version " + encryptionVersion);
        }
    }

    private int intFromString(String intString) {
        try {
            return Integer.parseInt(intString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
