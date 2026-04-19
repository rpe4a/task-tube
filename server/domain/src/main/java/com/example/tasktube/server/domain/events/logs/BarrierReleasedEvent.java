package com.example.tasktube.server.domain.events.logs;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.events.DomainEvent;

public record BarrierReleasedEvent(
        Barrier barrier,
        String client
) implements DomainEvent {
}
