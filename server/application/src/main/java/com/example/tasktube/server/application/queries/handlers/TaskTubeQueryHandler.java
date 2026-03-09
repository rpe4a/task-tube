package com.example.tasktube.server.application.queries.handlers;

import com.example.tasktube.server.application.queries.TaskTubeQuery;
import com.example.tasktube.server.application.queries.repositories.ITaskViewRepository;
import com.example.tasktube.server.application.queries.views.TaskTubeView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public final class TaskTubeQueryHandler {

    private final ITaskViewRepository repository;

    public TaskTubeQueryHandler(final ITaskViewRepository repository) {
        this.repository = repository;
    }

    public List<TaskTubeView> handle(final TaskTubeQuery query) {
        Objects.requireNonNull(query);

        return repository.getTaskTube(query.correlationId());
    }
}
