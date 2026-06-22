package com.ets.exception;

/**
 * Exception thrown when external API calls fail.
 */
public final class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}