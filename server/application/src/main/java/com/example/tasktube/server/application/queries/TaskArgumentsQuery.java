package com.example.tasktube.server.application.queries;

import jakarta.annotation.Nullable;

import java.util.UUID;

public record TaskArgumentsQuery(
        @Nullable UUID taskId
) {
}
