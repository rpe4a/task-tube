package com.example.tasktube.server.application.exceptions;

public class ApplicationException extends RuntimeException {
    public ApplicationException(final String message) {
        super(message);
    }
}
