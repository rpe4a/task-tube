package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.domain.values.argument.Argument;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ITaskService {

    List<Argument> startTask(UUID taskId, Instant startedAt, String client);

    void processTask(UUID taskId, Instant processedAt, String client);

    void finishTask(FinishTaskDto taskDto);

    void failTask(UUID taskId, Instant failedAt, String failedReason, String client);

    void unlockTask(UUID taskId, int lockedTimeoutSeconds);
}
