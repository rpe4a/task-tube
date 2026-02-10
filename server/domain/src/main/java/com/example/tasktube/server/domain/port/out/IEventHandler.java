package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.events.DomainEvent;

import java.util.Collection;

public interface IEventHandler<TEvent extends DomainEvent> {

    Class<TEvent> getEventType();

    void handle(Collection<TEvent> events);
}
