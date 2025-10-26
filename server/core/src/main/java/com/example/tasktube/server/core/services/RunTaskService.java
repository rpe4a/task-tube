package com.example.tasktube.server.core.services;

import com.example.tasktube.server.core.enties.Task;
import com.example.tasktube.server.core.interfaces.ITaskRepository;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RunTaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunTaskService.class);

    private final ITaskRepository repository;

    public RunTaskService(final ITaskRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Task runTask(final Task task) {
        Preconditions.checkNotNull(task);
        Preconditions.checkArgument(Task.Status.CREATED.equals(task.getStatus()));
        Preconditions.checkArgument(Strings.isNotEmpty(task.getName()));
        Preconditions.checkArgument(Strings.isNotEmpty(task.getQueue()));
        Preconditions.checkNotNull(task.getInput());
        Preconditions.checkNotNull(task.getCreateAt());
        Preconditions.checkNotNull(task.getUpdateAt());

        LOGGER.debug("Run task: {}.", task);
        return repository.create(task);
    }
}
