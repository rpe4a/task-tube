package com.example.tasktube.client.sdk.exception;

public class TaskTubeApiException extends TaskTubeException {
    public TaskTubeApiException(final String message) {
        super(message);
    }

    public TaskTubeApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
