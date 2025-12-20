package com.example.tasktube.client.sdk;

public record TaskTubeClientSettings(
        int connectionTimeoutSeconds,
        String taskTubeServerApiHost
) {
}
