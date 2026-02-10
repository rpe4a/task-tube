package com.example.tasktube.server.infrastructure.event;

import com.example.tasktube.server.domain.events.DomainEvent;
import com.example.tasktube.server.domain.port.out.IEventHandler;
import com.google.common.collect.HashMultimap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class EventHandlerRegistry {

    private final HashMultimap<Class<? extends DomainEvent>, IEventHandler> handlers = HashMultimap.create();

    public EventHandlerRegistry(final List<IEventHandler<?>> handlers) {
        for (final IEventHandler<?> handler : Objects.requireNonNull(handlers)) {
            this.handlers.put(handler.getEventType(), handler);
        }
    }

    public <TEvent extends DomainEvent> Set<IEventHandler> getHandlers(final Class<TEvent> eventType) {
        return handlers.get(eventType);
    }
}
