package com.example.tasktube.client.sdk.core.task;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class TaskLogger {

    private final List<LogRecord> logs;

    public TaskLogger(@Nonnull final List<LogRecord> logs) {
        this.logs = Objects.requireNonNull(logs);
    }

    public void trace(@Nullable final String msg) {
        handleArgs0Call(LogRecordLevel.TRACE, msg, null);
    }

    public void trace(@Nullable final String format, @Nullable final Object arg) {
        handleArgs1Call(LogRecordLevel.TRACE, format, arg);
    }

    public void trace(@Nullable final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        handle2ArgsCall(LogRecordLevel.TRACE, format, arg1, arg2);
    }

    public void trace(@Nullable final String format, @Nullable final Object... arguments) {
        handleArgArrayCall(LogRecordLevel.TRACE, format, arguments);
    }

    public void trace(@Nullable final String msg, @Nullable final Throwable t) {
        handleArgs0Call(LogRecordLevel.TRACE, msg, t);
    }

    public void debug(@Nullable final String msg) {
        handleArgs0Call(LogRecordLevel.DEBUG, msg, null);
    }

    public void debug(@Nullable final String format, @Nullable final Object arg) {
        handleArgs1Call(LogRecordLevel.DEBUG, format, arg);
    }

    public void debug(@Nullable final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        handle2ArgsCall(LogRecordLevel.DEBUG, format, arg1, arg2);
    }

    public void debug(@Nullable final String format, @Nullable final Object... arguments) {
        handleArgArrayCall(LogRecordLevel.DEBUG, format, arguments);
    }

    public void debug(@Nullable final String msg, @Nullable final Throwable t) {
        handleArgs0Call(LogRecordLevel.DEBUG, msg, t);
    }

    public void info(@Nullable final String msg) {
        handleArgs0Call(LogRecordLevel.INFO, msg, null);
    }

    public void info(@Nullable final String format, @Nullable final Object arg) {
        handleArgs1Call(LogRecordLevel.INFO, format, arg);
    }

    public void info(@Nullable final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        handle2ArgsCall(LogRecordLevel.INFO, format, arg1, arg2);
    }

    public void info(@Nullable final String format, @Nullable final Object... arguments) {
        handleArgArrayCall(LogRecordLevel.INFO, format, arguments);
    }

    public void info(@Nullable final String msg, @Nullable final Throwable t) {
        handleArgs0Call(LogRecordLevel.INFO, msg, t);
    }

    public void warn(@Nullable final String msg) {
        handleArgs0Call(LogRecordLevel.WARN, msg, null);
    }

    public void warn(@Nullable final String format, @Nullable final Object arg) {
        handleArgs1Call(LogRecordLevel.WARN, format, arg);
    }

    public void warn(@Nullable final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        handle2ArgsCall(LogRecordLevel.WARN, format, arg1, arg2);
    }

    public void warn(@Nullable final String format, @Nullable final Object... arguments) {
        handleArgArrayCall(LogRecordLevel.WARN, format, arguments);
    }

    public void warn(@Nullable final String msg, @Nullable final Throwable t) {
        handleArgs0Call(LogRecordLevel.WARN, msg, t);
    }

    public void error(@Nullable final String msg) {
        handleArgs0Call(LogRecordLevel.ERROR, msg, null);
    }

    public void error(@Nullable final String format, @Nullable final Object arg) {
        handleArgs1Call(LogRecordLevel.ERROR, format, arg);
    }

    public void error(@Nullable final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        handle2ArgsCall(LogRecordLevel.ERROR, format, arg1, arg2);
    }

    public void error(@Nullable final String format, @Nullable final Object... arguments) {
        handleArgArrayCall(LogRecordLevel.ERROR, format, arguments);
    }

    public void error(@Nullable final String msg, @Nullable final Throwable t) {
        handleArgs0Call(LogRecordLevel.ERROR, msg, t);
    }

    private void handleArgs0Call(@Nonnull final LogRecordLevel level, @Nullable final String msg, @Nullable final Throwable t) {
        handleNormalizedLoggingCall(level, msg, null, t);
    }

    private void handleArgs1Call(@Nonnull final LogRecordLevel level, @Nullable final String msg, @Nullable final Object arg1) {
        handleNormalizedLoggingCall(level, msg, new Object[] {arg1}, null);
    }

    private void handle2ArgsCall(@Nonnull final LogRecordLevel level, @Nullable final String msg, @Nullable final Object arg1, @Nullable final Object arg2) {
        if (arg2 instanceof Throwable) {
            handleNormalizedLoggingCall(level, msg, new Object[] {arg1}, (Throwable) arg2);
        } else {
            handleNormalizedLoggingCall(level, msg, new Object[] {arg1, arg2}, null);
        }
    }

    private void handleArgArrayCall(@Nonnull final LogRecordLevel level, @Nullable final String msg, @Nullable final Object[] args) {
        final Throwable throwableCandidate = MessageFormatter.getThrowableCandidate(args);
        if (throwableCandidate != null) {
            final Object[] trimmedCopy = MessageFormatter.trimmedCopy(args);
            handleNormalizedLoggingCall(level, msg, trimmedCopy, throwableCandidate);
        } else {
            handleNormalizedLoggingCall(level, msg, args, null);
        }
    }

    private void handleNormalizedLoggingCall(@Nonnull final LogRecordLevel level, @Nullable final String messagePattern, @Nullable final Object[] arguments, @Nullable final Throwable throwable) {
        final FormattingTuple ft = MessageFormatter.arrayFormat(messagePattern, arguments);
        logs.add(
                new LogRecord(
                        Instant.now(),
                        level,
                        ft.getMessage(),
                        Optional.ofNullable(throwable).orElseGet(ft::getThrowable)
                )
        );
    }
}
