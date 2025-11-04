package com.example.tasktube.server.api.controllers;

import com.example.tasktube.server.api.requests.FailTaskRequest;
import com.example.tasktube.server.api.requests.FinishTaskRequest;
import com.example.tasktube.server.api.requests.ProcessTaskRequest;
import com.example.tasktube.server.api.requests.StartTaskRequest;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.domain.enties.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

// ADD responseDto

@RestController()
@RequestMapping(path = "/api/v1/task")
public final class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);
    private final ITaskService taskService;

    public TaskController(
            final ITaskService taskService
    ) {
        this.taskService = Objects.requireNonNull(taskService);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    public ResponseEntity<Task> get(
            @PathVariable("id") final UUID taskId
    ) {
        if (Objects.isNull(taskId)) {
            LOGGER.info("Parameter taskId is invalid.");
            return ResponseEntity.badRequest().build();
        }

        return taskService
                .getTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(
            value = "/{id}/start",
            method = RequestMethod.POST
    )
    public ResponseEntity<Void> start(
            @PathVariable("id") final UUID taskId,
            @RequestBody final StartTaskRequest request
    ) {
        if (Objects.isNull(taskId)) {
            LOGGER.info("Parameter taskId is invalid.");
            return ResponseEntity.badRequest().build();
        }

        if (Objects.isNull(request)) {
            LOGGER.info("Parameter request is invalid.");
            return ResponseEntity.badRequest().build();
        }

        taskService.startTask(taskId, request.client(), request.startedAt());

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            value = "/{id}/process",
            method = RequestMethod.POST
    )
    public ResponseEntity<Void> process(
            @PathVariable("id") final UUID taskId,
            @RequestBody final ProcessTaskRequest request
    ) {
        if (Objects.isNull(taskId)) {
            LOGGER.info("Parameter taskId is invalid.");
            return ResponseEntity.badRequest().build();
        }

        if (Objects.isNull(request)) {
            LOGGER.info("Parameter request is invalid.");
            return ResponseEntity.badRequest().build();
        }

        taskService.processTask(taskId, request.client(), request.processedAt());

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            value = "/{id}/finish",
            method = RequestMethod.POST
    )
    public ResponseEntity<Void> finish(
            @PathVariable("id") final UUID taskId,
            @RequestBody final FinishTaskRequest request
    ) {
        if (Objects.isNull(taskId)) {
            LOGGER.info("Parameter taskId is invalid.");
            return ResponseEntity.badRequest().build();
        }

        if (Objects.isNull(request)) {
            LOGGER.info("Parameter request is invalid.");
            return ResponseEntity.badRequest().build();
        }

        taskService.finishTask(request.to(taskId));

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            value = "/{id}/fail",
            method = RequestMethod.POST
    )
    public ResponseEntity<Void> fail(
            @PathVariable("id") final UUID taskId,
            @RequestBody final FailTaskRequest request
    ) {
        if (Objects.isNull(taskId)) {
            LOGGER.info("Parameter taskId is invalid.");
            return ResponseEntity.badRequest().build();
        }

        if (Objects.isNull(request)) {
            LOGGER.info("Parameter request is invalid.");
            return ResponseEntity.badRequest().build();
        }

        taskService.failTask(taskId, request.client(), request.failedAt(), request.failedReason());

        return ResponseEntity.noContent().build();
    }
}
