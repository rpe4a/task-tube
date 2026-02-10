package com.example.tasktube.server.domain.enties;

import java.time.Instant;
import java.util.UUID;

public class LogRecord extends Entity<UUID> {

    private UUID taskId;
    private LogRecordType type;
    private LogRecordLevel level;
    private Instant timestamp;
    private String message;
    private String exceptionMessage;
    private String exceptionStackTrace;

    public LogRecord() {
        super(UUID.randomUUID());
    }

    public LogRecord(final UUID taskId,
                     final LogRecordType type,
                     final Instant timestamp,
                     final LogRecordLevel level,
                     final String message,
                     final String exceptionMessage,
                     final String exceptionStackTrace
    ) {
        this(UUID.randomUUID(), taskId, type, timestamp, level, message, exceptionMessage, exceptionStackTrace);
    }

    public LogRecord(
            final UUID id,
            final UUID taskId,
            final LogRecordType type,
            final Instant timestamp,
            final LogRecordLevel level,
            final String message,
            final String exceptionMessage,
            final String exceptionStackTrace
    ) {
        super(id);
        this.taskId = taskId;
        this.type = type;
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.exceptionMessage = exceptionMessage;
        this.exceptionStackTrace = exceptionStackTrace;
    }

    public static LogRecord info(final UUID taskId, final String message) {
        return new LogRecord(
                taskId,
                LogRecordType.SERVER,
                Instant.now(),
                LogRecordLevel.INFO,
                message,
                null,
                null
        );
    }

    public LogRecordType getType() {
        return type;
    }

    public void setType(final LogRecordType type) {
        this.type = type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Instant timestamp) {
        this.timestamp = timestamp;
    }

    public LogRecordLevel getLevel() {
        return level;
    }

    public void setLevel(final LogRecordLevel level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(final String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionStackTrace() {
        return exceptionStackTrace;
    }

    public void setExceptionStackTrace(final String exceptionStackTrace) {
        this.exceptionStackTrace = exceptionStackTrace;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(final UUID taskId) {
        this.taskId = taskId;
    }
}
