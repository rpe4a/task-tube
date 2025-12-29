package com.example.tasktube.client.sdk.core.poller.exception;

public class TaskInterruptedException extends RuntimeException {
    public TaskInterruptedException(final String message) {
        super(message);
    }
}
