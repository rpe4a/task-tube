package com.example.tasktube.client.sdk.core.poller.middleware;

import com.example.tasktube.client.sdk.core.task.TaskInput;
import com.example.tasktube.client.sdk.core.task.TaskOutput;
import jakarta.annotation.Nonnull;

import java.time.Duration;
import java.time.Instant;

@Order(2)
public final class InformationMiddleware extends AbstractMiddleware {
    private static final String TASK_DURATION_DATE_FORMAT = "%02d:%02d:%02d.%03d";
    private static final String START_WORKER_FOR_TASK = "Start task '{}'.";
    private static final String WORKER_HAS_FINISHED = "Task '{}' has finished. Duration: {}.";
    private static final String TASK_INPUT = "Task input: {}.";
    private static final String TASK_OUTPUT = "Task output: {}.";

    @Override
    public void invokeImpl(@Nonnull final TaskInput input, @Nonnull final TaskOutput output, @Nonnull final Pipeline next) {
        logger.info(START_WORKER_FOR_TASK, input.getName());
        logger.debug(TASK_INPUT, input);
        final Instant starts = Instant.now();
        next.handle(input, output);
        final Instant ends = Instant.now();
        final Duration diff = Duration.between(starts, ends);
        logger.debug(TASK_OUTPUT, output);
        logger.info(
                WORKER_HAS_FINISHED,
                input.getName(),
                String.format(
                        TASK_DURATION_DATE_FORMAT,
                        diff.toHours(),
                        diff.toMinutesPart(),
                        diff.toSecondsPart(),
                        diff.toMillisPart())
        );
    }
}
