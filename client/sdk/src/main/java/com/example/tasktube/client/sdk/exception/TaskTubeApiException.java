package com.example.tasktube.client.sdk.exception;

public class TaskTubeApiException extends TaskTubeException {
    public TaskTubeApiException(String message) {
        super(message);
    }

    public TaskTubeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
