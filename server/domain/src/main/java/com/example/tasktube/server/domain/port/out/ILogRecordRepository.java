package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.LogRecord;

import java.util.List;

public interface ILogRecordRepository {

    void save(LogRecord log);

    void save(List<LogRecord> logs);
}
