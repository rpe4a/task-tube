package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.domain.enties.Task;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ITaskService {
    Optional<Task> getTaskById(UUID taskId);

    void startTask(UUID taskId, String client, Instant instant);

    void processTask(UUID taskId, String client, Instant instant);

    void finishTask(FinishTaskDto taskDto);
}
