package com.example.tasktube.client.sdk.exception;

public class TaskTubeException extends RuntimeException {
    public TaskTubeException(String message) {
        super(message);
    }

    public TaskTubeException(String message, Throwable cause) {
        super(message, cause);
    }
}
