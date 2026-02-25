package com.example.tasktube.server.application.queries.handlers;

import com.example.tasktube.server.application.queries.ParentTasksQuery;
import com.example.tasktube.server.application.queries.repositories.ITaskViewRepository;
import com.example.tasktube.server.application.queries.results.ParentTasksResult;
import com.example.tasktube.server.application.queries.views.ParentTaskView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public final class ParentTasksQueryHandler {

    private final ITaskViewRepository repository;

    public ParentTasksQueryHandler(final ITaskViewRepository repository) {
        this.repository = repository;
    }

    public List<ParentTaskView> handle(final ParentTasksQuery query) {
        Objects.requireNonNull(query);

        return repository.getParentTaskList(
                query.taskId(),
                query.taskName(),
                query.tube(),
                query.status(),
                query.createdFrom(),
                query.createdTo(),
                query.page(),
                query.size()
        );
    }
}
