package com.example.tasktube.client.sdk.dto;

public record PopTasksRequest(
        String client,
        int count
) {
}
