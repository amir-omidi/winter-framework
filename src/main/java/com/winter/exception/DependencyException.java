package com.winter.exception;

public class DependencyException
        extends WinterException {

    public DependencyException(
            String message
    ) {
        super(message);
    }

    public DependencyException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}