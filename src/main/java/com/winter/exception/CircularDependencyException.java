package com.winter.exception;

public class CircularDependencyException
        extends DependencyException {

    public CircularDependencyException(
            String message
    ) {
        super(message);
    }
}