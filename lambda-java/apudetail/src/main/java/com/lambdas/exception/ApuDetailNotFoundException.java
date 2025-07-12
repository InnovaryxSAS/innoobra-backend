package com.lambdas.exception;

public class ApuDetailNotFoundException extends RuntimeException {
    public ApuDetailNotFoundException(String message) {
        super(message);
    }

    public ApuDetailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}