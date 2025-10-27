package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.models.RunningTaskDto;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService implements ITaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final ITaskRepository repository;

    public TaskService(final ITaskRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Transactional
    public Optional<Task> getTaskById(final UUID taskId) {
        Preconditions.checkNotNull(taskId);

        LOGGER.debug("Get task by id: {}.", taskId);
        return repository.getById(taskId);
    }

    @Transactional
    public UUID runTask(final RunningTaskDto task) {
        Preconditions.checkNotNull(task);

        LOGGER.debug("Run task: {}.", task);
        return repository
                .create(
                        Task.getRunningTask(
                                task.name(),
                                task.queue(),
                                task.input(),
                                task.createAt()
                        )
                )
                .getId();
    }
}
