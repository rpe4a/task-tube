package com.example.tasktube.client.sdk.core.http;

public record TaskTubeClientSettings(
        int connectionTimeoutSeconds,
        String taskTubeServerApiHost
) {
}
