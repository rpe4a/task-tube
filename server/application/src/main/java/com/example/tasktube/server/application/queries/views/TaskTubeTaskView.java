package com.example.tasktube.server.application.queries.views;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.TaskSettings;

import java.time.Instant;
import java.util.UUID;

public class TaskTubeTaskView {

    private UUID id;
    private String name;
    private String tube;
    private Task.Status status;
    private String correlationId;
    private UUID parentId;
    private String input;
    private String output;
    private Instant updatedAt;
    private Instant createdAt;
    private Instant canceledAt;
    private Instant scheduledAt;
    private Instant startedAt;
    private Instant heartbeatAt;
    private Instant finishedAt;
    private Instant failedAt;
    private Instant abortedAt;
    private Instant completedAt;
    private int failures;
    private String failedReason;
    private TaskSettings settings;
    private String handledBy;
    private int countChildren;

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

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(final UUID parentId) {
        this.parentId = parentId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(final String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(final String output) {
        this.output = output;
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

    public Instant getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(final Instant canceledAt) {
        this.canceledAt = canceledAt;
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

    public Instant getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(final Instant failedAt) {
        this.failedAt = failedAt;
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

    public int getFailures() {
        return failures;
    }

    public void setFailures(final int failures) {
        this.failures = failures;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(final String failedReason) {
        this.failedReason = failedReason;
    }

    public TaskSettings getSettings() {
        return settings;
    }

    public void setSettings(final TaskSettings settings) {
        this.settings = settings;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(final String handledBy) {
        this.handledBy = handledBy;
    }

    public int getCountChildren() {
        return countChildren;
    }

    public void setCountChildren(final int countChildren) {
        this.countChildren = countChildren;
    }
}
