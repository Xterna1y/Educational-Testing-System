package com.ets.exception;
/**
 * Thrown when the Open Trivia Database API returns an invalid or
 * unsuccessful response — either no response at all or a non-zero
 * response code.
 * <p>
 * Callers are expected to catch this (directly or via a wrapping
 * exception) and fall back to the locally cached question bank, so
 * that API failures never surface as raw errors to the user.
 */

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message,
                        Throwable cause) {
        super(message, cause);
    }
}