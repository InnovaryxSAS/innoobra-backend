package com.lambdas.exception;

public class ChapterAlreadyExistsException extends RuntimeException {
    public ChapterAlreadyExistsException(String message) {
        super(message);
    }

    public ChapterAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}