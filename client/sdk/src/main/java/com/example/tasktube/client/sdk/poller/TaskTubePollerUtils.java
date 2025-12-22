package com.example.tasktube.client.sdk.poller;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ThreadFactory;

public final class TaskTubePollerUtils {
    private TaskTubePollerUtils() {
    }

    public static ThreadFactory getThreadFactory(final ThreadGroup threadGroup) {
        return getThreadFactoryBuilder(threadGroup)
                .build();
    }

    public static BasicThreadFactory.Builder getThreadFactoryBuilder(final ThreadGroup threadGroup) {
        return new BasicThreadFactory.Builder()
                .namingPattern(threadGroup.getName() + "-%d")
                .uncaughtExceptionHandler(new TaskTubePollerUncaughtExceptionHandler())
                .wrappedFactory(runnable -> new Thread(threadGroup, runnable));
    }
}
