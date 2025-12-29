package com.example.tasktube.client.sandbox;

import com.example.tasktube.client.sandbox.tube.regress.StartPointTaskReturnNothing;
import com.example.tasktube.client.sdk.core.IInstanceIdProvider;
import com.example.tasktube.client.sdk.core.http.ITaskTubeClient;
import com.example.tasktube.client.sdk.core.http.TaskTubeClientSettings;
import com.example.tasktube.client.sdk.core.impl.ClientIInstanceIdProvider;
import com.example.tasktube.client.sdk.core.impl.ReflectionITaskFactory;
import com.example.tasktube.client.sdk.core.impl.TaskTubeHttpClient;
import com.example.tasktube.client.sdk.core.module.TaskTubeModule;
import com.example.tasktube.client.sdk.core.poller.TaskTubePoller;
import com.example.tasktube.client.sdk.core.poller.TaskTubePollerSettings;
import com.example.tasktube.client.sdk.core.poller.middleware.ExceptionMiddleware;
import com.example.tasktube.client.sdk.core.poller.middleware.HeartbeatMiddleware;
import com.example.tasktube.client.sdk.core.poller.middleware.InformationMiddleware;
import com.example.tasktube.client.sdk.core.poller.middleware.MDCMiddleware;
import com.example.tasktube.client.sdk.core.poller.middleware.Middleware;
import com.example.tasktube.client.sdk.core.poller.middleware.TaskHandlerMiddleware;
import com.example.tasktube.client.sdk.core.publisher.TaskTubePublisher;
import com.example.tasktube.client.sdk.core.publisher.TaskTubePublisherFactory;
import com.example.tasktube.client.sdk.core.task.TaskConfiguration;
import com.example.tasktube.client.sdk.core.task.slot.SlotValueSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(final String[] args) throws InterruptedException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new TaskTubeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        final SlotValueSerializer slotValueSerializer = new SlotValueSerializer(objectMapper);
        final TaskTubeClientSettings settings = new TaskTubeClientSettings(30, "http://localhost:8080/");
        final ITaskTubeClient ITaskTubeClient = new TaskTubeHttpClient(objectMapper, settings);
        final IInstanceIdProvider IInstanceIdProvider = new ClientIInstanceIdProvider("sandbox-client");
        final ReflectionITaskFactory taskFactory = new ReflectionITaskFactory();
        final TaskTubePollerSettings pollerSettings = new TaskTubePollerSettings();
        final List<Middleware> middlewares = List.of(
                new MDCMiddleware(),
                new InformationMiddleware(),
                new ExceptionMiddleware(ITaskTubeClient, IInstanceIdProvider),
                new TaskHandlerMiddleware(ITaskTubeClient, IInstanceIdProvider, slotValueSerializer),
                new HeartbeatMiddleware(ITaskTubeClient, IInstanceIdProvider, pollerSettings)
        );

        final TaskTubePublisherFactory publisherFactory = new TaskTubePublisherFactory(ITaskTubeClient, slotValueSerializer);
        final TaskTubePublisher publisher = publisherFactory
                .create(
                        new StartPointTaskReturnNothing(),
                        TaskConfiguration.failureRetryTimeoutSeconds(120),
                        TaskConfiguration.maxCountOfFailures(5)
                );

        final UUID taskId = publisher.pushIn("test-tube").get();

        final TaskTubePoller taskPoller = new TaskTubePoller(
                objectMapper,
                ITaskTubeClient,
                taskFactory,
                IInstanceIdProvider,
                middlewares,
                pollerSettings
        );

        final Thread pollerThread = new Thread(
                () -> {
                    taskPoller.start("test-tube");
                }
        );

        pollerThread.start();
        pollerThread.join();
    }
}