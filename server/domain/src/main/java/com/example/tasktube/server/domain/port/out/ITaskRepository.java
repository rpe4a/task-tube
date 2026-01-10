package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITaskRepository {

    Optional<Task> get(UUID id);

    Optional<Task> get(UUID taskId, String client);

    List<Task> get(List<UUID> taskIdList);

    void update(Task task);

}
