package com.example.tasktube.client.sdk.http;

public record TaskTubeClientSettings(
        int connectionTimeoutSeconds,
        String taskTubeServerApiHost
) {
}
