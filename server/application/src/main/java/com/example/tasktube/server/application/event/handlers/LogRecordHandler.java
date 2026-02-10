package com.example.tasktube.server.application.event.handlers;

import com.example.tasktube.server.domain.enties.LogRecord;
import com.example.tasktube.server.domain.events.logs.LogRecordsAddedEvent;
import com.example.tasktube.server.domain.port.out.IEventHandler;
import com.example.tasktube.server.domain.port.out.ILogRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class LogRecordHandler implements IEventHandler<LogRecordsAddedEvent> {

    private final ILogRecordRepository repository;

    public LogRecordHandler(final ILogRecordRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Class<LogRecordsAddedEvent> getEventType() {
        return LogRecordsAddedEvent.class;
    }

    @Override
    public void handle(final Collection<LogRecordsAddedEvent> events) {
        final List<LogRecord> logs = events
                .stream()
                .flatMap(e -> e.records().stream())
                .toList();

        repository.save(logs);
    }

}
