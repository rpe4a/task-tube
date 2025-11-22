package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.values.TaskSettings;

public record TaskSettingsDto(
        int maxFailures,
        int failureRetryTimeoutSeconds
) {

    public TaskSettings to() {
        return new TaskSettings(maxFailures, failureRetryTimeoutSeconds);
    }
}
