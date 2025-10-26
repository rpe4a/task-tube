package com.example.tasktube.server.core.services;

import com.example.tasktube.server.core.enties.Task;
import com.example.tasktube.server.core.interfaces.ITaskRepository;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final ITaskRepository repository;

    public TaskService(final ITaskRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Optional<Task> getTaskById(final UUID taskId) {
        Preconditions.checkNotNull(taskId);

        LOGGER.debug("Get task by id: {}.", taskId);
        return repository.getById(taskId);
    }
}
