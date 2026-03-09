package com.example.tasktube.server.application.queries.views;

import com.example.tasktube.server.domain.enties.Task;

import java.time.Instant;
import java.util.UUID;

public class TaskTubeView {

    private UUID id;
    private String name;
    private Task.Status status;
    private UUID parentId;
    private Instant createdAt;
    private Instant abortedAt;
    private Instant completedAt;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Task.Status getStatus() {
        return status;
    }

    public void setStatus(final Task.Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getAbortedAt() {
        return abortedAt;
    }

    public void setAbortedAt(final Instant abortedAt) {
        this.abortedAt = abortedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(final Instant completedAt) {
        this.completedAt = completedAt;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(final UUID parentId) {
        this.parentId = parentId;
    }
}
