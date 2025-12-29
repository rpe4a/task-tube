package com.example.tasktube.client.sdk.core.exception;

public class TaskTubeException extends RuntimeException {
    public TaskTubeException(final String message) {
        super(message);
    }

    public TaskTubeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
