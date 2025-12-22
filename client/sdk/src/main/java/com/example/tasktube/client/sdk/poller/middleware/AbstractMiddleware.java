package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractMiddleware implements Middleware {
    private static final String START_INVOKE_MIDDLEWARE = "Start invoking middleware.";
    private static final String MIDDLEWARE_FINISHED = "Middleware has finished.";

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public TaskOutput invoke(final TaskInput input, final Pipeline next) {
        logger.debug(START_INVOKE_MIDDLEWARE);
        final TaskOutput output = invokeImpl(input, next);
        logger.debug(MIDDLEWARE_FINISHED);
        return output;
    }

    public abstract TaskOutput invokeImpl(TaskInput input, Pipeline next);
}


