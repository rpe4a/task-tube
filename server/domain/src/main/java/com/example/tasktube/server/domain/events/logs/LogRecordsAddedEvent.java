package com.example.tasktube.server.domain.events.logs;

import com.example.tasktube.server.domain.enties.LogRecord;
import com.example.tasktube.server.domain.events.DomainEvent;

import java.util.List;

public record LogRecordsAddedEvent(List<LogRecord> records) implements DomainEvent {
}
