package com.example.tasktube.client.sdk.poller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class TaskTubePollerUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTubePoller.class);

    @Override
    public void uncaughtException(final Thread thread, final Throwable e) {
        LOGGER.error("Thread '{}' will be interrupted.", thread, e);
    }
}
