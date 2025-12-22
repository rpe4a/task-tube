package com.example.tasktube.client.sdk;

import com.example.tasktube.client.sdk.dto.FailTaskRequest;
import com.example.tasktube.client.sdk.dto.FinishTaskRequest;
import com.example.tasktube.client.sdk.dto.PopTaskRequest;
import com.example.tasktube.client.sdk.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.dto.PopTasksRequest;
import com.example.tasktube.client.sdk.dto.ProcessTaskRequest;
import com.example.tasktube.client.sdk.dto.StartTaskRequest;
import com.example.tasktube.client.sdk.dto.TaskRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskTubeClient {

    void startTask(UUID taskId, StartTaskRequest request);

    void processTask(UUID taskId, ProcessTaskRequest request);

    void finishTask(UUID taskId, FinishTaskRequest request);

    void failTask(UUID taskId, FailTaskRequest request);

    UUID pushTask(String tubeName, TaskRequest request);

    Optional<PopTaskResponse> popTask(String tubeName, PopTaskRequest request);

    List<PopTaskResponse> popTasks(String tubeName, PopTasksRequest request);
}
