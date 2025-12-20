package com.example.tasktube.client.sdk.dto;

import java.util.Map;
import java.util.UUID;

public record PopTaskResponse(
        UUID id,
        String name,
        String tube,
        Map<String, Object> input
) {
}
