package com.example.tasktube.server.api.controllers;

import com.example.tasktube.server.api.requests.PopTaskRequest;
import com.example.tasktube.server.api.requests.PopTaskResponse;
import com.example.tasktube.server.api.requests.TaskRequest;
import com.example.tasktube.server.application.port.in.ITubeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(path = "/api/v1/tube")
public final class TubeController extends AbstractController {
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
            @NotNull @Valid @RequestBody final TaskRequest request,
            final BindingResult result
    ) {
        if (isInvalid(result)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                tubeService.push(request.to())
        );
    }

    @RequestMapping(
            value = "/{name}/pop",
            method = RequestMethod.POST
    )
    public ResponseEntity<PopTaskResponse> pop(
            @PathVariable("name") final String tube,
            @NotNull @Valid @RequestBody final PopTaskRequest request,
            final BindingResult result
    ) {
        if (isInvalid(result)) {
            return ResponseEntity.badRequest().build();
        }

        return tubeService.pop(tube, request.client())
                .map(popTaskDto ->
                        ResponseEntity.ok(
                                PopTaskResponse.from(popTaskDto)
                        )
                )
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
