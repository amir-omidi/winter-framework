package com.winter.exception;

public class WinterException extends RuntimeException {

    public WinterException(String message) {
        super(message);
    }

    public WinterException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}