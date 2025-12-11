package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.application.models.FinishTaskDto;

import java.time.Instant;
import java.util.UUID;

public interface ITaskService {
    void scheduleTask(UUID taskId, Instant scheduledAt, String client);

    void startTask(UUID taskId, Instant startedAt, String client);

    void processTask(UUID taskId, Instant processedAt, String client);

    void finishTask(FinishTaskDto taskDto);

    void failTask(UUID taskId, Instant failedAt, String failedReason, String client);

    void completeTask(UUID taskID, Instant completedAt, String client);

    void unlockTask(UUID taskId, final int lockedTimeoutSeconds);
}
