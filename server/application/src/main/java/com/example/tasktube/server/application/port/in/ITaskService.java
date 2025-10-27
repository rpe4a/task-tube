package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.application.models.RunningTaskDto;
import com.example.tasktube.server.domain.enties.Task;

import java.util.Optional;
import java.util.UUID;

public interface ITaskService {
    UUID runTask(final RunningTaskDto task);

    Optional<Task> getTaskById(UUID taskId);
}
