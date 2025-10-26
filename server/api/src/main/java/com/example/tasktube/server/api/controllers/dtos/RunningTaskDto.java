package com.example.tasktube.server.api.controllers.dtos;

import java.time.Instant;
import java.util.Map;

public record RunningTaskDto(
        String name,
        String queueName,
        Instant createdAt,
        Map<String, Object> input
) {

}

