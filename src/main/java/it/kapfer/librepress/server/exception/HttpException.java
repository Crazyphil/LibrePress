package it.kapfer.librepress.server.exception;

import it.kapfer.librepress.server.xml.response.ErrorResponse;

/**
 * This exception is thrown when an API request fails, either because the request could not be sent, the response could not be parsed, or the server actually
 * returned an error.
 * <p>
 * If the API request has actually reached the server, which sent a negative response, {@link #getErrorCode()} contains the error code returned by the server.
 */
public class HttpException extends RuntimeException {
    private final Integer errorCode;
    private final String errorMessage;

    public HttpException(String message) {
        super(message);

        errorCode = null;
        errorMessage = null;
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);

        errorCode = null;
        errorMessage = null;
    }

    public HttpException(ErrorResponse errorResponse) {
        super(toExceptionMessage(errorResponse));

        this.errorCode = errorResponse.errorCode;
        this.errorMessage = errorResponse.errorMessage;
    }

    private static String toExceptionMessage(ErrorResponse errorResponse) {
        String message = "API request failed with error code " + errorResponse.errorCode + ": " + errorResponse.errorMessage;
        if (errorResponse.errorHelpUrl != null && !errorResponse.errorHelpUrl.isBlank()) {
            message += " (see \"" + errorResponse.errorHelpUrl + "\" for help)";
        }
        return message;
    }

    /**
     * {@return the error code returned by the server, or {@code null} if the request itself failed}
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * {@return the error message returned by the server, or {@code null} if the request itself failed}
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
