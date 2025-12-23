package com.example.tasktube.client.sdk.task;

public class RunnableMethodException extends RuntimeException {
    public RunnableMethodException(final Throwable innerException) {
        super(innerException);
    }
}
