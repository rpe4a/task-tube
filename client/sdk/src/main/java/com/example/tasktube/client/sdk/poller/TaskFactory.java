package com.example.tasktube.client.sdk.poller;

import com.example.tasktube.client.sdk.task.Task;

public interface TaskFactory {
    Task<?> createInstance(String taskName);
}
