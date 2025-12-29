package com.example.tasktube.client.sdk.core.poller.middleware;

import com.example.tasktube.client.sdk.core.IInstanceIdProvider;
import com.example.tasktube.client.sdk.core.http.ITaskTubeClient;
import com.example.tasktube.client.sdk.core.http.dto.FailTaskRequest;
import com.example.tasktube.client.sdk.core.task.TaskInput;
import com.example.tasktube.client.sdk.core.task.TaskOutput;
import jakarta.annotation.Nonnull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Objects;

@Order(3)
public final class ExceptionMiddleware extends AbstractMiddleware {
    private static final String EXCEPTION_MESSAGE = "Task '%s' has failed. See the inner exception for details.";
    private static final int CAPACITY_BYTES = 1024;

    private final ITaskTubeClient ITaskTubeClient;
    private final IInstanceIdProvider IInstanceIdProvider;

    public ExceptionMiddleware(
            @Nonnull final ITaskTubeClient ITaskTubeClient,
            @Nonnull final IInstanceIdProvider IInstanceIdProvider) {
        this.ITaskTubeClient = Objects.requireNonNull(ITaskTubeClient);
        this.IInstanceIdProvider = Objects.requireNonNull(IInstanceIdProvider);
    }

    @Override
    public void invokeImpl(@Nonnull final TaskInput input, @Nonnull final TaskOutput output, @Nonnull final Pipeline next) {
        try {
            next.handle(input, output);
        } catch (final Exception ex) {
            final Throwable innerException = Objects.nonNull(ex.getCause()) ? ex.getCause() : ex;

            logger.error(String.format(EXCEPTION_MESSAGE, input.getId()), innerException);

            ITaskTubeClient.failTask(
                    input.getId(),
                    new FailTaskRequest(
                            IInstanceIdProvider.get(),
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
