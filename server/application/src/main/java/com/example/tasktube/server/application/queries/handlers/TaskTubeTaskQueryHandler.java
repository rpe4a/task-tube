package com.example.tasktube.server.application.queries.handlers;

import com.example.tasktube.server.application.queries.TaskTubeTaskQuery;
import com.example.tasktube.server.application.queries.repositories.ITaskViewRepository;
import com.example.tasktube.server.application.queries.views.TaskTubeTaskView;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public final class TaskTubeTaskQueryHandler {

    private final ITaskViewRepository repository;

    public TaskTubeTaskQueryHandler(final ITaskViewRepository repository) {
        this.repository = repository;
    }

    public Optional<TaskTubeTaskView> handle(final TaskTubeTaskQuery query) {
        Objects.requireNonNull(query);

        return repository.getTaskTubeTask(query.correlationId(), query.taskId());
    }
}
