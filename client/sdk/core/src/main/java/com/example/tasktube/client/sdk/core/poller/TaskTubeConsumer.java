package com.example.tasktube.client.sdk.core.poller;

import com.example.tasktube.client.sdk.core.poller.middleware.Middleware;
import com.example.tasktube.client.sdk.core.poller.middleware.Pipeline;
import com.example.tasktube.client.sdk.core.poller.middleware.PipelineBuilder;
import com.example.tasktube.client.sdk.core.task.ITaskFactory;
import com.example.tasktube.client.sdk.core.task.argument.ArgumentDeserializer;
import com.example.tasktube.client.sdk.core.task.Task;
import com.example.tasktube.client.sdk.core.task.TaskInput;
import com.example.tasktube.client.sdk.core.task.TaskOutput;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

final class TaskTubeConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTubeConsumer.class);
    private final BlockingQueue<TaskInput> queue;
    private final ITaskFactory ITaskFactory;
    private final ConsumerInspector inspector;
    private final ArgumentDeserializer slotDeserializer;
    private final TaskTubePollerSettings settings;
    private final List<Middleware> middlewares;

    TaskTubeConsumer(
            @Nonnull final ITaskFactory ITaskFactory,
            @Nonnull final BlockingQueue<TaskInput> queue,
            @Nonnull final ConsumerInspector inspector,
            @Nonnull final List<Middleware> middlewares,
            @Nonnull final ArgumentDeserializer slotDeserializer,
            @Nonnull final TaskTubePollerSettings settings
    ) {
        this.middlewares = Objects.requireNonNull(middlewares);
        this.queue = Objects.requireNonNull(queue);
        this.ITaskFactory = Objects.requireNonNull(ITaskFactory);
        this.inspector = Objects.requireNonNull(inspector);
        this.slotDeserializer = Objects.requireNonNull(slotDeserializer);
        this.settings = Objects.requireNonNull(settings);
    }

    @Override
    public void run() {
        LOGGER.debug("Task consumer start consuming tasks...");
        while (!Thread.currentThread().isInterrupted() && inspector.getPermit()) {
            try {
                consume();
            } catch (final InterruptedException ignored) {
                LOGGER.info("Task consumer has been stopped.");
                Thread.currentThread().interrupt();
            } catch (final Exception e) {
                LOGGER.error("Task consumer has an unhandled exception below:", e);
            }
        }
        LOGGER.debug("Task consumer has finished consuming tasks.");
    }

    private void consume() throws InterruptedException {
        final TaskInput input = queue.poll(settings.getConsumerEmptyQueueSleepTimeoutMilliseconds(), TimeUnit.MILLISECONDS);

        if (input == null) {
            LOGGER.debug("Nothing to do. Let's continue.");
            return;
        }

        final Task<?> task = ITaskFactory.createInstance(input.getName());

        final Pipeline pipeline = new PipelineBuilder()
                .add(middlewares)
                .createInstance(
                        (i, o) ->
                                new Task.Executor(task).invoke(i, o, slotDeserializer)
                );

        pipeline.handle(input, TaskOutput.createInstance(input));
    }
}
