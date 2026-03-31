package com.example.tasktube.server.ui.responses;

import com.example.tasktube.server.application.models.TaskSettingsDto;

public record TaskPushSettingRequest(
        int maxFailures,
        int failureRetryTimeoutSeconds,
        int timeoutSeconds,
        int heartbeatTimeoutSeconds
) {
    public TaskSettingsDto to() {
        return new TaskSettingsDto(maxFailures, failureRetryTimeoutSeconds, timeoutSeconds, heartbeatTimeoutSeconds);
    }
}
