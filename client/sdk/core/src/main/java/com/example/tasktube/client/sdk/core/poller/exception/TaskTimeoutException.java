package com.example.tasktube.client.sdk.core.poller.exception;

public class TaskTimeoutException extends RuntimeException {
    public TaskTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
