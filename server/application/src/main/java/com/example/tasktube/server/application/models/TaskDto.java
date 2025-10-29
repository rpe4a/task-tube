package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.enties.Task;

import java.util.Map;
import java.util.UUID;

public record TaskDto(UUID id, String name, String tube, Map<String, Object> input) {
    public static TaskDto from(final Task task) {
        return new TaskDto(
                task.getId(),
                task.getName(),
                task.getTube(),
                task.getInput()
        );
    }
}
