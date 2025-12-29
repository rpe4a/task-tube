package com.example.tasktube.sandboxspring.configuration;

import com.example.tasktube.client.sdk.core.IInstanceIdProvider;
import com.example.tasktube.client.sdk.core.http.ITaskTubeClient;
import com.example.tasktube.client.sdk.core.http.TaskTubeClientSettings;
import com.example.tasktube.client.sdk.core.impl.ClientIInstanceIdProvider;
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
import com.example.tasktube.client.sdk.core.publisher.TaskTubePublisherFactory;
import com.example.tasktube.client.sdk.core.task.ITaskFactory;
import com.example.tasktube.client.sdk.core.task.argument.ArgumentDeserializer;
import com.example.tasktube.client.sdk.core.task.slot.SlotValueSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import java.util.LinkedList;
import java.util.List;

public abstract class TaskTubeConfiguration {

    public TaskTubeConfiguration() {
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskTubeClientSettings registerTaskTubeClientSettings(
            @Value("${spring.task-tube.client.connectionTimeoutSeconds:30}") final int connectionTimeoutSeconds,
            @Value("${spring.task-tube.client.taskTubeServerApiHost}") final String taskTubeServerApiHost
    ) {
        return new TaskTubeClientSettings(connectionTimeoutSeconds, taskTubeServerApiHost);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskTubePollerSettings registerTaskTubePollerSettings(
            @Value("${spring.task-tube.poller.producerPollingIntervalMilliseconds:5000}") final int producerPollingIntervalMilliseconds,
            @Value("${spring.task-tube.poller.shutdownAwaitTerminationSeconds:60}") final int shutdownAwaitTerminationSeconds,
            @Value("${spring.task-tube.poller.maxConsumersCount:1}") final int maxConsumersCount,
            @Value("${spring.task-tube.poller.minConsumersCount:1}") final int minConsumersCount,
            @Value("${spring.task-tube.poller.consumerEmptyQueueSleepTimeoutMilliseconds:100}") final int consumerEmptyQueueSleepTimeoutMilliseconds,
            @Value("${spring.task-tube.poller.inspectorPollingIntervalMilliseconds:100}") final int inspectorPollingIntervalMilliseconds,
            @Value("${spring.task-tube.poller.maxBatchRequestedTasksCount:16}") final int maxBatchRequestedTasksCount,
            @Value("${spring.task-tube.poller.heartbeatDurationFactor:0.5}") final double heartbeatDurationFactor,
            @Value("${spring.task-tube.poller.queueSize:32}") final int queueSize
    ) {
        return new TaskTubePollerSettings(
                producerPollingIntervalMilliseconds,
                shutdownAwaitTerminationSeconds,
                maxConsumersCount,
                minConsumersCount,
                consumerEmptyQueueSleepTimeoutMilliseconds,
                inspectorPollingIntervalMilliseconds,
                maxBatchRequestedTasksCount,
                heartbeatDurationFactor,
                queueSize
        );
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IInstanceIdProvider registerIInstanceIdProvider(
            @Value("${spring.application.name:client}") final String client
    ) {
        return new ClientIInstanceIdProvider(client);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITaskFactory registerITaskFactory(
            final ApplicationContext context
    ) {
        return new SpringTaskFactory(context);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ObjectMapper registerObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new TaskTubeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        configureObjectMapper(objectMapper);

        return objectMapper;
    }

    @Bean
    @DependsOn({"registerTaskTubeClientSettings", "registerObjectMapper"})
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITaskTubeClient registerITaskTubeClient(
            final ObjectMapper objectMapper,
            final TaskTubeClientSettings settings
    ) {
        return new TaskTubeHttpClient(objectMapper, settings);
    }

    @Bean
    @DependsOn({"registerObjectMapper"})
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SlotValueSerializer registerSlotValueSerializer(
            final ObjectMapper objectMapper
    ) {
        return new SlotValueSerializer(objectMapper);
    }

    @Bean
    @DependsOn({"registerObjectMapper"})
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ArgumentDeserializer registerArgumentDeserializer(
            final ObjectMapper objectMapper
    ) {
        return new ArgumentDeserializer(objectMapper);
    }

    @Bean
    @DependsOn({"registerITaskTubeClient", "registerSlotValueSerializer"})
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskTubePublisherFactory registerTaskTubePublisherFactory(
            final ITaskTubeClient taskTubeClient,
            final SlotValueSerializer slotSerializer
    ) {
        return new TaskTubePublisherFactory(taskTubeClient, slotSerializer);
    }

    @Bean
    @DependsOn({
            "registerITaskTubeClient",
            "registerIInstanceIdProvider",
            "registerSlotValueSerializer",
            "registerTaskTubePollerSettings",
    })
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public List<Middleware> registerMiddlewares(
            final ITaskTubeClient taskTubeClient,
            final IInstanceIdProvider instanceIdProvider,
            final SlotValueSerializer slotSerializer,
            final TaskTubePollerSettings taskTubePollerSettings
    ) {
        final LinkedList<Middleware> middlewares = new LinkedList<Middleware>();
        middlewares.add(new MDCMiddleware());
        middlewares.add(new InformationMiddleware());
        middlewares.add(new ExceptionMiddleware(taskTubeClient, instanceIdProvider));
        addCustomMiddlewares(middlewares);
        middlewares.add(new TaskHandlerMiddleware(taskTubeClient, instanceIdProvider, slotSerializer));
        middlewares.add(new HeartbeatMiddleware(taskTubeClient, instanceIdProvider, taskTubePollerSettings));
        return middlewares;
    }

    @Bean
    @DependsOn({
            "registerObjectMapper",
            "registerITaskTubeClient",
            "registerITaskFactory",
            "registerIInstanceIdProvider",
            "registerMiddlewares",
            "registerTaskTubePollerSettings",
    })
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TaskTubePoller registerTaskTubePoller(
            final ObjectMapper objectMapper,
            final ITaskTubeClient taskTubeClient,
            final ITaskFactory taskFactory,
            final IInstanceIdProvider instanceIdProvider,
            final List<Middleware> middlewares,
            final TaskTubePollerSettings taskTubePollerSettings
    ) {
        return new TaskTubePoller(
                objectMapper,
                taskTubeClient,
                taskFactory,
                instanceIdProvider,
                middlewares,
                taskTubePollerSettings
        );
    }

    protected void configureObjectMapper(@Nonnull final ObjectMapper objectMapper) {
    }

    protected void addCustomMiddlewares(@Nonnull final LinkedList<Middleware> middlewares) {
    }
}
