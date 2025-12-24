package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractMiddleware implements Middleware {
    private static final String START_INVOKE_MIDDLEWARE = "Start invoking middleware.";
    private static final String MIDDLEWARE_FINISHED = "Middleware has finished.";

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void invoke(@Nonnull final TaskInput input, @Nonnull final TaskOutput output, @Nonnull final Pipeline next) {
        logger.debug(START_INVOKE_MIDDLEWARE);
        invokeImpl(input, output, next);
        logger.debug(MIDDLEWARE_FINISHED);
    }

    public abstract void invokeImpl(@Nonnull TaskInput input, @Nonnull TaskOutput output, @Nonnull Pipeline next);
}


