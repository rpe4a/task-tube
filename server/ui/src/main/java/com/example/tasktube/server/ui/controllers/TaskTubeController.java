package com.example.tasktube.server.ui.controllers;

import com.example.tasktube.server.application.queries.TaskTubeQuery;
import com.example.tasktube.server.application.queries.TaskTubeTreeNodeQuery;
import com.example.tasktube.server.application.queries.TaskTubeTaskQuery;
import com.example.tasktube.server.application.queries.handlers.TaskTubeTaskQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskTubeQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskTubeTreeNodeQueryHandler;
import com.example.tasktube.server.application.queries.views.TaskTubeTreeNodeView;
import com.example.tasktube.server.application.queries.views.TaskTubeTaskView;
import com.example.tasktube.server.application.queries.views.TaskTubeView;
import com.example.tasktube.server.ui.responses.TaskTubePageDto;
import com.example.tasktube.server.ui.responses.TaskTubePageResponse;
import com.example.tasktube.server.ui.responses.TaskTubeTaskResponse;
import com.example.tasktube.server.ui.responses.TaskTubeTreeNode;
import com.example.tasktube.server.ui.responses.TaskTubeTreeNodeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    public TaskTubeController(
            final TaskTubeQueryHandler queryHandler,
            final TaskTubeTaskQueryHandler taskTubeTaskQueryHandler,
            final TaskTubeTreeNodeQueryHandler taskTubeTreeNodeQueryHandler
    ) {
        this.queryHandler = Objects.requireNonNull(queryHandler);
        this.taskTubeTaskQueryHandler = Objects.requireNonNull(taskTubeTaskQueryHandler);
        this.taskTubeTreeNodeQueryHandler = Objects.requireNonNull(taskTubeTreeNodeQueryHandler);
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
    public ResponseEntity<TaskTubeTreeNodeResponse> getTaskTubeTaskChildren(
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
}
