package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.TaskSettingsDto;

public record TaskSettingRequest(
        int maxFailures,
        int failureRetryTimeoutSeconds,
        int timeoutSeconds,
        int heartbeatTimeoutSeconds
) {
    public TaskSettingsDto to() {
        return new TaskSettingsDto(maxFailures, failureRetryTimeoutSeconds, timeoutSeconds, heartbeatTimeoutSeconds);
    }
}
