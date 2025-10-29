package com.example.tasktube.server.api.controllers;

import com.example.tasktube.server.api.controllers.requests.PopTaskRequest;
import com.example.tasktube.server.api.controllers.requests.PushTaskRequest;
import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.application.port.in.ITubeService;
import com.google.common.base.Strings;
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

@RestController()
@RequestMapping(path = "/api/v1/tube")
public final class TubeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TubeController.class);

    private final ITubeService tubeService;

    public TubeController(
            final ITubeService tubeService
    ) {
        this.tubeService = Objects.requireNonNull(tubeService);
    }

    @RequestMapping(
            value = "/{name}/push",
            method = RequestMethod.POST
    )
    public ResponseEntity<UUID> push(
            @PathVariable("name") final String tube,
            @RequestBody final PushTaskRequest request
    ) {
        if (Objects.isNull(request) || Strings.isNullOrEmpty(request.getName())) {
            LOGGER.info("Parameter request is invalid.");
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                tubeService.push(request.toPushTaskDto(tube))
        );
    }

    @RequestMapping(
            value = "/{name}/pop",
            method = RequestMethod.POST
    )
    public ResponseEntity<TaskDto> pop(
            @PathVariable("name") final String tube,
            @RequestBody final PopTaskRequest request
    ) {
        if (Objects.isNull(request) || Strings.isNullOrEmpty(request.client())) {
            LOGGER.info("Parameter request is invalid.");
            return ResponseEntity.badRequest().build();
        }
        return tubeService.pop(tube, request.client())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
