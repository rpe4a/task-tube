package com.example.tasktube.server.application.event.handlers;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Entity;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.events.DomainEvent;
import com.example.tasktube.server.domain.events.logs.BarrierAddedEvent;
import com.example.tasktube.server.domain.events.logs.TaskChildrenAddedEvent;
import com.example.tasktube.server.domain.port.out.IEventHandler;
import com.example.tasktube.server.domain.port.out.IEventPublisher;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TaskChildrenAddedHandler implements IEventHandler<TaskChildrenAddedEvent> {

    private final ITubeRepository repository;
    private final IEventPublisher eventPublisher;

    public TaskChildrenAddedHandler(final ITubeRepository repository, final IEventPublisher eventPublisher) {
        this.repository = Objects.requireNonNull(repository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Class<TaskChildrenAddedEvent> getEventType() {
        return TaskChildrenAddedEvent.class;
    }

    @Override
    public void handle(final Collection<TaskChildrenAddedEvent> events) {
        events.forEach(event -> {
            final UUID taskId = event.taskId();
            final List<Task> children = event.children();

            repository.push(children);

            final List<DomainEvent> childrenEvents = new ArrayList<>(
                    children.stream()
                            .flatMap(t -> t.pullEvents().stream())
                            .toList());

            childrenEvents.add(
                    BarrierAddedEvent.create(
                            taskId,
                            children.stream().map(Entity::getId).toList(),
                            Barrier.Type.FINISH
                    )
            );

            eventPublisher.publish(childrenEvents);
        });
    }

}
