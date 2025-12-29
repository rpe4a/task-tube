package com.example.tasktube.client.sdk.core.task;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.UUID;

public interface TaskConfiguration {
    @Nonnull
    static TaskConfiguration waitFor(@Nonnull final TaskResult<?> taskResult) {
        return new WaitForTask(taskResult);
    }

    @Nonnull
    static TaskConfiguration maxCountOfFailures(final int value) {
        return new MaxCountOfFailures(value);
    }

    @Nonnull
    static TaskConfiguration failureRetryTimeoutSeconds(final int value) {
        return new FailureRetryTimeoutSeconds(value);
    }

    void applyTo(@Nonnull TaskRecord<?> taskRecord);

    final class WaitForTask implements TaskConfiguration {
        private final UUID id;

        public WaitForTask(@Nonnull final TaskResult<?> taskResult) {
            this.id = Objects.requireNonNull(taskResult).getId();
        }

        @Nonnull
        public UUID getId() {
            return id;
        }

        @Override
        public void applyTo(@Nonnull final TaskRecord<?> taskRecord) {
            Objects.requireNonNull(taskRecord).waitFor(id);
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

        @Override
        public void applyTo(@Nonnull final TaskRecord<?> taskRecord) {
            Objects.requireNonNull(taskRecord).getSetting().setMaxFailures(value);
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

        @Override
        public void applyTo(@Nonnull final TaskRecord<?> taskRecord) {
            Objects.requireNonNull(taskRecord).getSetting().setFailureRetryTimeoutSeconds(value);
        }
    }

    class TimeoutSeconds implements TaskConfiguration {
        private final int value;

        public TimeoutSeconds(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public void applyTo(@Nonnull final TaskRecord<?> taskRecord) {
            Objects.requireNonNull(taskRecord).getSetting().setTimeoutSeconds(value);
        }
    }

    class HeartbeatTimeoutSeconds implements TaskConfiguration {
        private final int value;

        public HeartbeatTimeoutSeconds(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public void applyTo(@Nonnull final TaskRecord<?> taskRecord) {
            Objects.requireNonNull(taskRecord).getSetting().setHeartbeatTimeoutSeconds(value);
        }
    }
}
