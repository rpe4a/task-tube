package com.example.tasktube.client.sdk.core.poller.middleware;

import com.example.tasktube.client.sdk.core.InstanceIdProvider;
import com.example.tasktube.client.sdk.core.http.TaskTubeClient;
import com.example.tasktube.client.sdk.core.http.dto.FinishTaskRequest;
import com.example.tasktube.client.sdk.core.http.dto.StartTaskRequest;
import com.example.tasktube.client.sdk.core.http.dto.TaskRequest;
import com.example.tasktube.client.sdk.core.task.TaskInput;
import com.example.tasktube.client.sdk.core.task.TaskOutput;
import com.example.tasktube.client.sdk.core.task.slot.SlotValueSerializer;
import jakarta.annotation.Nonnull;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

@Order(4)
public final class TaskHandlerMiddleware extends AbstractMiddleware {
    private final TaskTubeClient taskTubeClient;
    private final InstanceIdProvider instanceIdProvider;
    private final SlotValueSerializer slotValueSerializer;

    public TaskHandlerMiddleware(
            @Nonnull final TaskTubeClient taskTubeClient,
            @Nonnull final InstanceIdProvider instanceIdProvider,
            @Nonnull final SlotValueSerializer slotValueSerializer
    ) {
        this.taskTubeClient = Objects.requireNonNull(taskTubeClient);
        this.instanceIdProvider = Objects.requireNonNull(instanceIdProvider);
        this.slotValueSerializer = Objects.requireNonNull(slotValueSerializer);
    }

    @Override
    public void invokeImpl(@Nonnull final TaskInput input, @Nonnull final TaskOutput output, @Nonnull final Pipeline next) {
        start(input);

        next.handle(input, output);

        finish(output);
    }

    private void start(final TaskInput input) {
        taskTubeClient.startTask(input.getId(), new StartTaskRequest(
                instanceIdProvider.get(),
                Instant.now()
        ));
    }

    private void finish(final TaskOutput output) {
        final TaskRequest[] children =
                Objects.isNull(output.getChildren())
                        ? new TaskRequest[0]
                        : Arrays.stream(output.getChildren()).map(taskRecord -> taskRecord.toRequest(slotValueSerializer)).toArray(TaskRequest[]::new);

        taskTubeClient.finishTask(
                output.getId(),
                new FinishTaskRequest(
                        children,
                        output.getResult().serialize(slotValueSerializer),
                        instanceIdProvider.get(),
                        Instant.now()
                )
        );
    }
}
