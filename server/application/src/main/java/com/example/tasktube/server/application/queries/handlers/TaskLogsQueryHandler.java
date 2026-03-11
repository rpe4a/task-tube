package com.example.tasktube.server.application.queries.handlers;

import com.example.tasktube.server.application.queries.TaskLogsQuery;
import com.example.tasktube.server.application.queries.repositories.ITaskLogViewRepository;
import com.example.tasktube.server.application.queries.views.TaskLogView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public final class TaskLogsQueryHandler {

    private final ITaskLogViewRepository repository;

    public TaskLogsQueryHandler(final ITaskLogViewRepository repository) {
        this.repository = repository;
    }

    public List<TaskLogView> handle(final TaskLogsQuery query) {
        Objects.requireNonNull(query);

        return repository.getTaskLogList(query.taskId(), query.page(), query.size());
    }
}
