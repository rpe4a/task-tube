package com.example.tasktube.server.api.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PopTasksRequest(
        @NotBlank String client,
        @Min(1) int count
) {
}
