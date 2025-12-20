package com.example.tasktube.client.sdk.task;

import java.util.UUID;

public interface TaskConfiguration {
    static TaskConfiguration waitFor(final TaskResult<?> taskResult) {
        return new WaitForTask(taskResult);
    }

    static TaskConfiguration maxCountOfFailures(final int value) {
        return new MaxCountOfFailures(value);
    }

    static TaskConfiguration failureRetryTimeoutSeconds(final int value) {
        return new FailureRetryTimeoutSeconds(value);
    }

    final class WaitForTask implements TaskConfiguration {
        private final UUID id;

        public WaitForTask(final TaskResult<?> taskResult) {
            this.id = taskResult.getId();
        }

        public UUID getId() {
            return id;
        }
    }

    final class MaxCountOfFailures implements TaskConfiguration {
        private final int value;

        public MaxCountOfFailures(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    class FailureRetryTimeoutSeconds implements TaskConfiguration {
        private final int value;

        public FailureRetryTimeoutSeconds(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
