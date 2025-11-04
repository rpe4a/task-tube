package com.example.tasktube.server.domain.enties;

import com.example.tasktube.server.domain.values.Lock;
import com.example.tasktube.server.domain.values.TaskSettings;
import com.google.common.base.Preconditions;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Task {
    private UUID id;
    private String name;
    private String tube;
    private Status status;
    private UUID parentId;
    private Map<String, Object> input;
    private Map<String, Object> output;
    private boolean isRoot;
    private UUID startBarrier;
    private UUID finishBarrier;
    private Instant updatedAt;
    private Instant createdAt;
    private Instant scheduledAt;
    private Instant startedAt;
    private Instant heartbeatAt;
    private Instant finishedAt;
    private Instant failedAt;
    private Instant abortedAt;
    private Instant finalizedAt;
    private int failures;
    private String failedReason;
    private Lock lock;
    private TaskSettings settings;

    public Task(final UUID id,
                final String name,
                final String tube,
                final Status status,
                final UUID parentId,
                final Map<String, Object> input,
                final Map<String, Object> output,
                final boolean isRoot,
                final UUID startBarrier,
                final UUID finishBarrier,
                final Instant updatedAt,
                final Instant createdAt,
                final Instant scheduledAt,
                final Instant startedAt,
                final Instant heartbeatAt,
                final Instant finishedAt,
                final Instant failedAt,
                final Instant abortedAt,
                final Instant finalizedAt,
                final int failures,
                final String failedReason,
                final Lock lock,
                final TaskSettings settings
    ) {
        this.id = id;
        this.name = name;
        this.tube = tube;
        this.status = status;
        this.parentId = parentId;
        this.input = input;
        this.output = output;
        this.isRoot = isRoot;
        this.startBarrier = startBarrier;
        this.finishBarrier = finishBarrier;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.scheduledAt = scheduledAt;
        this.startedAt = startedAt;
        this.heartbeatAt = heartbeatAt;
        this.finishedAt = finishedAt;
        this.failedAt = failedAt;
        this.abortedAt = abortedAt;
        this.finalizedAt = finalizedAt;
        this.failures = failures;
        this.failedReason = failedReason;
        this.lock = lock;
        this.settings = settings;
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

    public void schedule() {
        setStatus(Status.SCHEDULED);
        setScheduledAt(Instant.now());
    }

    private boolean canStart(final String client) {
        canHandleBy(client, Status.SCHEDULED);
        return true;
    }

    private boolean canProcess(final String client) {
        canHandleBy(client, Status.PROCESSING);
        return true;
    }

    private boolean canFinish(final String client, final Map<String, Object> output) {
        Preconditions.checkNotNull(output);
        canHandleBy(client, Status.PROCESSING);
        return true;
    }

    private boolean canFail(final String client) {
        canHandleBy(client, Status.PROCESSING);
        return true;
    }

    private void canHandleBy(final String client, final Status status) {
        // TODO: need to use DOMAIN Exception
        Preconditions.checkNotNull(client);
        Preconditions.checkNotNull(status);
        Preconditions.checkState(getLock().isLockedBy(client), "The client '%s' can't start task.".formatted(client));
        Preconditions.checkState(getStatus().equals(status), "Status must be %s.".formatted(status));
    }

    public void start(final String client, final Instant startedAt) {
        if (canStart(client)) {
            setStartedAt(startedAt);
            setStatus(Status.PROCESSING);
        }
    }

    public void process(final String client, final Instant heartbeatAt) {
        if (canProcess(client)) {
            setHeartbeatAt(heartbeatAt);
            setStatus(Status.PROCESSING);
        }
    }

    public void finish(final String client, final Instant finishedAt, final Map<String, Object> output) {
        if (canFinish(client, output)) {
            setFinishedAt(finishedAt);
            setStatus(Status.FINISHED);
            setOutput(output);
            setLock(getLock().unlock());
        }
    }

    public void fail(final String client, final Instant failedAt, final String failedReason) {
        if (canFail(client)) {
            if (getFailures() < getSettings().maxFailures()) {
                setStatus(Status.SCHEDULED);
                setScheduledAt(Instant.now().plusSeconds(getSettings().failureRetryTimeoutSeconds()));
                setStartedAt(null);
                setHeartbeatAt(null);
                setFinishedAt(null);
                setFailedAt(failedAt);
                setFailures(getFailures() + 1);
                setFailedReason(failedReason);
                setFinishBarrier(null);
                setOutput(null);
                setLock(getLock().unlock());
            } else {
                setStatus(Status.ABORTED);
                setAbortedAt(Instant.now());
                setFinishedAt(null);
                setFailedAt(failedAt);
                setFailedReason(failedReason);
                setFinishBarrier(null);
                setOutput(null);
                setLock(getLock().unlock());
            }
        }
    }

    public Task attachParent(final Task task) {
        Preconditions.checkNotNull(task);
        setParentId(task.getId());
        return this;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(final UUID parentId) {
        this.parentId = parentId;
    }

    public Barrier addStartBarrier(final List<UUID> waitForTaskIds) {
        Preconditions.checkNotNull(waitForTaskIds);
        Preconditions.checkArgument(!waitForTaskIds.isEmpty());

        final Barrier barrier = new Barrier(
                UUID.randomUUID(),
                getId(),
                waitForTaskIds,
                Barrier.Type.START,
                false,
                Instant.now(),
                Instant.now(),
                null,
                null
        );

        setStartBarrier(barrier.getId());
        return barrier;
    }

    public Barrier addFinishBarrier(final List<UUID> waitForTaskIds) {
        Preconditions.checkNotNull(waitForTaskIds);
        Preconditions.checkArgument(!waitForTaskIds.isEmpty());

        final Barrier barrier = new Barrier(
                UUID.randomUUID(),
                getId(),
                waitForTaskIds,
                Barrier.Type.FINISH,
                false,
                Instant.now(),
                Instant.now(),
                null,
                null
        );

        setFinishBarrier(barrier.getId());
        return barrier;
    }

    public UUID getStartBarrier() {
        return startBarrier;
    }

    public void setStartBarrier(final UUID startBarrier) {
        this.startBarrier = startBarrier;
    }

    public UUID getFinishBarrier() {
        return finishBarrier;
    }

    public void setFinishBarrier(final UUID finishBarrier) {
        this.finishBarrier = finishBarrier;
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public void setOutput(final Map<String, Object> output) {
        this.output = output;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(final Instant failedAt) {
        this.failedAt = failedAt;
    }

    public Instant getFinalizedAt() {
        return finalizedAt;
    }

    public void setFinalizedAt(final Instant finalizedAt) {
        this.finalizedAt = finalizedAt;
    }

    public int getFailures() {
        return failures;
    }

    public void setFailures(final int failures) {
        this.failures = failures;
    }

    public TaskSettings getSettings() {
        return settings;
    }

    public void setSettings(final TaskSettings settings) {
        this.settings = settings;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(final String failedReason) {
        this.failedReason = failedReason;
    }

    public Instant getAbortedAt() {
        return abortedAt;
    }

    public void setAbortedAt(final Instant abortedAt) {
        this.abortedAt = abortedAt;
    }

    public enum Status {
        CREATED,
        SCHEDULED,
        PROCESSING,
        FINISHED,
        ABORTED,
        WAITING_FINALIZED,
        FINALIZED
    }
}
