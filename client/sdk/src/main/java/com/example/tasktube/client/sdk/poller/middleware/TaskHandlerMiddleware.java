package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.InstanceIdProvider;
import com.example.tasktube.client.sdk.http.TaskTubeClient;
import com.example.tasktube.client.sdk.http.dto.FinishTaskRequest;
import com.example.tasktube.client.sdk.http.dto.StartTaskRequest;
import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;
import jakarta.annotation.Nonnull;

import java.time.Instant;
import java.util.Objects;

@Order(4)
public final class TaskHandlerMiddleware extends AbstractMiddleware {
    private final TaskTubeClient taskTubeClient;
    private final InstanceIdProvider instanceIdProvider;

    public TaskHandlerMiddleware(
            final TaskTubeClient taskTubeClient,
            final InstanceIdProvider instanceIdProvider
    ) {
        this.taskTubeClient = Objects.requireNonNull(taskTubeClient);
        this.instanceIdProvider = Objects.requireNonNull(instanceIdProvider);
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
        taskTubeClient.finishTask(
                output.getId(),
                new FinishTaskRequest(
                        output.getChildren(),
                        output.getResult(),
                        instanceIdProvider.get(),
                        Instant.now()
                )
        );
    }
}
