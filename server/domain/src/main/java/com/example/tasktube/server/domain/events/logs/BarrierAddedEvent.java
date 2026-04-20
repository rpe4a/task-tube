package com.example.tasktube.server.domain.events.logs;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.events.DomainEvent;

import java.util.List;
import java.util.UUID;

public record BarrierAddedEvent(
        UUID taskId,
        List<UUID> waitForList,
        Barrier.Type type
) implements DomainEvent {
}
