package com.example.tasktube.client.sdk.core.task;

import com.google.common.base.Throwables;

import java.time.Instant;

public class LogRecord {

    private final LogRecordType type = LogRecordType.CLIENT;
    private final Instant timestamp;
    private final LogRecordLevel level;
    private final String message;
    private String exceptionMessage;
    private String exceptionStackTrace;

    public LogRecord(final Instant timestamp, final LogRecordLevel level, final String message, final Throwable throwable) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        if (throwable != null) {
            this.exceptionMessage = throwable.getMessage();
            this.exceptionStackTrace = Throwables.getStackTraceAsString(throwable);
        }
    }

    public LogRecordType getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public LogRecordLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getExceptionStackTrace() {
        return exceptionStackTrace;
    }
}
