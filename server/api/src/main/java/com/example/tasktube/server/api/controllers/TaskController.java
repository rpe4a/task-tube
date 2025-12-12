package com.example.tasktube.server.api.controllers;

import com.example.tasktube.server.api.requests.FailTaskRequest;
import com.example.tasktube.server.api.requests.FinishTaskRequest;
import com.example.tasktube.server.api.requests.ProcessTaskRequest;
import com.example.tasktube.server.api.requests.StartTaskRequest;
import com.example.tasktube.server.application.port.in.ITaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController()
@RequestMapping(path = "/api/v1/task")
public final class TaskController extends AbstractController {
    private final ITaskService taskService;

    public TaskController(
            final ITaskService taskService
    ) {
        this.taskService = Objects.requireNonNull(taskService);
    }

    @RequestMapping(
            value = "/{id}/start",
            method = RequestMethod.POST
    )
    public ResponseEntity<Void> start(
            @PathVariable("id") final UUID taskId,
            @NotNull @Valid @RequestBody final StartTaskRequest request,
            final BindingResult bindingResult
    ) {
        if (isInvalid(bindingResult)) {
            return ResponseEntity.badRequest().build();
        }

        taskService.startTask(taskId, request.startedAt(), request.client());

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            value = "/{id}/process",
            method = RequestMethod.POST
    )
    public ResponseEntity<Void> process(
            @PathVariable("id") final UUID taskId,
            @NotNull @Valid @RequestBody final ProcessTaskRequest request,
            final BindingResult bindingResult
    ) {
        if (isInvalid(bindingResult)) {
            return ResponseEntity.badRequest().build();
        }

        taskService.processTask(taskId, request.processedAt(), request.client());

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            value = "/{id}/finish",
            method = RequestMethod.POST
    )
    public ResponseEntity<Void> finish(
            @PathVariable("id") final UUID taskId,
            @NotNull @Valid @RequestBody final FinishTaskRequest request,
            final BindingResult bindingResult
    ) {
        if (isInvalid(bindingResult)) {
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
            @NotNull @Valid @RequestBody final FailTaskRequest request,
            final BindingResult bindingResult
    ) {
        if (isInvalid(bindingResult)) {
            return ResponseEntity.badRequest().build();
        }

        taskService.failTask(taskId, request.failedAt(), request.failedReason(), request.client());

        return ResponseEntity.noContent().build();
    }
}
