package com.example.tasktube.server.ui.controllers;

import com.example.tasktube.server.application.port.in.ITubeService;
import com.example.tasktube.server.application.queries.TaskArgumentsQuery;
import com.example.tasktube.server.application.queries.TaskLogsQuery;
import com.example.tasktube.server.application.queries.TaskTubeQuery;
import com.example.tasktube.server.application.queries.TaskTubeTaskQuery;
import com.example.tasktube.server.application.queries.TaskTubeTreeNodeQuery;
import com.example.tasktube.server.application.queries.handlers.TaskArgumentsQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskLogsQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskTubeQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskTubeTaskQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskTubeTreeNodeQueryHandler;
import com.example.tasktube.server.application.queries.views.TaskLogView;
import com.example.tasktube.server.application.queries.views.TaskTubeTaskView;
import com.example.tasktube.server.application.queries.views.TaskTubeTreeNodeView;
import com.example.tasktube.server.application.queries.views.TaskTubeView;
import com.example.tasktube.server.domain.values.argument.Argument;
import com.example.tasktube.server.infrastructure.configuration.InstanceIdProvider;
import com.example.tasktube.server.ui.responses.TaskPushRequest;
import com.example.tasktube.server.ui.responses.TaskTubePageDto;
import com.example.tasktube.server.ui.responses.TaskTubePageResponse;
import com.example.tasktube.server.ui.responses.TaskTubeTaskLogDto;
import com.example.tasktube.server.ui.responses.TaskTubeTaskLogsResponse;
import com.example.tasktube.server.ui.responses.TaskTubeTaskResponse;
import com.example.tasktube.server.ui.responses.TaskTubeTreeNode;
import com.example.tasktube.server.ui.responses.TaskTubeTreeNodeResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController()
@RequestMapping(path = "/api/v1/tasktube")
public final class TaskTubeController extends AbstractController {

    private final TaskTubeQueryHandler queryHandler;
    private final TaskTubeTaskQueryHandler taskTubeTaskQueryHandler;
    private final TaskTubeTreeNodeQueryHandler taskTubeTreeNodeQueryHandler;
    private final TaskLogsQueryHandler taskLogsQueryHandler;
    private final TaskArgumentsQueryHandler taskArgumentsQueryHandler;
    private final ITubeService tubeService;
    private final InstanceIdProvider instanceId;

    public TaskTubeController(
            final TaskTubeQueryHandler queryHandler,
            final TaskTubeTaskQueryHandler taskTubeTaskQueryHandler,
            final TaskTubeTreeNodeQueryHandler taskTubeTreeNodeQueryHandler,
            final TaskLogsQueryHandler taskLogsQueryHandler,
            final TaskArgumentsQueryHandler taskArgumentsQueryHandler,
            final ITubeService tubeService,
            final InstanceIdProvider instanceId
    ) {
        this.queryHandler = Objects.requireNonNull(queryHandler);
        this.taskTubeTaskQueryHandler = Objects.requireNonNull(taskTubeTaskQueryHandler);
        this.taskTubeTreeNodeQueryHandler = Objects.requireNonNull(taskTubeTreeNodeQueryHandler);
        this.taskLogsQueryHandler = Objects.requireNonNull(taskLogsQueryHandler);
        this.taskArgumentsQueryHandler = Objects.requireNonNull(taskArgumentsQueryHandler);
        this.tubeService = Objects.requireNonNull(tubeService);
        this.instanceId = Objects.requireNonNull(instanceId);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "{correlationId}"
    )
    public ResponseEntity<TaskTubePageResponse> getTaskTube(@PathVariable final String correlationId) {
        final List<TaskTubeView> tasks = queryHandler.handle(new TaskTubeQuery(correlationId));

        return ResponseEntity.ok(
                new TaskTubePageResponse(
                        tasks.stream()
                                .map(t -> new TaskTubePageDto(
                                        t.getId(),
                                        t.getName(),
                                        t.getStatus().name(),
                                        t.getParentId(),
                                        t.getCreatedAt(),
                                        t.getAbortedAt(),
                                        t.getCompletedAt()
                                ))
                                .toArray(TaskTubePageDto[]::new)
                )
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/{correlationId}/task/{taskId}"
    )
    public ResponseEntity<TaskTubeTaskResponse> getTaskTubeTask(
            @PathVariable(name = "correlationId") final String correlationId,
            @PathVariable(name = "taskId") final UUID taskId
    ) {
        final Optional<TaskTubeTaskView> task = taskTubeTaskQueryHandler.handle(new TaskTubeTaskQuery(correlationId, taskId));

        if (task.isPresent()) {
            final TaskTubeTaskView taskView = task.get();
            return ResponseEntity.ok(
                    new TaskTubeTaskResponse(
                            taskView.getId(),
                            taskView.getName(),
                            taskView.getTube(),
                            taskView.getStatus(),
                            taskView.getCorrelationId(),
                            taskView.getParentId(),
                            taskView.getInput(),
                            taskView.getOutput(),
                            taskView.getUpdatedAt(),
                            taskView.getCreatedAt(),
                            taskView.getCanceledAt(),
                            taskView.getScheduledAt(),
                            taskView.getStartedAt(),
                            taskView.getHeartbeatAt(),
                            taskView.getFinishedAt(),
                            taskView.getFailedAt(),
                            taskView.getAbortedAt(),
                            taskView.getCompletedAt(),
                            taskView.getFailures(),
                            taskView.getFailedReason(),
                            taskView.getSettings(),
                            taskView.getHandledBy(),
                            taskView.getCountChildren()
                    )
            );
        }

        return (ResponseEntity<TaskTubeTaskResponse>) ResponseEntity.notFound();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/{correlationId}/task/{taskId}/treenode"
    )
    public ResponseEntity<TaskTubeTreeNodeResponse> getTaskTubeTreeNode(
            @PathVariable(name = "correlationId") final String correlationId,
            @PathVariable(name = "taskId") final UUID taskId
    ) {
        final List<TaskTubeTreeNodeView> tasks = taskTubeTreeNodeQueryHandler.handle(new TaskTubeTreeNodeQuery(correlationId, taskId));

        final TaskTubeTreeNode root = tasks.stream()
                .filter(t -> taskId.equals(t.getId()))
                .map(t ->
                        new TaskTubeTreeNode(
                                t.getId(),
                                t.getName(),
                                t.getStatus().name(),
                                t.getParentId(),
                                t.getCreatedAt(),
                                t.getScheduledAt(),
                                t.getStartedAt(),
                                t.getFinishedAt(),
                                t.getAbortedAt(),
                                t.getCanceledAt(),
                                t.getCompletedAt(),
                                t.getChildrenCount()
                        )
                )
                .findFirst()
                .get();

        final TaskTubeTreeNode[] children = tasks.stream()
                .filter(t -> taskId.equals(t.getParentId()))
                .map(t ->
                        new TaskTubeTreeNode(
                                t.getId(),
                                t.getName(),
                                t.getStatus().name(),
                                t.getParentId(),
                                t.getCreatedAt(),
                                t.getScheduledAt(),
                                t.getStartedAt(),
                                t.getFinishedAt(),
                                t.getAbortedAt(),
                                t.getCanceledAt(),
                                t.getCompletedAt(),
                                t.getChildrenCount()
                        )
                )
                .toArray(TaskTubeTreeNode[]::new);

        return ResponseEntity.ok(
                new TaskTubeTreeNodeResponse(
                        root,
                        children
                )
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/{correlationId}/task/{taskId}/logs"
    )
    public ResponseEntity<TaskTubeTaskLogsResponse> getTaskTubeTaskLogs(
            @PathVariable(name = "correlationId") final String correlationId,
            @PathVariable(name = "taskId") final UUID taskId,
            @RequestParam(required = false, name = "page", defaultValue = "0") final int page,
            @RequestParam(required = false, name = "size", defaultValue = "100") final int size
    ) {
        final List<TaskLogView> logs = taskLogsQueryHandler.handle(
                new TaskLogsQuery(
                        taskId,
                        page,
                        size
                )
        );

        return ResponseEntity.ok(new TaskTubeTaskLogsResponse(
                        logs.stream()
                                .map(l -> new TaskTubeTaskLogDto(
                                                l.getId(),
                                                l.getTaskId(),
                                                l.getType(),
                                                l.getLevel(),
                                                l.getTimestamp(),
                                                l.getMessage(),
                                                l.getExceptionMessage(),
                                                l.getExceptionStackTrace()
                                        )
                                ).toArray(TaskTubeTaskLogDto[]::new),
                        logs.isEmpty()
                                ? 0L
                                : logs.getFirst().getTotalCount()
                )
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/{correlationId}/task/{taskId}/arguments"
    )
    public ResponseEntity<List<Argument>> getTaskTubeTaskArguments(
            @PathVariable(name = "correlationId") final String correlationId,
            @PathVariable(name = "taskId") final UUID taskId
    ) {
        final Optional<List<Argument>> arguments = taskArgumentsQueryHandler.handle(
                new TaskArgumentsQuery(
                        taskId
                )
        );

        return arguments
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/push"
    )
    public ResponseEntity<UUID> postPushTask(
            @NotNull @Valid @RequestBody final TaskPushRequest request,
            final BindingResult bindingResult
    ) {
        if (isInvalid(bindingResult)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                tubeService.push(request.to(), instanceId.get())
        );
    }
}
