package com.example.tasktube.server.domain.events.logs;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.events.DomainEvent;

import java.util.List;
import java.util.UUID;

public record TaskChildrenAddedEvent(
        UUID taskId,
        List<Task> children
) implements DomainEvent {
}
