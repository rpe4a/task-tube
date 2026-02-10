package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.LogRecordDto;
import com.example.tasktube.server.domain.enties.LogRecordLevel;
import com.example.tasktube.server.domain.enties.LogRecordType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record LogRequest(
        @NotNull String type,
        @NotNull Instant timestamp,
        @NotNull String level,
        @NotBlank(message = "message is invalid.") String message,
        @Nullable String exceptionMessage,
        @Nullable String exceptionStackTrace
) {

    public LogRecordDto to() {
        return new LogRecordDto(
                LogRecordType.valueOf(type),
                timestamp,
                LogRecordLevel.valueOf(level),
                message,
                exceptionMessage,
                exceptionStackTrace
        );
    }
}
