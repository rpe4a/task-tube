package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;
import jakarta.annotation.Nonnull;
import org.slf4j.MDC;

@Order(1)
public final class MDCMiddleware extends AbstractMiddleware {
    public static final String CORRELATION_ID = "correlationId";
    public static final String TASK_ID = "taskId";

    @Override
    public void invokeImpl(@Nonnull final TaskInput input, @Nonnull final TaskOutput output, @Nonnull final Pipeline next) {
        try (
                final MDC.MDCCloseable correlationId = MDC.putCloseable(CORRELATION_ID, input.getCorrelationId());
                final MDC.MDCCloseable taskId = MDC.putCloseable(TASK_ID, input.getId().toString())
        ) {
            next.handle(input, output);
        }
    }
}
