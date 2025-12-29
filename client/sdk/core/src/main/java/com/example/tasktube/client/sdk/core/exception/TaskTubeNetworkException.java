package com.example.tasktube.client.sdk.core.exception;

public class TaskTubeNetworkException extends TaskTubeException {
    public TaskTubeNetworkException(final String message) {
        super(message);
    }

    public TaskTubeNetworkException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
