package it.kapfer.librepress.drm;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class NDCertificateEncryption {
    private static final String SECRET_FILE = "ndsecret.bin";
    private static final byte[] SALT = new byte[]{0x1c, (byte) 0xfd, (byte) 0xf5, 0x4e, 0x17, 0x60, 0x09, 0x39};

    private static final int KEY_BITS = 256;  // Equals AES encryption strength, e.g. AES-256 -> KEY_BITS = 256
    private static final int IV_BYTES = 16;
    private static final int ITERATION_COUNT = 101;

    private final byte[] secret;
    private final int clientNumber;
    private final String clientAddress;
    private final Cipher cipher;

    public NDCertificateEncryption(int clientNumber, String clientAddress) {
        this.secret = readNDSecret();
        this.clientNumber = clientNumber;
        this.clientAddress = clientAddress;
        this.cipher = getCipher();
    }

    private byte[] readNDSecret() {
        Path secretFile = Path.of(SECRET_FILE);
        try {
            byte[] secretBytes = Files.readAllBytes(secretFile);
            if (secretBytes.length != 16) {
                throw new IOException("Secret file must be exactly 16 bytes long");
            }
            return secretBytes;
        } catch (IOException e) {
            throw new IllegalStateException("The secret file " + secretFile.toAbsolutePath() + " could not be read, ensure it exists and has 16 bytes in it");
        }
    }

    private Cipher getCipher() {
        try {
            return Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException("The JVM doesn't support the required cryptography (cipher) functionality", e);
        }
    }

    public String decrypt(byte[] encryptedCertificate, String activationPassword) {
        byte[] clientSpecificSecret = getClientSpecificSecret(activationPassword, false);
        String decryptedCertificate = decrypt(encryptedCertificate, clientSpecificSecret);
        if (decryptedCertificate == null) {
            clientSpecificSecret = getClientSpecificSecret(activationPassword, true);
            decryptedCertificate = decrypt(encryptedCertificate, clientSpecificSecret);
        }
        return decryptedCertificate;
    }

    private byte[] getClientSpecificSecret(String activationPassword, boolean includeClientAddress) {
        byte[] modifiedSecret = Arrays.copyOf(secret, secret.length);
        for (int i = 0; i < 16; i++) {
            if (activationPassword == null) {
                modifiedSecret[i] = (byte) (modifiedSecret[i] ^ byteOfInt(clientNumber, i % Integer.BYTES));
                if (includeClientAddress) {
                    modifiedSecret[i] = (byte) (modifiedSecret[i] ^ clientAddress.charAt(i % clientAddress.length()));
                }
            } else {
                modifiedSecret[i] = (byte) (modifiedSecret[i] ^ activationPassword.charAt(i % activationPassword.length()));
            }
        }
        return modifiedSecret;
    }

    private byte byteOfInt(int integer, int nThByte) {
        if (nThByte < 0 || nThByte > Integer.BYTES) {
            throw new IllegalArgumentException("A Java integer has " + Integer.BYTES + " bytes, requested " + nThByte);
        }
        return (byte) (integer >> (nThByte * Byte.SIZE));
    }

    private String decrypt(byte[] encryptedCertificate, byte[] secret) {
        prepareDecryption(secret);

        byte[] decryptedCertificate = decryptCertificate(encryptedCertificate);
        if (decryptedCertificate.length == 0) {
            return null;
        }
        return stringFromBytes(decryptedCertificate);
    }

    private void prepareDecryption(byte[] secret) {
        OpenSSL.CipherParameters parameters = OpenSSL.EVP_BytesToKey(KEY_BITS / Byte.SIZE, IV_BYTES, getMessageDigest(), SALT, secret, ITERATION_COUNT);
        initializeCipherForDecryption(parameters);
    }

    private MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("The JVM doesn't support the required cryptography (message digest) functionality", e);
        }
    }

    private void initializeCipherForDecryption(OpenSSL.CipherParameters parameters) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, parameters.getSecretKey(), parameters.getAlgorithmParameters());
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalStateException("Cannot initialize decryptor with the provided secret key and IV", e);
        }
    }

    private byte[] decryptCertificate(byte[] encryptedCertificate) {
        try {
            return cipher.doFinal(encryptedCertificate);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return new byte[0];
        }
    }

    private String stringFromBytes(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return String.valueOf(chars);
    }
}
