package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;

@FunctionalInterface
public interface Pipeline {
    TaskOutput handle(TaskInput input);
}

