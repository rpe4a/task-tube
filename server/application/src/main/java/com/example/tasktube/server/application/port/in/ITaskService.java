package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.domain.enties.Task;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ITaskService {
    Optional<Task> getTaskById(UUID taskId);

    void scheduleTask(UUID taskId, Instant scheduledAt, String client);

    void startTask(UUID taskId, Instant startedAt, String client);

    void processTask(UUID taskId, Instant processedAt, String client);

    void finishTask(FinishTaskDto taskDto);

    void completeTask(UUID taskID, Instant finalizedAt, String client);

    void failTask(UUID taskId, Instant failedAt, String failedReason, String client);

}
