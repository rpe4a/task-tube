package com.example.tasktube.client.sandbox;

import com.example.tasktube.client.sandbox.tube.ParentTaskReturnString0String;
import com.example.tasktube.client.sdk.InstanceIdProvider;
import com.example.tasktube.client.sdk.impl.ClientInstanceIdProvider;
import com.example.tasktube.client.sdk.http.TaskTubeClient;
import com.example.tasktube.client.sdk.http.TaskTubeClientSettings;
import com.example.tasktube.client.sdk.impl.TaskTubeHttpClient;
import com.example.tasktube.client.sdk.module.TaskTubeModule;
import com.example.tasktube.client.sdk.impl.ReflectionTaskFactory;
import com.example.tasktube.client.sdk.poller.TaskTubePoller;
import com.example.tasktube.client.sdk.poller.TaskTubePollerSettings;
import com.example.tasktube.client.sdk.poller.middleware.ExceptionMiddleware;
import com.example.tasktube.client.sdk.poller.middleware.HeartbeatMiddleware;
import com.example.tasktube.client.sdk.poller.middleware.InformationMiddleware;
import com.example.tasktube.client.sdk.poller.middleware.MDCMiddleware;
import com.example.tasktube.client.sdk.poller.middleware.Middleware;
import com.example.tasktube.client.sdk.poller.middleware.TaskHandlerMiddleware;
import com.example.tasktube.client.sdk.publisher.TaskTubePublisher;
import com.example.tasktube.client.sdk.publisher.TaskTubePublisherFactory;
import com.example.tasktube.client.sdk.task.argument.ArgumentDeserializer;
import com.example.tasktube.client.sdk.task.slot.SlotValueSerializer;
import com.example.tasktube.client.sdk.task.Constant;
import com.example.tasktube.client.sdk.task.TaskConfiguration;
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
        final ArgumentDeserializer argumentDeserializer = new ArgumentDeserializer(objectMapper);
        final TaskTubeClientSettings settings = new TaskTubeClientSettings(30, "http://localhost:8080/");
        final TaskTubeClient taskTubeClient = new TaskTubeHttpClient(objectMapper, settings);
        final InstanceIdProvider instanceIdProvider = new ClientInstanceIdProvider("sandbox-client");
        final ReflectionTaskFactory taskFactory = new ReflectionTaskFactory();
        final TaskTubePollerSettings pollerSettings = new TaskTubePollerSettings();
        final List<Middleware> middlewares = List.of(
                new MDCMiddleware(),
                new InformationMiddleware(),
                new ExceptionMiddleware(taskTubeClient, instanceIdProvider),
                new TaskHandlerMiddleware(taskTubeClient, instanceIdProvider, slotValueSerializer),
                new HeartbeatMiddleware(taskTubeClient, instanceIdProvider, pollerSettings)
        );

        final TaskTubePublisherFactory publisherFactory = new TaskTubePublisherFactory(taskTubeClient, slotValueSerializer);
        final TaskTubePublisher publisher = publisherFactory
                .create(
                        new ParentTaskReturnString0String(),
                        new Constant<>("simple string"),
                        TaskConfiguration.failureRetryTimeoutSeconds(120),
                        TaskConfiguration.maxCountOfFailures(5)
                );

        final UUID taskId = publisher.pushIn("test-tube").get();

        final TaskTubePoller taskPoller = new TaskTubePoller(
                taskTubeClient,
                taskFactory,
                instanceIdProvider,
                middlewares,
                argumentDeserializer,
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