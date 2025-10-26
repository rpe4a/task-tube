package com.example.tasktube.server.api.controllers;

import com.example.tasktube.server.api.controllers.dtos.RunningTaskDto;
import com.example.tasktube.server.core.enties.Task;
import com.example.tasktube.server.core.services.RunTaskService;
import com.example.tasktube.server.core.services.TaskService;
import com.google.common.base.Preconditions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController()
@RequestMapping(path = "/api/v1/task")
public final class TaskController {

    private final RunTaskService runTaskService;
    private final TaskService taskService;

    public TaskController(
            final RunTaskService runTaskService,
            final TaskService taskService
    ) {
        this.runTaskService = runTaskService;
        this.taskService = taskService;
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.POST
    )
    public UUID create(@RequestBody final RunningTaskDto runningTaskDto) {
        Preconditions.checkNotNull(runningTaskDto);
        return runTaskService.runTask(
                        Task.getRunningTask(
                                runningTaskDto.name(),
                                runningTaskDto.queueName(),
                                runningTaskDto.input(),
                                runningTaskDto.createdAt()
                        )
                )
                .getId();
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
