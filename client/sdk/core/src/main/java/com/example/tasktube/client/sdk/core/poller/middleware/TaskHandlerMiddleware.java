package com.example.tasktube.client.sdk.core.poller.middleware;

import com.example.tasktube.client.sdk.core.IInstanceIdProvider;
import com.example.tasktube.client.sdk.core.http.ITaskTubeClient;
import com.example.tasktube.client.sdk.core.http.dto.FinishTaskRequest;
import com.example.tasktube.client.sdk.core.http.dto.LogRequest;
import com.example.tasktube.client.sdk.core.http.dto.StartTaskRequest;
import com.example.tasktube.client.sdk.core.http.dto.StartTaskResponse;
import com.example.tasktube.client.sdk.core.http.dto.TaskRequest;
import com.example.tasktube.client.sdk.core.task.TaskInput;
import com.example.tasktube.client.sdk.core.task.TaskOutput;
import com.example.tasktube.client.sdk.core.task.slot.SlotValueSerializer;
import jakarta.annotation.Nonnull;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Order(Integer.MAX_VALUE - 1)
public final class TaskHandlerMiddleware extends AbstractMiddleware {

    private final ITaskTubeClient ITaskTubeClient;
    private final IInstanceIdProvider IInstanceIdProvider;
    private final SlotValueSerializer slotValueSerializer;

    public TaskHandlerMiddleware(
            @Nonnull final ITaskTubeClient ITaskTubeClient,
            @Nonnull final IInstanceIdProvider IInstanceIdProvider,
            @Nonnull final SlotValueSerializer slotValueSerializer
    ) {
        this.ITaskTubeClient = Objects.requireNonNull(ITaskTubeClient);
        this.IInstanceIdProvider = Objects.requireNonNull(IInstanceIdProvider);
        this.slotValueSerializer = Objects.requireNonNull(slotValueSerializer);
    }

    @Override
    public void invokeImpl(@Nonnull final TaskInput input, @Nonnull final TaskOutput output, @Nonnull final Pipeline next) {
        start(input);

        next.handle(input, output);

        finish(output);
    }

    private void start(final TaskInput input) {
        final Optional<StartTaskResponse> result = ITaskTubeClient
                .startTask(
                        input.getId(),
                        new StartTaskRequest(
                                IInstanceIdProvider.get(),
                                Instant.now()
                        )
                );

        input.setArguments(result.get().arguments());
    }

    private void finish(final TaskOutput output) {
        final TaskRequest[] children =
                Objects.isNull(output.getChildren())
                        ? new TaskRequest[0]
                        : Arrays.stream(output.getChildren()).map(taskRecord -> taskRecord.toRequest(slotValueSerializer)).toArray(TaskRequest[]::new);

        final LogRequest[] logs =
                Objects.isNull(output.getLogs())
                        ? new LogRequest[0]
                        : Arrays.stream(output.getLogs()).map(LogRequest::from).toArray(LogRequest[]::new);


        ITaskTubeClient.finishTask(
                output.getId(),
                new FinishTaskRequest(
                        children,
                        logs,
                        output.getResult().serialize(slotValueSerializer),
                        IInstanceIdProvider.get(),
                        Instant.now()
                )
        );
    }
}
