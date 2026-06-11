package com.example.tasktube.server.domain.enties;

import com.example.tasktube.server.domain.values.Lock;

import java.time.Instant;
import java.util.UUID;

public class TaskTube extends Entity<UUID> {

    private String correlationId;
    private UUID taskId;
    private boolean isTerminationRequested;
    private boolean isRecoveryRequested;
    private Lock lock;
    private Instant updatedAt;
    private Instant createdAt;

    public TaskTube() {
        super(UUID.randomUUID());
    }

    public TaskTube(
            final String correlationId,
            final UUID taskId,
            final boolean isTerminationRequested,
            final boolean isRecoveryRequested
    ) {
        super(UUID.randomUUID());
        this.correlationId = correlationId;
        this.taskId = taskId;
        this.isTerminationRequested = isTerminationRequested;
        this.isRecoveryRequested = isRecoveryRequested;
        this.setUpdatedAt(Instant.now());
        this.setCreatedAt(Instant.now());
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(final Lock lock) {
        this.lock = lock;
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

    public String getCorrelationId() {
        return correlationId;
    }

    public TaskTube setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public TaskTube setTaskId(final UUID taskId) {
        this.taskId = taskId;
        return this;
    }

    public boolean isTerminationRequested() {
        return isTerminationRequested;
    }

    public TaskTube setTerminationRequested(final boolean terminationRequested) {
        isTerminationRequested = terminationRequested;
        return this;
    }

    public boolean isRecoveryRequested() {
        return isRecoveryRequested;
    }

    public TaskTube setRecoveryRequested(final boolean recoveryRequested) {
        isRecoveryRequested = recoveryRequested;
        return this;
    }
}
