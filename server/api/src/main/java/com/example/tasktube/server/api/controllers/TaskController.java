package com.example.tasktube.server.api.controllers;

import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.domain.enties.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

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
            method = RequestMethod.POST
    )
    public ResponseEntity<Task> get(@PathVariable("id") final UUID taskId) {
        if (Objects.isNull(taskId)) {
            LOGGER.info("Parameter taskId is invalid.");
            return ResponseEntity.badRequest().build();
        }

        return taskService
                .getTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
