package com.example.tasktube.server.api.controllers;

import com.example.tasktube.server.api.controllers.requests.RunningTaskRequest;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.domain.enties.Task;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController()
@RequestMapping(path = "/api/v1/task")
public final class TaskController {

    private final ITaskService taskService;

    public TaskController(
            final ITaskService taskService
    ) {
        this.taskService = Objects.requireNonNull(taskService);
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.POST
    )
    public ResponseEntity<UUID> create(@RequestBody final RunningTaskRequest request) {
        if (Objects.isNull(request)
                || Strings.isNullOrEmpty(request.getName())
                || Strings.isNullOrEmpty(request.getQueueName())
        ) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                taskService.runTask(request.toRunningTaskDto())
        );
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    public ResponseEntity<Task> get(@PathVariable("id") final UUID taskId) {
        Preconditions.checkNotNull(taskId);

        return taskService
                .getTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
