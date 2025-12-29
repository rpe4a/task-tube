package com.example.tasktube.client.sdk.core.task;

import jakarta.annotation.Nonnull;

public final class TaskSettings {
    private int heartbeatTimeoutSeconds;
    private int timeoutSeconds;
    private int maxFailures;
    private int failureRetryTimeoutSeconds;

    public TaskSettings() {
    }

    public TaskSettings(
            final int maxFailures,
            final int failureRetryTimeoutSeconds,
            final int timeoutSeconds,
            final int heartbeatTimeoutSeconds
    ) {
        this.maxFailures = maxFailures;
        this.failureRetryTimeoutSeconds = failureRetryTimeoutSeconds;
        this.timeoutSeconds = timeoutSeconds;
        this.heartbeatTimeoutSeconds = heartbeatTimeoutSeconds;
    }

    @Nonnull
    public static TaskSettings DEFAULT() {
        return new TaskSettings(3, 60, 60 * 60, 10 * 60);
    }

    public int getMaxFailures() {
        return maxFailures;
    }

    public void setMaxFailures(final int maxFailures) {
        this.maxFailures = maxFailures;
    }

    public int getFailureRetryTimeoutSeconds() {
        return failureRetryTimeoutSeconds;
    }

    public void setFailureRetryTimeoutSeconds(final int failureRetryTimeoutSeconds) {
        this.failureRetryTimeoutSeconds = failureRetryTimeoutSeconds;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(final int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getHeartbeatTimeoutSeconds() {
        return heartbeatTimeoutSeconds;
    }

    public void setHeartbeatTimeoutSeconds(final int heartbeatTimeoutSeconds) {
        this.heartbeatTimeoutSeconds = heartbeatTimeoutSeconds;
    }

}
