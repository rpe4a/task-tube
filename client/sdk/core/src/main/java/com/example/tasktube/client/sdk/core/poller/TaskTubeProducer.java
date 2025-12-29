package com.example.tasktube.client.sdk.core.poller;

import com.example.tasktube.client.sdk.core.IInstanceIdProvider;
import com.example.tasktube.client.sdk.core.http.ITaskTubeClient;
import com.example.tasktube.client.sdk.core.http.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.core.http.dto.PopTasksRequest;
import com.example.tasktube.client.sdk.core.task.TaskInput;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

final class TaskTubeProducer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTubeProducer.class);

    private final ITaskTubeClient ITaskTubeClient;
    private final BlockingQueue<TaskInput> clientQueue;
    private final String tube;
    private final IInstanceIdProvider IInstanceIdProvider;
    private final TaskTubePollerSettings settings;

    TaskTubeProducer(
            @Nonnull final ITaskTubeClient ITaskTubeClient,
            @Nonnull final BlockingQueue<TaskInput> clientQueue,
            @Nonnull final String tube,
            @Nonnull final IInstanceIdProvider IInstanceIdProvider,
            @Nonnull final TaskTubePollerSettings settings
    ) {
        this.ITaskTubeClient = Objects.requireNonNull(ITaskTubeClient);
        this.clientQueue = Objects.requireNonNull(clientQueue);
        this.tube = Objects.requireNonNull(tube);
        this.IInstanceIdProvider = IInstanceIdProvider;
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

        LOGGER.debug("Polling tasks to handle by '{}' client.", IInstanceIdProvider.get());
        final List<PopTaskResponse> responses =
                ITaskTubeClient.popTasks(tube, new PopTasksRequest(IInstanceIdProvider.get(), taskCount));

        if (responses.isEmpty()) {
            LOGGER.info("There is nothing to handle by '{}' client.", IInstanceIdProvider.get());
        } else {
            LOGGER.info("There are '{}' tasks to handle by '{}' client. Tasks: {}",
                    responses.size(),
                    IInstanceIdProvider.get(),
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
