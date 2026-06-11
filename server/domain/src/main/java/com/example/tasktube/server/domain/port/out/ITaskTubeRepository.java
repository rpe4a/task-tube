package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.TaskTube;

import java.util.Optional;
import java.util.UUID;

public interface ITaskTubeRepository {

    Optional<TaskTube> find(final String correlationId, final UUID taskId);

    void create(final TaskTube taskTube);

    void update(final TaskTube taskTube);

}
