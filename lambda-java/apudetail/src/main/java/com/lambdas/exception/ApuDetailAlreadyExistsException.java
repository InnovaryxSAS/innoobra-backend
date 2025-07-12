package com.lambdas.exception;

public class ApuDetailAlreadyExistsException extends RuntimeException {
    public ApuDetailAlreadyExistsException(String message) {
        super(message);
    }

    public ApuDetailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}