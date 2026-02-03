package com.example.tasktube.client.sdk.core.http.dto;

import com.example.tasktube.client.sdk.core.task.LogRecord;
import com.example.tasktube.client.sdk.core.task.LogRecordLevel;
import com.example.tasktube.client.sdk.core.task.LogRecordType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.event.Level;

import java.time.Instant;

public record LogRequest(
        @Nonnull LogRecordType type,
        @Nonnull Instant timestamp,
        @Nonnull LogRecordLevel level,
        @Nonnull String message,
        @Nullable String exceptionMessage,
        @Nullable String exceptionStackTrace
) {

    public static LogRequest from(final LogRecord logRecord) {
        return new LogRequest(
                logRecord.getType(),
                logRecord.getTimestamp(),
                logRecord.getLevel(),
                logRecord.getMessage(),
                logRecord.getExceptionMessage(),
                logRecord.getExceptionStackTrace()
        );
    }
}
