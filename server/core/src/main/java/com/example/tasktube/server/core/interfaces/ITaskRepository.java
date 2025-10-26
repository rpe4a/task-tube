package com.example.tasktube.server.core.interfaces;

import com.example.tasktube.server.core.enties.Task;

import java.util.Optional;
import java.util.UUID;

public interface ITaskRepository {
    Task create(Task task);

    Optional<Task> getById(UUID id);
}
