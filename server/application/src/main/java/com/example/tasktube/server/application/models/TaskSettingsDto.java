package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.values.TaskSettings;

public record TaskSettingsDto(
        int maxFailures,
        int failureRetryTimeoutSeconds,
        int timeoutSeconds,
        int heartbeatTimeoutSeconds
) {
    public TaskSettings to() {
        return new TaskSettings(maxFailures, failureRetryTimeoutSeconds, timeoutSeconds, heartbeatTimeoutSeconds);
    }
}
