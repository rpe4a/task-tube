package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.enties.LogRecord;
import com.example.tasktube.server.domain.enties.LogRecordLevel;
import com.example.tasktube.server.domain.enties.LogRecordType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.UUID;

public record LogRecordDto(
        @Nonnull LogRecordType type,
        @Nonnull Instant timestamp,
        @Nonnull LogRecordLevel level,
        @Nonnull String message,
        @Nullable String exceptionMessage,
        @Nullable String exceptionStackTrace
) {

    public LogRecord to(final UUID taskId) {
        return new LogRecord(taskId, type, timestamp, level, message, exceptionMessage, exceptionStackTrace);
    }
}
