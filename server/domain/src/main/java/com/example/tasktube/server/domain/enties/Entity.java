package com.example.tasktube.server.domain.enties;

import com.example.tasktube.server.domain.events.DomainEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class Entity<TKey> {

    protected final List<DomainEvent> events = new LinkedList<>();

    private TKey id;

    public Entity(final TKey id) {
        this.id = id;
    }

    public TKey getId() {
        return id;
    }

    public void setId(final TKey id) {
        this.id = Objects.requireNonNull(id);
    }

    protected void addEvent(final DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> pullEvents() {
        final List<DomainEvent> copiedEvents = List.copyOf(events);
        events.clear();
        return copiedEvents;
    }
}
