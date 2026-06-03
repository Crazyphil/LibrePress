package it.kapfer.librepress.pdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Security handler for NDPD (NewspaperDirect) custom PDF encryption.
 * <p>
 * The NDPD encryption uses a custom filter "NDPD:CryptHandler" with cryptVersion=2. NDPD uses RC4 for both stream and string encryption.
 * <p>
 * Key derivation (per object, matches PDF spec Algorithm 1):
 * <ul>
 * <li>MD5(encryptionKey + objNum(3B LE) + genNum(2B LE))</li>
 * <li>NO "sAlT" appendix (that's only for AES algorithm)</li>
 * <li>Truncated to min(encryptionKey.length + 5, 16) bytes</li>
 * </ul>
 * <p>
 * We set {@code useAES=false} so the base class uses RC4 for both streams and strings. The base class's {@code calcFinalKey()} does NOT append AES_SALT when
 * {@code useAES} is {@code false}, which matches the NDPD key derivation exactly.
 */
public class NDSecurityHandler extends SecurityHandler<NDProtectionPolicy> {
    private static final String FILTER_NAME = "NDPD:CryptHandler";

    public static void register() {
        if (SecurityHandlerFactory.INSTANCE.newSecurityHandlerForFilter(FILTER_NAME) == null) {
            SecurityHandlerFactory.INSTANCE.registerHandler(FILTER_NAME, NDSecurityHandler.class, NDProtectionPolicy.class);
        }
    }

    @Override
    public void prepareDocumentForEncryption(PDDocument doc) {
        throw new UnsupportedOperationException("Encryption not supported - decryption only");
    }

    /**
     * Prepares for decryption of NDPD-encrypted PDF documents.
     * <p>
     * The encryption key is provided as the "password" in the decryption material.
     * The key bytes are preserved through ISO-8859-1 encoding (1:1 byte-char mapping).
     * <p>
     * NDPD uses RC4 for all object types. We set useAES=false so the base class
     * uses RC4 for both streams and strings via its standard decrypt pipeline.
     */
    @Override
    public void prepareForDecryption(PDEncryption encryption, COSArray documentIDArray, DecryptionMaterial decryptionMaterial) throws IOException {
        checkPreconditions(encryption, decryptionMaterial);

        String password = ((StandardDecryptionMaterial) decryptionMaterial).getPassword();

        // Recover the original encryption key bytes using ISO-8859-1 (1:1 byte-char mapping)
        byte[] encryptionKey = password.getBytes(StandardCharsets.ISO_8859_1);

        setDecryptMetadata(encryption.isEncryptMetaData());
        setAES(false); // NDPD uses RC4, not AES
        setKeyLength(encryptionKey.length * Byte.SIZE); // key length in bits
        setEncryptionKey(encryptionKey);
    }

    private void checkPreconditions(PDEncryption encryption, DecryptionMaterial decryptionMaterial) throws IOException {
        if (encryption.getVersion() != 2) {
            throw new IOException("Unsupported encryption version " + encryption.getVersion() + ", only version 2 is supported");
        }
        if (!(decryptionMaterial instanceof StandardDecryptionMaterial standardDecryptionMaterial)) {
            throw new IOException("Decryption material is not compatible with the document");
        }
        String password = standardDecryptionMaterial.getPassword();
        if (password == null || password.isEmpty()) {
            throw new IOException("Encryption key (password) is required");
        }
    }
}
