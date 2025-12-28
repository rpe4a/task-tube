package com.example.tasktube.client.sdk.http;

import com.example.tasktube.client.sdk.http.dto.FailTaskRequest;
import com.example.tasktube.client.sdk.http.dto.FinishTaskRequest;
import com.example.tasktube.client.sdk.http.dto.PopTaskRequest;
import com.example.tasktube.client.sdk.http.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.http.dto.PopTasksRequest;
import com.example.tasktube.client.sdk.http.dto.ProcessTaskRequest;
import com.example.tasktube.client.sdk.http.dto.StartTaskRequest;
import com.example.tasktube.client.sdk.http.dto.TaskRequest;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskTubeClient {

    void startTask(@Nonnull UUID taskId, @Nonnull StartTaskRequest request);

    void processTask(@Nonnull UUID taskId, @Nonnull ProcessTaskRequest request);

    void finishTask(@Nonnull UUID taskId, @Nonnull FinishTaskRequest request);

    void failTask(@Nonnull UUID taskId, @Nonnull FailTaskRequest request);

    @Nonnull
    Optional<UUID> pushTask(@Nonnull String tubeName, @Nonnull TaskRequest request);

    @Nonnull
    Optional<PopTaskResponse> popTask(@Nonnull String tubeName, @Nonnull PopTaskRequest request);

    @Nonnull
    List<PopTaskResponse> popTasks(@Nonnull String tubeName, @Nonnull PopTasksRequest request);
}
