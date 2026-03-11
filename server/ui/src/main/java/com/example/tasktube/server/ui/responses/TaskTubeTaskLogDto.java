package com.example.tasktube.server.ui.responses;

import java.time.Instant;
import java.util.UUID;

public record TaskTubeTaskLogDto(
        UUID id,
        UUID taskId,
        String type,
        String level,
        Instant timestamp,
        String message,
        String exceptionMessage,
        String exceptionStackTrace
) {
}
