package com.example.tasktube.server.application.queries.handlers;

import com.example.tasktube.server.application.queries.TaskArgumentsQuery;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.values.argument.Argument;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public final class TaskArgumentsQueryHandler {

    private final ITaskRepository repository;
    private final IArgumentFiller argumentFiller;

    public TaskArgumentsQueryHandler(
            final ITaskRepository repository,
            final IArgumentFiller argumentFiller
    ) {
        this.repository = repository;
        this.argumentFiller = argumentFiller;
    }

    public Optional<List<Argument>> handle(final TaskArgumentsQuery query) {
        Objects.requireNonNull(query);

        final Task task = repository.get(query.taskId()).get();

        return task.isCreated()
                ? Optional.empty()
                : Optional.of(task.getArguments(argumentFiller));
    }
}
