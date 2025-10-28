package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITaskRepository {
    Task create(Task task);

    Optional<Task> getById(UUID id);

    List<Task> getTasksForScheduling(String worker, int count);

    void schedule(List<Task> tasks);
}
