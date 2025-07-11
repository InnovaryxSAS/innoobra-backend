package com.lambdas.exception;

public class ActivityAlreadyExistsException extends RuntimeException {
    public ActivityAlreadyExistsException(String message) {
        super(message);
    }

    public ActivityAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}