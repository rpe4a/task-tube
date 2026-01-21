package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.domain.values.argument.Argument;
import jakarta.annotation.Nonnull;

public record StartTaskResponse(
        @Nonnull Argument[] arguments
) {
}
