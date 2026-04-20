package com.example.tasktube.server.domain.events.logs;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.events.DomainEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BarrierAddedEvent(
        UUID taskId,
        List<UUID> waitForList,
        Barrier.Type type,
        Instant createdAt
) implements DomainEvent {

    public static BarrierAddedEvent create(
            final UUID taskId,
            final List<UUID> waitForList,
            final Barrier.Type type
    ) {
        return new BarrierAddedEvent(taskId, waitForList, type, Instant.now());
    }
}
