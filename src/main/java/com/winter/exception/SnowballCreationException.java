package com.winter.exception;

public class SnowballCreationException
        extends WinterException {

    public SnowballCreationException(
            String message
    ) {
        super(message);
    }

    public SnowballCreationException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}