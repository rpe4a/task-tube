package com.example.tasktube.server.domain.enties;

import com.example.tasktube.server.domain.values.Lock;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Task {
    private UUID id;
    private String name;
    private String tube;
    private Status status;
    private Map<String, Object> input;
    private boolean isRoot;
    private Instant updatedAt;
    private Instant createdAt;
    private Instant scheduledAt;
    private Instant startedAt;
    private Instant heartbeatAt;
    private Instant finishedAt;
    private Lock lock;

    public Task(final UUID id,
                final String name,
                final String tube,
                final Status status,
                final Map<String, Object> input,
                final boolean isRoot,
                final Instant updatedAt,
                final Instant createdAt,
                final Instant scheduledAt,
                final Instant startedAt,
                final Instant heartbeatAt,
                final Instant finishedAt,
                final Lock lock
    ) {
        this.id = id;
        this.name = name;
        this.tube = tube;
        this.status = status;
        this.input = input;
        this.isRoot = isRoot;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.scheduledAt = scheduledAt;
        this.startedAt = startedAt;
        this.heartbeatAt = heartbeatAt;
        this.finishedAt = finishedAt;
        this.lock = lock;
    }

    public Task() {
    }

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

    public String getTube() {
        return tube;
    }

    public void setTube(final String tube) {
        this.tube = tube;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(final Map<String, Object> input) {
        this.input = input;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(final boolean root) {
        isRoot = root;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void schedule() {
        setStatus(Status.SCHEDULED);
        setScheduledAt(Instant.now());
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(final Lock lock) {
        this.lock = lock;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(final Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(final Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getHeartbeatAt() {
        return heartbeatAt;
    }

    public void setHeartbeatAt(final Instant heartbeatAt) {
        this.heartbeatAt = heartbeatAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(final Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public enum Status {
        SCHEDULED, CREATED
    }
}
