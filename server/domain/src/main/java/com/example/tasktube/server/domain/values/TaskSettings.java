package com.example.tasktube.server.domain.values;

public record TaskSettings(
        int maxFailures,
        int failureRetryTimeoutSeconds
) {
    public static final int MAX_FAILURES = 3;
    public static final int FAILURE_RETRY_TIMEOUT_SECONDS = 60;

    public static TaskSettings getDefault(){
        return new TaskSettings(MAX_FAILURES, FAILURE_RETRY_TIMEOUT_SECONDS);
    }
}
