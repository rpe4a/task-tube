package com.example.tasktube.server.infrastructure.event;

import com.example.tasktube.server.domain.events.DomainEvent;
import com.example.tasktube.server.domain.port.out.IEventHandler;
import com.example.tasktube.server.domain.port.out.IEventPublisher;
import com.example.tasktube.server.infrastructure.postgresql.repository.TaskViewRepository;
import com.google.common.collect.HashMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ApplicationEventPublisher implements IEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskViewRepository.class);

    private final EventHandlerRegistry registry;

    public ApplicationEventPublisher(final EventHandlerRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
    }

    @Override
    public void publish(final List<DomainEvent> events) {
        Objects.requireNonNull(events);
        if (events.isEmpty()) {
            return;
        }

        final HashMultimap<Class<? extends DomainEvent>, DomainEvent> eventMap = HashMultimap.create();

        events.forEach(event -> {
            eventMap.put(event.getClass(), event);
        });

        eventMap.keySet().forEach(eventType -> {
            final Set<IEventHandler> handlers = registry.getHandlers(eventType);
            if (Objects.isNull(handlers)) {
                throw new IllegalArgumentException("No handler registered for " + eventType);
            }

            handlers.forEach(
                    handler -> {
                        LOGGER.debug("'{}' starts handling '{}' events of type '{}'.", handler.getClass(), eventMap.get(eventType).size(), eventType);
                        handler.handle(eventMap.get(eventType));
                    }
            );
        });
    }
}
