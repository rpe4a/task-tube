package com.example.tasktube.server.domain.values;

import com.google.common.base.Throwables;

import java.time.Instant;

public class LogRecord {

    private LogRecordType type;
    private Instant timestamp;
    private LogRecordLevel level;
    private String message;
    private String exceptionMessage;
    private String exceptionStackTrace;

    public LogRecord() {
    }

    public LogRecord(final LogRecordType type, final Instant timestamp, final LogRecordLevel level, final String message, final Throwable throwable) {
        this(type, timestamp, level, message, throwable.getMessage(), Throwables.getStackTraceAsString(throwable));
    }

    public LogRecord(
            final LogRecordType type,
            final Instant timestamp,
            final LogRecordLevel level,
            final String message,
            final String exceptionMessage,
            final String exceptionStackTrace
    ) {
        this.type = type;
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.exceptionMessage = exceptionMessage;
        this.exceptionStackTrace = exceptionStackTrace;
    }

    public static LogRecord info(final String message) {
        return new LogRecord(
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
}
