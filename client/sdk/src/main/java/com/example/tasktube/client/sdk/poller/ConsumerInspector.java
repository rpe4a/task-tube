package com.example.tasktube.client.sdk.poller;

import com.example.tasktube.client.sdk.poller.middleware.Middleware;
import com.example.tasktube.client.sdk.task.argument.ArgumentDeserializer;
import com.example.tasktube.client.sdk.task.slot.SlotValueSerializer;
import com.example.tasktube.client.sdk.task.TaskInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

final class ConsumerInspector implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerInspector.class);
    private final AtomicInteger consumersCount = new AtomicInteger(0);
    private final TaskFactory taskFactory;
    private final BlockingQueue<TaskInput> queue;
    private final ExecutorService consumerPool;
    private final ArgumentDeserializer slotDeserializer;
    private final TaskTubePollerSettings settings;
    private final List<Middleware> middlewares;

    ConsumerInspector(
            final TaskFactory taskFactory,
            final BlockingQueue<TaskInput> queue,
            final ExecutorService consumerPool,
            final List<Middleware> middlewares,
            final ArgumentDeserializer slotDeserializer,
            final TaskTubePollerSettings settings
    ) {
        this.taskFactory = Objects.requireNonNull(taskFactory);
        this.queue = Objects.requireNonNull(queue);
        this.consumerPool = Objects.requireNonNull(consumerPool);
        this.middlewares = Objects.requireNonNull(middlewares);
        this.slotDeserializer = Objects.requireNonNull(slotDeserializer);
        this.settings = Objects.requireNonNull(settings);

        // add default consumers
        for (int i = 0; i < settings.getMinConsumersCount(); i++) {
            addConsumer();
        }
    }

    public int activeConsumersCount() {
        return consumersCount.get();
    }

    @Override
    public void run() {
        LOGGER.debug("Consumer inspector starts...");
        try {
            inspect();
        } catch (final Exception e) {
            LOGGER.error("Consumer inspector has an unhandled exception below:", e);
        }
        LOGGER.debug("Consumer inspector has finished.");
    }

    /**
     * @return
     */
    public synchronized boolean getPermit() {
        if (!queue.isEmpty() || consumersCount.get() <= settings.getMinConsumersCount()) {
            // We have free tasks in queue, or we always must have at once one consumer
            return true;
        } else {
            // We have enough consumers, so need to remove someone.
            removeConsumer();
            return false;
        }
    }

    private synchronized void inspect() {
        if (queueIsNotEmptyAndNotReachMaxConsumersCount()) {
            LOGGER.debug("Add extra consumer.");
            addConsumer();
        }
    }

    private void addConsumer() {
        consumersCount.incrementAndGet();
        consumerPool.execute(new TaskTubeConsumer(taskFactory, queue, this, middlewares, slotDeserializer, settings));
    }

    private void removeConsumer() {
        // consumerPool is going to close every idle consumer in 60 seconds by itself
        consumersCount.decrementAndGet();
    }

    private boolean queueIsNotEmptyAndNotReachMaxConsumersCount() {
        return !queue.isEmpty() && consumersCount.get() < settings.getMaxConsumersCount();
    }
}
