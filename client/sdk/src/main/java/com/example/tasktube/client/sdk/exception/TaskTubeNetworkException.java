package com.example.tasktube.client.sdk.exception;

public class TaskTubeNetworkException extends TaskTubeException {
    public TaskTubeNetworkException(String message) {
        super(message);
    }

    public TaskTubeNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
