package com.example.tasktube.server.application.models;

import java.util.UUID;

public record TaskTubeDto(String correlationId, UUID taskId) {
}
