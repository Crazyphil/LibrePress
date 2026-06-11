package it.kapfer.librepress.drm.exception;

/**
 * Exception thrown when the decryption of a certificate fails.
 */
public class DecryptionException extends RuntimeException {
    public DecryptionException(String message) {
        super(message);
    }

    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
