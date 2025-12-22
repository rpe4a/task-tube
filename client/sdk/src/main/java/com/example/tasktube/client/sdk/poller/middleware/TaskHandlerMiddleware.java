package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.InstanceIdProvider;
import com.example.tasktube.client.sdk.TaskTubeClient;
import com.example.tasktube.client.sdk.dto.FinishTaskRequest;
import com.example.tasktube.client.sdk.dto.StartTaskRequest;
import com.example.tasktube.client.sdk.dto.TaskRequest;
import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;
import com.example.tasktube.client.sdk.task.TaskRecord;

import java.time.Instant;
import java.util.List;
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
        this.instanceIdProvider = instanceIdProvider;
    }

    @Override
    public TaskOutput invokeImpl(final TaskInput input, final Pipeline next) {
        start(input);

        final TaskOutput output = next.handle(input);

        finish(output);

        return output;
    }

    private void start(final TaskInput input) {
        taskTubeClient.startTask(input.getId(), new StartTaskRequest(
                instanceIdProvider.get(),
                Instant.now()
        ));
    }

    private void finish(final TaskOutput task) {
        final List<TaskRequest> children = task.getChildren().stream().map(TaskRecord::toRequest).toList();
        taskTubeClient.finishTask(
                task.getId(),
                new FinishTaskRequest(
                        children,
                        task.getOutput(),
                        instanceIdProvider.get(),
                        Instant.now()
                )
        );
    }
}
