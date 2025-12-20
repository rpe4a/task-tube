package com.example.tasktube.client.sdk.task;

import java.util.Objects;

public final class TaskSetting {
    private int maxFailures;
    private int failureRetryTimeoutSeconds;

    public TaskSetting(
            final int maxFailures,
            final int failureRetryTimeoutSeconds
    ) {
        this.maxFailures = maxFailures;
        this.failureRetryTimeoutSeconds = failureRetryTimeoutSeconds;
    }

    public static TaskSetting DEFAULT() {
        return new TaskSetting(3, 60);
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

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final TaskSetting that = (TaskSetting) obj;
        return this.maxFailures == that.maxFailures &&
                this.failureRetryTimeoutSeconds == that.failureRetryTimeoutSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxFailures, failureRetryTimeoutSeconds);
    }

    @Override
    public String toString() {
        return "TaskSetting[" +
                "maxFailures=" + maxFailures + ", " +
                "failureRetryTimeoutSeconds=" + failureRetryTimeoutSeconds + ']';
    }

}
