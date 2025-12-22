package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;
import org.slf4j.MDC;

@Order(1)
public final class MDCMiddleware extends AbstractMiddleware {
    private static final String CORRELATION_ID = "correlationId";
    private static final String TASK_ID = "taskId";

    @Override
    public TaskOutput invokeImpl(final TaskInput input, final Pipeline next) {
        try (
                final MDC.MDCCloseable correlationId = MDC.putCloseable(CORRELATION_ID, input.getCorrelationId());
                final MDC.MDCCloseable taskId = MDC.putCloseable(TASK_ID, input.getId().toString())
        ) {
            return next.handle(input);
        }
    }
}
