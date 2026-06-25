package com.example.tasktube.server.application.queries.views;

import com.example.tasktube.server.domain.enties.Task;

import java.time.Instant;
import java.util.UUID;

public class ParentTaskView {

    private boolean isTerminationRequested;
    private boolean isRecoveryRequested;
    private UUID id;
    private String name;
    private String tube;
    private Task.Status status;
    private String correlationId;
    private Instant updatedAt;
    private Instant createdAt;
    private Instant abortedAt;
    private Instant cancelledAt;
    private Instant completedAt;
    private String handledBy;
    private int totalCount;

    public boolean isTerminationRequested() {
        return isTerminationRequested;
    }

    public void setTerminationRequested(final boolean terminationRequested) {
        isTerminationRequested = terminationRequested;
    }

    public boolean isRecoveryRequested() {
        return isRecoveryRequested;
    }

    public void setRecoveryRequested(final boolean recoveryRequested) {
        isRecoveryRequested = recoveryRequested;
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

    public Task.Status getStatus() {
        return status;
    }

    public void setStatus(final Task.Status status) {
        this.status = status;
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

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(final String handledBy) {
        this.handledBy = handledBy;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(final int totalCount) {
        this.totalCount = totalCount;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(final Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
