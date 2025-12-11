package com.example.tasktube.server.api.requests;

import jakarta.validation.constraints.NotBlank;

public record PopTaskRequest(
        @NotBlank String client
) {
}

