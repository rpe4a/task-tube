package com.example.tasktube.client.sdk.core.http.dto;

import com.example.tasktube.client.sdk.core.task.argument.Argument;
import jakarta.annotation.Nonnull;

public record StartTaskResponse(
        @Nonnull Argument[] arguments
) {
}
