package com.example.tasktube.client.sdk.core.publisher;

import com.example.tasktube.client.sdk.core.http.ITaskTubeClient;
import com.example.tasktube.client.sdk.core.task.Constant;
import com.example.tasktube.client.sdk.core.task.Task;
import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Task1;
import com.example.tasktube.client.sdk.core.task.Task2;
import com.example.tasktube.client.sdk.core.task.Task3;
import com.example.tasktube.client.sdk.core.task.Task4;
import com.example.tasktube.client.sdk.core.task.Task5;
import com.example.tasktube.client.sdk.core.task.Task6;
import com.example.tasktube.client.sdk.core.task.Task7;
import com.example.tasktube.client.sdk.core.task.TaskConfiguration;
import com.example.tasktube.client.sdk.core.task.TaskRecord;
import com.example.tasktube.client.sdk.core.task.slot.SlotValueSerializer;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.UUID;

public final class TaskTubePublisherFactory {
    private final ITaskTubeClient client;
    private final SlotValueSerializer slotSerializer;

    public TaskTubePublisherFactory(
            @Nonnull final ITaskTubeClient client,
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

        builder.setArgument(Objects.requireNonNull(arg0));

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

        builder.setArgument(Objects.requireNonNull(arg0))
                .setArgument(Objects.requireNonNull(arg1));

        return new TaskTubePublisher(
                client,
                slotSerializer,
                builder,
                configurations
        );
    }

    @Nonnull
    public <R, A0, A1, A2> TaskTubePublisher create(
            @Nonnull final Task3<R, A0, A1, A2> task,
            @Nonnull final Constant<A0> arg0,
            @Nonnull final Constant<A1> arg1,
            @Nonnull final Constant<A2> arg2,
            @Nonnull final TaskConfiguration... configurations
    ) {
        final TaskRecord.Builder<R> builder = createBuilder(Objects.requireNonNull(task));

        builder.setArgument(Objects.requireNonNull(arg0))
                .setArgument(Objects.requireNonNull(arg1))
                .setArgument(Objects.requireNonNull(arg2));

        return new TaskTubePublisher(
                client,
                slotSerializer,
                builder,
                configurations
        );
    }

    @Nonnull
    public <R, A0, A1, A2, A3> TaskTubePublisher create(
            @Nonnull final Task4<R, A0, A1, A2, A3> task,
            @Nonnull final Constant<A0> arg0,
            @Nonnull final Constant<A1> arg1,
            @Nonnull final Constant<A2> arg2,
            @Nonnull final Constant<A3> arg3,
            @Nonnull final TaskConfiguration... configurations
    ) {
        final TaskRecord.Builder<R> builder = createBuilder(Objects.requireNonNull(task));

        builder.setArgument(Objects.requireNonNull(arg0))
                .setArgument(Objects.requireNonNull(arg1))
                .setArgument(Objects.requireNonNull(arg2))
                .setArgument(Objects.requireNonNull(arg3));

        return new TaskTubePublisher(
                client,
                slotSerializer,
                builder,
                configurations
        );
    }

    @Nonnull
    public <R, A0, A1, A2, A3, A4> TaskTubePublisher create(
            @Nonnull final Task5<R, A0, A1, A2, A3, A4> task,
            @Nonnull final Constant<A0> arg0,
            @Nonnull final Constant<A1> arg1,
            @Nonnull final Constant<A2> arg2,
            @Nonnull final Constant<A3> arg3,
            @Nonnull final Constant<A4> arg4,
            @Nonnull final TaskConfiguration... configurations
    ) {
        final TaskRecord.Builder<R> builder = createBuilder(Objects.requireNonNull(task));

        builder.setArgument(Objects.requireNonNull(arg0))
                .setArgument(Objects.requireNonNull(arg1))
                .setArgument(Objects.requireNonNull(arg2))
                .setArgument(Objects.requireNonNull(arg3))
                .setArgument(Objects.requireNonNull(arg4));

        return new TaskTubePublisher(
                client,
                slotSerializer,
                builder,
                configurations
        );
    }

    @Nonnull
    public <R, A0, A1, A2, A3, A4, A5> TaskTubePublisher create(
            @Nonnull final Task6<R, A0, A1, A2, A3, A4, A5> task,
            @Nonnull final Constant<A0> arg0,
            @Nonnull final Constant<A1> arg1,
            @Nonnull final Constant<A2> arg2,
            @Nonnull final Constant<A3> arg3,
            @Nonnull final Constant<A4> arg4,
            @Nonnull final Constant<A5> arg5,
            @Nonnull final TaskConfiguration... configurations
    ) {
        final TaskRecord.Builder<R> builder = createBuilder(Objects.requireNonNull(task));

        builder.setArgument(Objects.requireNonNull(arg0))
                .setArgument(Objects.requireNonNull(arg1))
                .setArgument(Objects.requireNonNull(arg2))
                .setArgument(Objects.requireNonNull(arg3))
                .setArgument(Objects.requireNonNull(arg4))
                .setArgument(Objects.requireNonNull(arg5));

        return new TaskTubePublisher(
                client,
                slotSerializer,
                builder,
                configurations
        );
    }

    @Nonnull
    public <R, A0, A1, A2, A3, A4, A5, A6> TaskTubePublisher create(
            @Nonnull final Task7<R, A0, A1, A2, A3, A4, A5, A6> task,
            @Nonnull final Constant<A0> arg0,
            @Nonnull final Constant<A1> arg1,
            @Nonnull final Constant<A2> arg2,
            @Nonnull final Constant<A3> arg3,
            @Nonnull final Constant<A4> arg4,
            @Nonnull final Constant<A5> arg5,
            @Nonnull final Constant<A6> arg6,
            @Nonnull final TaskConfiguration... configurations
    ) {
        final TaskRecord.Builder<R> builder = createBuilder(Objects.requireNonNull(task));

        builder.setArgument(Objects.requireNonNull(arg0))
                .setArgument(Objects.requireNonNull(arg1))
                .setArgument(Objects.requireNonNull(arg2))
                .setArgument(Objects.requireNonNull(arg3))
                .setArgument(Objects.requireNonNull(arg4))
                .setArgument(Objects.requireNonNull(arg5))
                .setArgument(Objects.requireNonNull(arg6));

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
