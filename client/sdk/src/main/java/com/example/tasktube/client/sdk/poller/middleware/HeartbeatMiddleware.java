package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.InstanceIdProvider;
import com.example.tasktube.client.sdk.TaskTubeClient;
import com.example.tasktube.client.sdk.dto.ProcessTaskRequest;
import com.example.tasktube.client.sdk.poller.TaskTubePollerSettings;
import com.example.tasktube.client.sdk.poller.TaskTubePollerUtils;
import com.example.tasktube.client.sdk.poller.exception.TaskInterruptedException;
import com.example.tasktube.client.sdk.poller.exception.TaskTimeoutException;
import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Order(5)
public final class HeartbeatMiddleware extends AbstractMiddleware {
    public static final int COUNT_OF_THREADS_TO_HANDLE_TASK = 2;
    public static final double HEARTBEAT_FACTOR = 0.5;
    private final TaskTubeClient taskTubeClient;
    private final InstanceIdProvider instanceIdProvider;
    private final TaskTubePollerSettings settings;

    public HeartbeatMiddleware(
            final TaskTubeClient taskTubeClient,
            final InstanceIdProvider instanceIdProvider,
            final TaskTubePollerSettings settings
    ) {
        this.taskTubeClient = Objects.requireNonNull(taskTubeClient);
        this.instanceIdProvider = Objects.requireNonNull(instanceIdProvider);
        this.settings = Objects.requireNonNull(settings);
    }

    @Override
    public TaskOutput invokeImpl(final TaskInput input, final Pipeline next) {
        final ExecutorService taskLocalExecutor = getTaskLocalExecutor(input);

        final CompletableFuture<TaskOutput> handleFuture =
                CompletableFuture
                        .supplyAsync(() -> next.handle(input), taskLocalExecutor);

        // if the task has timeout, we must set it
        // else the task doesn't have timeout and it will execute infinitely
        if (input.getSettings().getTimeoutSeconds() != 0) {
            handleFuture
                    .orTimeout(input.getSettings().getTimeoutSeconds(), TimeUnit.SECONDS);
        }

        try {
            while (!handleFuture.isDone()) {
                final CompletableFuture<Void> heartbeating =
                        CompletableFuture
                                .runAsync(runHeartbeat(input), taskLocalExecutor);

                CompletableFuture
                        .anyOf(handleFuture, heartbeating)
                        .get();

                if (!handleFuture.isDone()) {
                    continueTask(input);
                } else {
                    heartbeating.cancel(true);
                }
            }
            return handleFuture.get();
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
            taskLocalExecutor.shutdownNow();
        }
    }

    private ExecutorService getTaskLocalExecutor(final TaskInput task) {
        final ThreadGroup taskThreadGroup = new ThreadGroup(
                Thread.currentThread().getThreadGroup().getParent(), //main thread group
                "%s-%s".formatted(task.getName(), task.getId())
        );

        return Executors.newFixedThreadPool(
                COUNT_OF_THREADS_TO_HANDLE_TASK,
                TaskTubePollerUtils
                        .getThreadFactoryBuilder(taskThreadGroup)
                        .build()
        );
    }

    private void continueTask(final TaskInput input) throws InterruptedException {
        logger.debug("Let's extend a lease of task '{}' of type '{}' by client '{}'.",
                input.getId(),
                input.getName(),
                instanceIdProvider.get()
        );

        taskTubeClient.processTask(input.getId(), new ProcessTaskRequest(instanceIdProvider.get(), Instant.now()));
    }

    private Runnable runHeartbeat(final TaskInput input) {
        return () -> {
            try {
                Thread.sleep(
                        Duration.ofSeconds(
                                        Math.round(input.getSettings().getHeartbeatTimeoutSeconds() * HEARTBEAT_FACTOR))
                                .toMillis()
                );
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }
}
