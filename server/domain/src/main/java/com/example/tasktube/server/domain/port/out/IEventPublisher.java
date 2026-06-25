package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.events.DomainEvent;

import java.util.List;

public interface IEventPublisher {

    void publish(DomainEvent event);

    void publish(List<DomainEvent> events);

}
