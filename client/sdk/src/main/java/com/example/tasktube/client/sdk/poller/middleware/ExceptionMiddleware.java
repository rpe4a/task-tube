package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

@Order(3)
public final class ExceptionMiddleware extends AbstractMiddleware {
    private static final String EXCEPTION_MESSAGE = "Task '%s' has failed. See the inner exception for details.";
    private static final int CAPACITY_BYTES = 1024;

    @Override
    public TaskOutput invokeImpl(final TaskInput input, final Pipeline next) {
        try {
            return next.handle(input);
        } catch (final Exception ex) {
            final Throwable innerException = Objects.nonNull(ex.getCause()) ? ex.getCause() : ex;
            logger.error(String.format(EXCEPTION_MESSAGE, input.getId()), innerException);
            return TaskOutput.createInstance(input)
                    .setFailureMessage(printException(innerException));
        }
    }

    private String printException(final Throwable e) {
        final StringWriter sw = new StringWriter(CAPACITY_BYTES);
        final PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
