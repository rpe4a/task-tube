package com.example.tasktube.client.sdk.core.poller.middleware;

import com.example.tasktube.client.sdk.core.IInstanceIdProvider;
import com.example.tasktube.client.sdk.core.http.ITaskTubeClient;
import com.example.tasktube.client.sdk.core.http.dto.ProcessTaskRequest;
import com.example.tasktube.client.sdk.core.poller.TaskTubePoller;
import com.example.tasktube.client.sdk.core.poller.TaskTubePollerSettings;
import com.example.tasktube.client.sdk.core.poller.TaskTubePollerUtils;
import com.example.tasktube.client.sdk.core.poller.exception.TaskInterruptedException;
import com.example.tasktube.client.sdk.core.poller.exception.TaskTimeoutException;
import com.example.tasktube.client.sdk.core.task.TaskInput;
import com.example.tasktube.client.sdk.core.task.TaskOutput;
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

@Order(Integer.MAX_VALUE)
public final class HeartbeatMiddleware extends AbstractMiddleware {
    private static final String TASK_HANDLER_GROUP = "task-handler";
    private final ITaskTubeClient ITaskTubeClient;
    private final IInstanceIdProvider IInstanceIdProvider;
    private final TaskTubePollerSettings settings;
    private final ExecutorService taskHandlerPool;

    public HeartbeatMiddleware(
            @Nonnull final ITaskTubeClient ITaskTubeClient,
            @Nonnull final IInstanceIdProvider IInstanceIdProvider,
            @Nonnull final TaskTubePollerSettings settings
    ) {
        this.ITaskTubeClient = Objects.requireNonNull(ITaskTubeClient);
        this.IInstanceIdProvider = Objects.requireNonNull(IInstanceIdProvider);
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

        final long period = Math.round(input.getSettings().getHeartbeatTimeoutSeconds() * settings.getHeartbeatDurationFactor());
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
                                IInstanceIdProvider.get(),
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
                IInstanceIdProvider.get()
        );

        ITaskTubeClient.processTask(input.getId(), new ProcessTaskRequest(IInstanceIdProvider.get(), Instant.now()));
    }
}
