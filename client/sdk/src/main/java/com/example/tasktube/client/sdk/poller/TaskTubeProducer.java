package com.example.tasktube.client.sdk.poller;

import com.example.tasktube.client.sdk.InstanceIdProvider;
import com.example.tasktube.client.sdk.http.TaskTubeClient;
import com.example.tasktube.client.sdk.http.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.http.dto.PopTasksRequest;
import com.example.tasktube.client.sdk.task.TaskInput;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

final class TaskTubeProducer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTubeProducer.class);

    private final TaskTubeClient taskTubeClient;
    private final BlockingQueue<TaskInput> clientQueue;
    private final String tube;
    private final InstanceIdProvider instanceIdProvider;
    private final TaskTubePollerSettings settings;

    TaskTubeProducer(
            @Nonnull final TaskTubeClient taskTubeClient,
            @Nonnull final BlockingQueue<TaskInput> clientQueue,
            @Nonnull final String tube,
            @Nonnull final InstanceIdProvider instanceIdProvider,
            @Nonnull final TaskTubePollerSettings settings
    ) {
        this.taskTubeClient = Objects.requireNonNull(taskTubeClient);
        this.clientQueue = Objects.requireNonNull(clientQueue);
        this.tube = Objects.requireNonNull(tube);
        this.instanceIdProvider = instanceIdProvider;
        this.settings = Objects.requireNonNull(settings);
    }

    @Override
    public void run() {
        LOGGER.debug("Task producer start polling tasks...");
        if (isFullClientQueue()) {
            return;
        }
        try {
            produce();
        } catch (final InterruptedException e) {
            LOGGER.info("Task producer has been stopped.");
            Thread.currentThread().interrupt();
        } catch (final Exception e) {
            LOGGER.error("Task producer has an unhandled exception below:", e);
        }
        LOGGER.debug("Task producer has finished polling tasks.");
    }

    private void produce() throws InterruptedException {
        final int taskCount = getTaskCount();
        if (taskCount == 0) {
            LOGGER.info("Tasks are limited now in '{}' queue.", tube);
        }

        LOGGER.debug("Polling tasks to handle by '{}' client.", instanceIdProvider.get());
        final List<PopTaskResponse> responses =
                taskTubeClient.popTasks(tube, new PopTasksRequest(instanceIdProvider.get(), taskCount));

        if (responses.isEmpty()) {
            LOGGER.info("There is nothing to handle by '{}' client.", instanceIdProvider.get());
        } else {
            LOGGER.info("There are '{}' tasks to handle by '{}' client. Tasks: {}",
                    responses.size(),
                    instanceIdProvider.get(),
                    responses.stream().map(r -> r.id().toString()).collect(Collectors.joining(", "))
            );

            for (final PopTaskResponse response : responses) {
                clientQueue.put(TaskInput.from(response));
                LOGGER.debug("Task: '{}' was added to the client queue.", response.id());
            }

        }
    }

    private boolean isFullClientQueue() {
        if (clientQueue.remainingCapacity() == 0) {
            LOGGER.info("Client queue is full. Let's try next time.");
            return true;
        }
        return false;
    }

    private int getTaskCount() {
        return Math.min(
                clientQueue.remainingCapacity(),
                settings.getMaxBatchRequestedTasksCount()
        );
    }

}
