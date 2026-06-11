package it.kapfer.librepress.drm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import it.kapfer.librepress.drm.exception.DecryptionException;
import it.kapfer.librepress.drm.xml.Certificate;
import it.kapfer.librepress.server.DeviceRegistration;
import it.kapfer.librepress.server.xml.jackson.XmlMapperProvider;

import java.util.Base64;

/**
 * Decrypts the certificate contained in the activation response for a newspaper issue and provides the encryption key necessary to open the newspaper file.
 */
public class EncryptionKeyProvider {
    private final XmlMapper xmlMapper;

    private final String activationPassword;
    private final NDCertificateEncryption encryption;

    /**
     * Creates a new encryption key provider that can provide encryption keys for all certificates created for the given client number and address.
     * <p>
     * The client address is the client's MAC address provided when activating the newspaper (or {@code 00:00:00:00:00:00} by default) and is only used when the
     * certificate was encrypted including this information. This case hasn't been observed yet in the wild.
     *
     * @param clientId      client ID from {@link DeviceRegistration#clientId()}
     * @param clientAddress MAC address of the client used during activation to decrypt a client-specific certificate, or {@code null} otherwise
     */
    public EncryptionKeyProvider(int clientId, String clientAddress) {
        this(XmlMapperProvider.createXmlMapper(), clientId, clientAddress, null);
    }

    /**
     * Creates a new encryption key provider that can provide encryption keys for all certificates created for the given static activation password.
     *
     * @param activationPassword static password to use for decrypting the certificate instead of using the client-specific information. This is what the user
     *                           enters as "Access password" in the PressReader software.
     */
    public EncryptionKeyProvider(String activationPassword) {
        this(XmlMapperProvider.createXmlMapper(), 0, null, activationPassword);
    }

    EncryptionKeyProvider(XmlMapper xmlMapper, int clientId, String clientAddress, String activationPassword) {
        this.xmlMapper = xmlMapper;
        this.activationPassword = activationPassword;
        this.encryption = new NDCertificateEncryption(clientId, clientAddress);
    }

    /**
     * Returns the encryption key contained within the given Base64-encoded encrypted certificate. The certificate is decrypted using the information given when
     * creating the class.
     *
     * @param encodedCertificate the Base64-encoded encrypted certificate
     * @return the encryption key required to open the newspaper file
     */
    public byte[] getEncryptionKey(String encodedCertificate) {
        try {
            byte[] certificateBytes = Base64.getDecoder().decode(encodedCertificate);
            String certificateData = decryptCertificate(certificateBytes);

            Certificate certificate = xmlMapper.readValue(certificateData, Certificate.class);
            ensureCorrectEncryptionVersion(certificate);
            return Base64.getDecoder().decode(certificate.encryption.encryptionKey);
        } catch (IllegalArgumentException e) {
            throw new DecryptionException("Could not decode encrypted certificate from Base64 string", e);
        } catch (JsonProcessingException e) {
            throw new DecryptionException("Could not parse decrypted certificate", e);
        }

    }

    private String decryptCertificate(byte[] encryptedCertificate) {
        String decryptedCertificate = encryption.decrypt(encryptedCertificate, activationPassword);
        if (decryptedCertificate == null) {
            throw new DecryptionException("Could not decrypt certificate, was it created for the given clientNumber, clientAddress and activationPassword?");
        }
        return decryptedCertificate;
    }

    private void ensureCorrectEncryptionVersion(Certificate certificate) {
        if (certificate.encryption.cryptVersion != 2) {
            throw new DecryptionException("Only encryption version 2 is supported, certificate has version " + certificate.encryption.cryptVersion);
        }
    }
}
