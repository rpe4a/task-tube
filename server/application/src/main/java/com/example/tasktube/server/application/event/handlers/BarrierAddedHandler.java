package com.example.tasktube.server.application.event.handlers;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.events.logs.BarrierAddedEvent;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.IEventHandler;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class BarrierAddedHandler implements IEventHandler<BarrierAddedEvent> {

    private final IBarrierRepository repository;

    public BarrierAddedHandler(final IBarrierRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Class<BarrierAddedEvent> getEventType() {
        return BarrierAddedEvent.class;
    }

    @Override
    public void handle(final Collection<BarrierAddedEvent> events) {
        final List<Barrier> barriers = events.stream()
                .map(e ->
                        Barrier.create(
                                e.taskId(),
                                e.waitForList(),
                                e.type()
                        )
                )
                .toList();

        repository.save(barriers);
    }

}
