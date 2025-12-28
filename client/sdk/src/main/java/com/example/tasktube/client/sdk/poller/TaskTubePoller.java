package com.example.tasktube.client.sdk.poller;

import com.example.tasktube.client.sdk.InstanceIdProvider;
import com.example.tasktube.client.sdk.http.TaskTubeClient;
import com.example.tasktube.client.sdk.poller.middleware.Middleware;
import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.argument.ArgumentDeserializer;
import com.example.tasktube.client.sdk.task.slot.SlotValueSerializer;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class TaskTubePoller {
    public static final String INSPECTOR_THREAD_GROUP = "inspector";
    public static final String PRODUCER_THREAD_GROUP = "producer";
    public static final String CONSUMER_THREAD_GROUP = "consumer";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTubePoller.class);
    private final BlockingQueue<TaskInput> clientQueue;
    private final TaskTubeClient taskTubeClient;
    private final TaskTubePollerSettings settings;

    private final ScheduledExecutorService inspectorPool;
    private final ScheduledExecutorService producerPool;
    private final ExecutorService consumerPool;
    private final ThreadGroup inspectorThreadGroup;
    private final ThreadGroup producerThreadGroup;
    private final ThreadGroup consumerThreadGroup;
    private final TaskFactory taskFactory;
    private final InstanceIdProvider instanceIdProvider;
    private final List<Middleware> middlewares;
    private final ArgumentDeserializer slotDeserializer;
    private final SlotValueSerializer slotValueSerializer;

    public TaskTubePoller(
            @Nonnull final TaskTubeClient taskTubeClient,
            @Nonnull final TaskFactory taskFactory,
            @Nonnull final InstanceIdProvider instanceIdProvider,
            @Nonnull final List<Middleware> middlewares,
            @Nonnull final ArgumentDeserializer slotDeserializer,
            @Nonnull final SlotValueSerializer slotValueSerializer,
            @Nonnull final TaskTubePollerSettings settings
    ) {
        Objects.requireNonNull(taskTubeClient);
        Objects.requireNonNull(instanceIdProvider);
        Objects.requireNonNull(middlewares);
        Objects.requireNonNull(slotDeserializer);
        Objects.requireNonNull(slotValueSerializer);
        Objects.requireNonNull(taskFactory);
        Objects.requireNonNull(settings);

        inspectorThreadGroup = new ThreadGroup(INSPECTOR_THREAD_GROUP);
        final ThreadFactory inspectorThreadFactory = TaskTubePollerUtils.getThreadFactory(inspectorThreadGroup);

        producerThreadGroup = new ThreadGroup(PRODUCER_THREAD_GROUP);
        final ThreadFactory producerThreadFactory = TaskTubePollerUtils.getThreadFactory(producerThreadGroup);

        consumerThreadGroup = new ThreadGroup(CONSUMER_THREAD_GROUP);
        final ThreadFactory consumerThreadFactory = TaskTubePollerUtils.getThreadFactory(consumerThreadGroup);

        this.taskTubeClient = taskTubeClient;
        this.middlewares = middlewares;
        this.slotDeserializer = slotDeserializer;
        this.slotValueSerializer = slotValueSerializer;
        this.instanceIdProvider = instanceIdProvider;
        this.settings = settings;
        this.taskFactory = taskFactory;
        this.clientQueue = new ArrayBlockingQueue<>(settings.getQueueSize());
        this.inspectorPool = Executors.newSingleThreadScheduledExecutor(inspectorThreadFactory);
        this.producerPool = Executors.newSingleThreadScheduledExecutor(producerThreadFactory);
        this.consumerPool = Executors.newCachedThreadPool(consumerThreadFactory);
    }

    /**
     * Start inner consumer/producer pools
     */
    public void start(@Nonnull final String tube) {
        Preconditions.checkArgument(StringUtils.isNotBlank(tube));

        LOGGER.info("Task poller start with the settings: {}", settings);

        final ConsumerInspector inspector = new ConsumerInspector(
                taskFactory,
                clientQueue,
                consumerPool,
                middlewares,
                slotDeserializer,
                slotValueSerializer,
                settings
        );

        // consumer inspector add one default consumer immediately
        LOGGER.info("Consumer pool has started.");

        inspectorPool.scheduleWithFixedDelay(
                inspector,
                settings.getInspectorPollingIntervalMilliseconds(),
                settings.getInspectorPollingIntervalMilliseconds(),
                TimeUnit.MILLISECONDS
        );
        LOGGER.info("Inspector pool has started.");

        producerPool.scheduleWithFixedDelay(
                new TaskTubeProducer(taskTubeClient, clientQueue, tube, instanceIdProvider, settings),
                0,
                settings.getProducerPollingIntervalMilliseconds(),
                TimeUnit.MILLISECONDS
        );
        LOGGER.info("Producer pool has started.");

        LOGGER.info("Task poller has started successfully. There are '1' producers, '{}' consumers, '1' inspector and '1' monitor.", consumerThreadGroup.activeCount());
    }

    /**
     * Stop inner consumer/producer pools
     */
    public void stop() {
        LOGGER.info("Task poller shutdown starting...");

        shutdownAndAwaitTermination(producerPool, producerThreadGroup.getName());
        shutdownAndAwaitTermination(inspectorPool, inspectorThreadGroup.getName());
        shutdownAndAwaitTermination(consumerPool, consumerThreadGroup.getName());

        LOGGER.info("Task poller has been shutdown successfully.");
    }

    private void shutdownAndAwaitTermination(final ExecutorService pool, final String poolName) {
        LOGGER.info("Let's shutdown pool '{}'.", poolName);

        pool.shutdown();
        try {
            if (!pool.awaitTermination(settings.getShutdownAwaitTerminationSeconds(), TimeUnit.SECONDS)) {
                pool.shutdownNow();

                if (!pool.awaitTermination(settings.getShutdownAwaitTerminationSeconds(), TimeUnit.SECONDS)) {
                    LOGGER.error("There is a problem because a pool hasn't terminated.");
                }
            }
        } catch (final InterruptedException e) {
            LOGGER.warn("Shutdown thread has been interrupted.", e);

            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        LOGGER.info("Pool '{}' has been shutdown successfully.", poolName);
    }
}
