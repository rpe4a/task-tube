package com.example.tasktube.server.application.event.handlers;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.events.logs.BarrierReleasedEvent;
import com.example.tasktube.server.domain.port.out.IEventHandler;
import com.example.tasktube.server.domain.port.out.IEventPublisher;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

@Service
public class BarrierReleasedHandler implements IEventHandler<BarrierReleasedEvent> {

    private final ITaskRepository repository;
    private final IEventPublisher eventPublisher;

    public BarrierReleasedHandler(final ITaskRepository repository, final IEventPublisher eventPublisher) {
        this.repository = Objects.requireNonNull(repository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Class<BarrierReleasedEvent> getEventType() {
        return BarrierReleasedEvent.class;
    }

    @Override
    public void handle(final Collection<BarrierReleasedEvent> events) {
        events.forEach(event -> {
            final Barrier barrier = event.barrier();
            final String client = event.client();

            final Task task = repository.get(barrier.getTaskId(), client).orElseThrow();

            task.releaseBarrier(barrier, client);

            repository.update(task);

            eventPublisher.publish(task.pullEvents());
        });
    }

}
