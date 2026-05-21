package it.kapfer.librepress.pdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.*;

import java.io.IOException;
public class NDSecurityHandler extends SecurityHandler<NDProtectionPolicy> {
    private static final String FILTER_NAME = "NDPD:CryptHandler";

    public static void register() {
        if (SecurityHandlerFactory.INSTANCE.newSecurityHandlerForFilter(FILTER_NAME) == null) {
            SecurityHandlerFactory.INSTANCE.registerHandler(FILTER_NAME, NDSecurityHandler.class, NDProtectionPolicy.class);
        }
    }

    @Override
    public void prepareDocumentForEncryption(PDDocument doc) throws IOException {
        throw new UnsupportedOperationException("Encryption not supported - decryption only");
    }

    @Override
    public void prepareForDecryption(PDEncryption encryption, COSArray documentIDArray, DecryptionMaterial decryptionMaterial) throws IOException {
        if (!(decryptionMaterial instanceof StandardDecryptionMaterial)) {
            throw new IOException("Decryption material is not compatible with the document");
        }

        String password = ((StandardDecryptionMaterial) decryptionMaterial).getPassword();
        if (password == null || password.isEmpty()) {
            throw new IOException("Encryption key (password) is required");
        }

        // The password contains the raw encryption key bytes
        // Convert from string back to bytes
        byte[] encryptionKey = password.getBytes();
        
        // NDPD encryption uses AES-128
        setDecryptMetadata(encryption.isEncryptMetaData());
        setAES(true);
        setKeyLength(128);
        
        // Set the encryption key - base SecurityHandler will use this
        // to derive per-object keys via calcFinalKey()
        setEncryptionKey(encryptionKey);
    }
}
