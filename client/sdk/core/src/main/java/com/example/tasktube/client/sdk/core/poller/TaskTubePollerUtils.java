package com.example.tasktube.client.sdk.core.poller;

import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ThreadFactory;

public final class TaskTubePollerUtils {
    private TaskTubePollerUtils() {
    }

    @Nonnull
    public static ThreadFactory getThreadFactory(@Nonnull final ThreadGroup threadGroup) {
        return getThreadFactoryBuilder(threadGroup)
                .build();
    }

    @Nonnull
    public static BasicThreadFactory.Builder getThreadFactoryBuilder(@Nonnull final ThreadGroup threadGroup) {
        Preconditions.checkNotNull(threadGroup);

        return new BasicThreadFactory.Builder()
                .namingPattern(threadGroup.getName() + "-%d")
                .uncaughtExceptionHandler(new TaskTubePollerUncaughtExceptionHandler())
                .wrappedFactory(runnable -> new Thread(threadGroup, runnable));
    }
}
