package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.values.LogRecord;
import com.example.tasktube.server.domain.values.LogRecordLevel;
import com.example.tasktube.server.domain.values.LogRecordType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Instant;

public record LogRecordDto(
        @Nonnull LogRecordType type,
        @Nonnull Instant timestamp,
        @Nonnull LogRecordLevel level,
        @Nonnull String message,
        @Nullable String exceptionMessage,
        @Nullable String exceptionStackTrace
) {

    public LogRecord to() {
        return new LogRecord(type, timestamp, level, message, exceptionMessage, exceptionStackTrace);
    }
}
