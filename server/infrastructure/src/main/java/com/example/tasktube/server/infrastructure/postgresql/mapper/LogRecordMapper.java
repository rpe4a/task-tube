package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.domain.enties.LogRecord;
import com.example.tasktube.server.domain.enties.LogRecordLevel;
import com.example.tasktube.server.domain.enties.LogRecordType;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class LogRecordMapper {

    public Map<String, ?> getDataDto(final LogRecord log) {
        final Map<String, Object> map = new HashMap<>();
        map.put("id", log.getId());
        map.put("task_id", log.getTaskId());
        map.put("type", log.getType().toString());
        map.put("level", log.getLevel().toString());
        map.put("timestamp", log.getTimestamp() != null ? Timestamp.from(log.getTimestamp()) : null);
        map.put("message", log.getMessage());
        map.put("exceptionMessage", log.getExceptionMessage());
        map.put("exceptionStackTrace", log.getExceptionStackTrace());
        return map;
    }

    public LogRecord getLogRecord(final ResultSet rs) throws SQLException {
        final LogRecord log = new LogRecord();
        log.setId(rs.getObject("id", UUID.class));
        log.setTaskId(UUID.fromString(rs.getString("task_id")));
        log.setType(LogRecordType.valueOf(rs.getString("type")));
        log.setLevel(LogRecordLevel.valueOf(rs.getString("level")));
        log.setTimestamp(rs.getTimestamp("timestamp") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("timestamp").getTime())
                : null);
        log.setMessage(rs.getString("message"));
        log.setExceptionMessage(rs.getString("exceptionMessage"));
        log.setExceptionStackTrace(rs.getString("exceptionStackTrace"));
        return log;
    }
}
