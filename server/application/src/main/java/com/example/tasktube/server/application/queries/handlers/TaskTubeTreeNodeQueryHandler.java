package com.example.tasktube.server.application.queries.handlers;

import com.example.tasktube.server.application.queries.TaskTubeTreeNodeQuery;
import com.example.tasktube.server.application.queries.repositories.ITaskViewRepository;
import com.example.tasktube.server.application.queries.views.TaskTubeTreeNodeView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public final class TaskTubeTreeNodeQueryHandler {

    private final ITaskViewRepository repository;

    public TaskTubeTreeNodeQueryHandler(final ITaskViewRepository repository) {
        this.repository = repository;
    }

    public List<TaskTubeTreeNodeView> handle(final TaskTubeTreeNodeQuery query) {
        Objects.requireNonNull(query);

        return repository.getTaskTubeTreeNode(query.correlationId(), query.taskId());
    }
}
