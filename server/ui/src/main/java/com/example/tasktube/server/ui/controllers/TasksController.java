package com.example.tasktube.server.ui.controllers;

import com.example.tasktube.server.application.queries.ParentTasksQuery;
import com.example.tasktube.server.application.queries.handlers.ParentTasksQueryHandler;
import com.example.tasktube.server.application.queries.views.ParentTaskView;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.ui.responses.TasksPageDto;
import com.example.tasktube.server.ui.responses.TasksPageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController()
@RequestMapping(path = "/api/v1/tasks")
public final class TasksController extends AbstractController {

    private final ParentTasksQueryHandler queryHandler;

    public TasksController(final ParentTasksQueryHandler queryHandler) {
        this.queryHandler = Objects.requireNonNull(queryHandler);
    }

    @RequestMapping(
            method = RequestMethod.GET
    )
    public ResponseEntity<TasksPageResponse> getTasks(
            @RequestParam(required = false, name = "page", defaultValue = "0") final int page,
            @RequestParam(required = false, name = "size", defaultValue = "100") final int size,
            @RequestParam(required = false, name = "id") final UUID id,
            @RequestParam(required = false, name = "name") final String name,
            @RequestParam(required = false, name = "tube") final String tube,
            @RequestParam(required = false, name = "status") final String status,
            @RequestParam(required = false, name = "createdFrom") final Instant createdFrom,
            @RequestParam(required = false, name = "createdTo") final Instant createdTo,
            @RequestParam(required = false, name = "sort", defaultValue = "created_at") final String sort,
            @RequestParam(required = false, name = "by", defaultValue = "DESC") final String by
    ) {
        final List<ParentTaskView> tasks = queryHandler.handle(
                new ParentTasksQuery(
                        id,
                        name,
                        tube,
                        status != null ? Task.Status.valueOf(status) : null,
                        createdFrom,
                        createdTo,
                        sort,
                        by,
                        page,
                        size
                )
        );

        return ResponseEntity.ok(new TasksPageResponse(
                        tasks.stream()
                                .map(t -> new TasksPageDto(
                                                t.getId(),
                                                t.getName(),
                                                t.getTube(),
                                                t.getStatus().name(),
                                                t.getCorrelationId(),
                                                t.getUpdatedAt(),
                                                t.getCreatedAt(),
                                                t.getAbortedAt(),
                                                t.getCancelledAt(),
                                                t.getCompletedAt(),
                                                t.getHandledBy(),
                                                t.isTerminationRequested(),
                                                t.isRecoveryRequested()
                                        )
                                ).toArray(TasksPageDto[]::new),
                        tasks.isEmpty()
                                ? 0L
                                : tasks.getFirst().getTotalCount()
                )
        );
    }
}
