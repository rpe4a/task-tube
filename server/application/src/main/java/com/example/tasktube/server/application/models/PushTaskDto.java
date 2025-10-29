package com.example.tasktube.server.application.models;

import java.time.Instant;
import java.util.Map;

public record PushTaskDto(
        String name,
        String queue,
        Instant createAt,
        Map<String, Object> input) {
}
