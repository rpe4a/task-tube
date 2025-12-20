package com.example.tasktube.client.sdk.publisher;

import com.example.tasktube.client.sdk.TaskTubeClient;
import com.example.tasktube.client.sdk.slot.SlotValueMapper;
import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Task1;
import com.example.tasktube.client.sdk.task.Task2;
import com.example.tasktube.client.sdk.task.Constant;

import java.util.Objects;
import java.util.UUID;

public final class TaskTubePublisherFactory {
    private final TaskTubeClient client;
    private final SlotValueMapper mapper;

    public TaskTubePublisherFactory(final TaskTubeClient client, final SlotValueMapper mapper) {
        this.client = Objects.requireNonNull(client);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public <R> TaskTubePublisher create(final Task0<R> task) {
        return new TaskTubePublisher(
                client,
                new TaskInfo.Builder(mapper)
                        .setId(UUID.randomUUID())
                        .setName(task.getName())
        );
    }

    public <R, A0> TaskTubePublisher create(final Task1<R, A0> task, final A0 arg0) {
        return new TaskTubePublisher(
                client,
                new TaskInfo.Builder(mapper)
                        .setId(UUID.randomUUID())
                        .setName(task.getName())
                        .setSlot(new Constant<>(arg0))
        );
    }

    public <R, A0, A1> TaskTubePublisher create(final Task2<R, A0, A1> task, final A0 arg0, final A1 arg1) {
        return new TaskTubePublisher(
                client,
                new TaskInfo.Builder(mapper)
                        .setId(UUID.randomUUID())
                        .setName(task.getName())
                        .setSlot(new Constant<>(arg0))
                        .setSlot(new Constant<>(arg1))
        );
    }
}
