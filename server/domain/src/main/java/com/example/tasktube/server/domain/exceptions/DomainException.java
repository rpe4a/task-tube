package com.example.tasktube.server.domain.exceptions;

public class DomainException extends RuntimeException {
    public DomainException(final String message) {
        super(message);
    }
}
