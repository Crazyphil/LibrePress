package it.kapfer.librepress.server.exception;

import it.kapfer.librepress.server.xml.ActivationResponse;

/**
 * This exception is thrown when the server responds with an error during newspaper activation. This can happen, for example, when the newspaper issue was
 * already activated once.
 */
public class NewspaperActivationException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;

    public NewspaperActivationException(ActivationResponse response) {
        super("Could not activate newspaper issue. Server responded with error " + response.statusCode + ": " + response.statusMessage);

        errorCode = response.statusCode;
        errorMessage = response.statusMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
