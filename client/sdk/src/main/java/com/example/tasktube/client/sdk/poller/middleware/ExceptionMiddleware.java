package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.InstanceIdProvider;
import com.example.tasktube.client.sdk.http.TaskTubeClient;
import com.example.tasktube.client.sdk.http.dto.FailTaskRequest;
import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;
import jakarta.annotation.Nonnull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Objects;

@Order(3)
public final class ExceptionMiddleware extends AbstractMiddleware {
    private static final String EXCEPTION_MESSAGE = "Task '%s' has failed. See the inner exception for details.";
    private static final int CAPACITY_BYTES = 1024;

    private final TaskTubeClient taskTubeClient;
    private final InstanceIdProvider instanceIdProvider;

    public ExceptionMiddleware(
            @Nonnull final TaskTubeClient taskTubeClient,
            @Nonnull final InstanceIdProvider instanceIdProvider) {
        this.taskTubeClient = Objects.requireNonNull(taskTubeClient);
        this.instanceIdProvider = Objects.requireNonNull(instanceIdProvider);
    }

    @Override
    public void invokeImpl(@Nonnull final TaskInput input, @Nonnull final TaskOutput output, @Nonnull final Pipeline next) {
        try {
            next.handle(input, output);
        } catch (final Exception ex) {
            final Throwable innerException = Objects.nonNull(ex.getCause()) ? ex.getCause() : ex;

            logger.error(String.format(EXCEPTION_MESSAGE, input.getId()), innerException);

            taskTubeClient.failTask(
                    input.getId(),
                    new FailTaskRequest(
                            instanceIdProvider.get(),
                            Instant.now(),
                            """
                                    Exception: %s
                                    Stacktrace: %s
                                    """.formatted(innerException.getMessage(), printException(innerException))
                    )
            );
        }
    }

    private String printException(final Throwable e) {
        final StringWriter sw = new StringWriter(CAPACITY_BYTES);
        final PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
