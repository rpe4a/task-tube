package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.InstanceIdProvider;
import com.example.tasktube.client.sdk.TaskTubeClient;
import com.example.tasktube.client.sdk.dto.ProcessTaskRequest;
import com.example.tasktube.client.sdk.poller.TaskTubePoller;
import com.example.tasktube.client.sdk.poller.TaskTubePollerSettings;
import com.example.tasktube.client.sdk.poller.TaskTubePollerUtils;
import com.example.tasktube.client.sdk.poller.exception.TaskInterruptedException;
import com.example.tasktube.client.sdk.poller.exception.TaskTimeoutException;
import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;
import jakarta.annotation.Nonnull;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Order(5)
public final class HeartbeatMiddleware extends AbstractMiddleware {
    private static final String TASK_HANDLER_GROUP = "task-handler";
    private static final double HEARTBEAT_FACTOR = 0.5;
    private final TaskTubeClient taskTubeClient;
    private final InstanceIdProvider instanceIdProvider;
    private final TaskTubePollerSettings settings;
    private final ExecutorService taskHandlerPool;

    public HeartbeatMiddleware(
            final TaskTubeClient taskTubeClient,
            final InstanceIdProvider instanceIdProvider,
            final TaskTubePollerSettings settings
    ) {
        this.taskTubeClient = Objects.requireNonNull(taskTubeClient);
        this.instanceIdProvider = Objects.requireNonNull(instanceIdProvider);
        this.settings = Objects.requireNonNull(settings);

        this.taskHandlerPool = Executors.newCachedThreadPool(
                TaskTubePollerUtils
                        .getThreadFactoryBuilder(
                                new ThreadGroup(
                                        "%s-%s".formatted(TaskTubePoller.CONSUMER_THREAD_GROUP, TASK_HANDLER_GROUP)
                                )
                        )
                        .build()
        );
    }

    @Override
    public void invokeImpl(@Nonnull final TaskInput input, @Nonnull final TaskOutput output, @Nonnull final Pipeline next) {
        final CompletableFuture<Void> handleFuture =
                CompletableFuture
                        .runAsync(() -> {
                            try(final MDC.MDCCloseable taskId = MDC.putCloseable(MDCMiddleware.TASK_ID, input.getId().toString())){
                                next.handle(input, output);
                            }
                        }, taskHandlerPool);

        // if the task has timeout, we must set it
        // else the task doesn't have timeout and it will execute infinitely
        if (input.getSettings().getTimeoutSeconds() != 0) {
            handleFuture
                    .orTimeout(input.getSettings().getTimeoutSeconds(), TimeUnit.SECONDS);
        }

        final long period = Math.round(input.getSettings().getHeartbeatTimeoutSeconds() * HEARTBEAT_FACTOR);
        final ScheduledExecutorService heartBeatPool = Executors.newSingleThreadScheduledExecutor();
        final ScheduledFuture<?> heartBeatFuture =
                heartBeatPool.scheduleAtFixedRate(
                        () -> {
                            if (!handleFuture.isDone()) {
                                try (final MDC.MDCCloseable taskId = MDC.putCloseable(MDCMiddleware.TASK_ID, input.getId().toString())) {
                                    extendLockTimeoutTask(input);
                                } catch (final InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        },
                        period,
                        period,
                        TimeUnit.SECONDS
                );

        try {
            handleFuture.get();
        } catch (final InterruptedException e) {
            throw new TaskInterruptedException("Task execution has been interrupted.");
        } catch (final ExecutionException e) {
            final Throwable throwable = e.getCause();
            if (throwable instanceof TimeoutException) {
                throw new TaskTimeoutException(
                        "Task '%s' of type '%s' executing by client '%s' timed out after %s seconds.".formatted(
                                input.getId(),
                                input.getName(),
                                instanceIdProvider.get(),
                                input.getSettings().getTimeoutSeconds()),
                        throwable
                );
            } else {
                throw new RuntimeException(throwable);
            }
        } finally {
            heartBeatFuture.cancel(true);
            heartBeatPool.shutdownNow();
        }
    }

    private void extendLockTimeoutTask(final TaskInput input) throws InterruptedException {
        logger.debug("Let's extend a lease of task '{}' of type '{}' by client '{}'.",
                input.getId(),
                input.getName(),
                instanceIdProvider.get()
        );

        taskTubeClient.processTask(input.getId(), new ProcessTaskRequest(instanceIdProvider.get(), Instant.now()));
    }
}
