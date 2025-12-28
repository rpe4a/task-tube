package com.example.tasktube.client.sdk.publisher;

import com.example.tasktube.client.sdk.http.TaskTubeClient;
import com.example.tasktube.client.sdk.task.slot.SlotValueSerializer;
import com.example.tasktube.client.sdk.task.Constant;
import com.example.tasktube.client.sdk.task.Task;
import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Task1;
import com.example.tasktube.client.sdk.task.Task2;
import com.example.tasktube.client.sdk.task.TaskConfiguration;
import com.example.tasktube.client.sdk.task.TaskRecord;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.UUID;

public final class TaskTubePublisherFactory {
    private final TaskTubeClient client;
    private final SlotValueSerializer slotSerializer;

    public TaskTubePublisherFactory(
            @Nonnull final TaskTubeClient client,
            @Nonnull final SlotValueSerializer slotSerializer
    ) {
        this.client = Objects.requireNonNull(client);
        this.slotSerializer = Objects.requireNonNull(slotSerializer);
    }

    @Nonnull
    public <R> TaskTubePublisher create(
            @Nonnull final Task0<R> task,
            @Nonnull final TaskConfiguration... configurations
    ) {
        final TaskRecord.Builder<R> builder = createBuilder(Objects.requireNonNull(task));

        return new TaskTubePublisher(
                client,
                slotSerializer,
                builder,
                configurations
        );
    }

    @Nonnull
    public <R, A0> TaskTubePublisher create(
            @Nonnull final Task1<R, A0> task,
            @Nonnull final Constant<A0> arg0,
            @Nonnull final TaskConfiguration... configurations
    ) {
        final TaskRecord.Builder<R> builder = createBuilder(Objects.requireNonNull(task));

        builder.setArg(Objects.requireNonNull(arg0));

        return new TaskTubePublisher(
                client,
                slotSerializer,
                builder,
                configurations
        );
    }

    @Nonnull
    public <R, A0, A1> TaskTubePublisher create(
            @Nonnull final Task2<R, A0, A1> task,
            @Nonnull final Constant<A0> arg0,
            @Nonnull final Constant<A1> arg1,
            @Nonnull final TaskConfiguration... configurations
    ) {
        final TaskRecord.Builder<R> builder = createBuilder(Objects.requireNonNull(task));

        builder.setArg(Objects.requireNonNull(arg0))
                .setArg(Objects.requireNonNull(arg1));

        return new TaskTubePublisher(
                client,
                slotSerializer,
                builder,
                configurations
        );
    }

    @Nonnull
    private <R> TaskRecord.Builder<R> createBuilder(@Nonnull final Task<?> task) {
        return new TaskRecord.Builder<R>()
                .setId(UUID.randomUUID())
                .setName(task.getName())
                .setCorrelationId(UUID.randomUUID().toString());
    }
}
