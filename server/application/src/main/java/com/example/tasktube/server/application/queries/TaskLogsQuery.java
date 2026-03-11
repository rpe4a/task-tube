package com.example.tasktube.server.application.queries;

import jakarta.annotation.Nullable;

import java.util.UUID;

public record TaskLogsQuery(
        @Nullable UUID taskId,
        int page,
        int size
) {
}
