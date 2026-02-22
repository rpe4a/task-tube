package com.example.tasktube.server.ui.controllers;

import com.example.tasktube.server.ui.responses.TaskPageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping(path = "/api/v1/tasks")
public final class TasksController extends AbstractController {

    @RequestMapping(
            method = RequestMethod.GET
    )
    public ResponseEntity<TaskPageResponse[]> getTasks(
            @RequestParam(required = false, name = "page", defaultValue = "0") final int page,
            @RequestParam(required = false, name = "size", defaultValue = "100") final int size,
            @RequestParam(required = false, name = "id") final UUID id,
            @RequestParam(required = false, name = "name") final String name,
            @RequestParam(required = false, name = "tube") final String tube,
            @RequestParam(required = false, name = "status") final String status,
            @RequestParam(required = false, name = "createdFrom") final Instant createdFrom,
            @RequestParam(required = false, name = "createdTo") final Instant createdTo
    ) {
        final List<TaskPageResponse> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add(new TaskPageResponse(
                            UUID.randomUUID(),
                            "task" + i,
                            "tube_test",
                            "CREATE",
                            Instant.now(),
                            Instant.now(),
                            Instant.now(),
                            Instant.now(),
                            "worker"
                    )
            );
        }

        return ResponseEntity.ok(list.toArray(new TaskPageResponse[0]));
    }
}
