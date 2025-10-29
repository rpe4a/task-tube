package com.example.tasktube.server.api.controllers.requests;

import com.example.tasktube.server.application.models.PushTaskDto;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PushTaskRequest {
    private String name;
    private Instant createdAt = Instant.now();
    private Map<String, Object> input = HashMap.newHashMap(0);

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

    public PushTaskDto toPushTaskDto(final String tube) {
        return new PushTaskDto(name, tube, createdAt, input);
    }
}

