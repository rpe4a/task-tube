package com.example.tasktube.server.api.controllers.requests;

import com.example.tasktube.server.application.models.RunningTaskDto;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RunningTaskRequest {
    private String name;
    private String queueName;
    private Instant createdAt = Instant.now();
    private Map<String, Object> input = HashMap.newHashMap(0);

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(final String queueName) {
        this.queueName = queueName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(final Map<String, Object> input) {
        this.input = input;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public RunningTaskDto toRunningTaskDto() {
        return new RunningTaskDto(name, queueName, createdAt, input);
    }
}

