package com.example.tasktube.server.domain.enties;

import com.example.tasktube.server.domain.values.Lock;
import com.google.common.base.Preconditions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Barrier {
    private UUID id;
    private UUID taskId;
    private List<UUID> waitFor;
    private Type type;
    private boolean released;
    private Instant updatedAt;
    private Instant createdAt;
    private Instant releasedAt;
    private Lock lock;

    public Barrier() {
    }

    public Barrier(
            final UUID id,
            final UUID taskId,
            final List<UUID> waitFor,
            final Type type,
            final boolean released,
            final Instant updatedAt,
            final Instant createdAt,
            final Instant releasedAt,
            final Lock lock
    ) {
        this.id = id;
        this.taskId = taskId;
        this.waitFor = waitFor;
        this.type = type;
        this.released = released;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.releasedAt = releasedAt;
        this.lock = lock;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(final UUID taskId) {
        this.taskId = taskId;
    }

    public List<UUID> getWaitFor() {
        return waitFor;
    }

    public void setWaitFor(final List<UUID> waitFor) {
        this.waitFor = waitFor;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public boolean isReleased() {
        return released;
    }

    public boolean isNotReleased() {
        return !isReleased();
    }

    public void setReleased(final boolean released) {
        this.released = released;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(final Instant releasedAt) {
        this.releasedAt = releasedAt;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(final Lock lock) {
        this.lock = lock;
    }

    public void release(final String client) {
        Preconditions.checkNotNull(client);
        Preconditions.checkState(getLock().isLockedBy(client), "The client '%s' can't release barrier.".formatted(client));
        Preconditions.checkState(!isReleased(), "Barrier is already released.");

        setReleased(true);
        setReleasedAt(Instant.now());
    }

    public enum Type {
        START,
        FINISH
    }
}
