package com.example.tasktube.server.api.controllers;

import com.example.tasktube.server.api.requests.PopTaskRequest;
import com.example.tasktube.server.api.responses.PopTaskResponse;
import com.example.tasktube.server.api.requests.PopTasksRequest;
import com.example.tasktube.server.api.requests.TaskRequest;
import com.example.tasktube.server.application.port.in.ITubeService;
import com.example.tasktube.server.infrastructure.configuration.InstanceIdProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController()
@RequestMapping(path = "/api/v1/tube")
public final class TubeController extends AbstractController {

    private final ITubeService tubeService;
    private final InstanceIdProvider instanceId;

    public TubeController(
            final ITubeService tubeService,
            final InstanceIdProvider instanceId

    ) {
        this.tubeService = Objects.requireNonNull(tubeService);
        this.instanceId = Objects.requireNonNull(instanceId);
    }

    @RequestMapping(
            value = "/{name}/push",
            method = RequestMethod.POST
    )
    public ResponseEntity<UUID> push(
            @PathVariable("name") final String tube,
            @NotNull @Valid @RequestBody final TaskRequest request,
            final BindingResult bindingResult
    ) {
        if (isInvalid(bindingResult)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                tubeService.push(request.to(), instanceId.get())
        );
    }

    @RequestMapping(
            value = "/{name}/pop",
            method = RequestMethod.POST
    )
    public ResponseEntity<PopTaskResponse> pop(
            @PathVariable("name") final String tube,
            @NotNull @Valid @RequestBody final PopTaskRequest request,
            final BindingResult bindingResult
    ) {
        if (isInvalid(bindingResult)) {
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

    // TODO - tests
    @RequestMapping(
            value = "/{name}/pop/list",
            method = RequestMethod.POST
    )
    public ResponseEntity<List<PopTaskResponse>> popList(
            @PathVariable("name") final String tube,
            @NotNull @Valid @RequestBody final PopTasksRequest request,
            final BindingResult bindingResult
    ) {
        if (isInvalid(bindingResult)) {
            return ResponseEntity.badRequest().build();
        }

        final List<PopTaskResponse> responses = tubeService.popList(tube, request.client(), request.count())
                .stream()
                .map(PopTaskResponse::from)
                .toList();

        return responses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responses);

    }

}
