package com.example.tasktube.server.domain.enties;

import com.example.tasktube.server.domain.exceptions.ValidationDomainException;
import com.example.tasktube.server.domain.values.Lock;
import com.example.tasktube.server.domain.values.LogRecord;
import com.example.tasktube.server.domain.values.TaskSettings;
import com.example.tasktube.server.domain.values.slot.Slot;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Task {

    public static final String BARRIER_IS_FAILED = "Barrier '%s' has FAILED state.";
    private UUID id;
    private String name;
    private String tube;
    private Status status;
    private String correlationId;
    private UUID parentId;
    private List<Slot> input;
    private Slot output;
    private boolean isRoot;
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
    private Lock lock;
    private TaskSettings settings;
    private String handledBy;

    private List<LogRecord> logs;

    public Task(final UUID id,
                final String name,
                final String tube,
                final Status status,
                final String correlationId,
                final UUID parentId,
                final List<Slot> input,
                final Slot output,
                final boolean isRoot,
                final Instant updatedAt,
                final Instant createdAt,
                final Instant canceledAt,
                final Instant scheduledAt,
                final Instant startedAt,
                final Instant heartbeatAt,
                final Instant finishedAt,
                final Instant failedAt,
                final Instant abortedAt,
                final Instant completedAt,
                final int failures,
                final String failedReason,
                final Lock lock,
                final TaskSettings settings,
                final List<LogRecord> logs,
                final String handledBy
    ) {
        this.id = id;
        this.name = name;
        this.tube = tube;
        this.status = status;
        this.correlationId = correlationId;
        this.parentId = parentId;
        this.input = input;
        this.output = output;
        this.isRoot = isRoot;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.canceledAt = canceledAt;
        this.scheduledAt = scheduledAt;
        this.startedAt = startedAt;
        this.heartbeatAt = heartbeatAt;
        this.finishedAt = finishedAt;
        this.failedAt = failedAt;
        this.abortedAt = abortedAt;
        this.completedAt = completedAt;
        this.failures = failures;
        this.failedReason = failedReason;
        this.lock = lock;
        this.handledBy = handledBy;
        this.logs = Optional
                .ofNullable(logs).orElse(new LinkedList<>());
        this.settings = Optional
                .ofNullable(settings)
                .orElse(TaskSettings.getDefault());

        addLog(LogRecord.info("Task has been created."));
    }

    public Task() {
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
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

    public List<Slot> getInput() {
        return input;
    }

    public void setInput(final List<Slot> input) {
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

    private void checkSchedule(final String client) {
        checkHandleBy(Status.CREATED, client);
    }

    private void checkCancel(final String client) {
        checkHandleBy(Status.CREATED, client);
    }

    private void checkStart(final String client) {
        checkHandleBy(Status.SCHEDULED, client);
    }

    private void checkProcess(final String client) {
        checkHandleBy(Status.PROCESSING, client);
    }

    private void checkFinish(final Slot output, final String client) {
        checkHandleBy(Status.PROCESSING, client);
        if (Objects.isNull(output)) {
            throw new ValidationDomainException("Parameter output cannot be null.");
        }
    }

    private void checkFail(final String client) {
        checkHandleBy(Status.PROCESSING, client);
    }

    private void checkAbort(final String client) {
        checkHandleBy(Status.FINISHED, client);
    }

    private void checkComplete(final String client) {
        checkHandleBy(Status.FINISHED, client);
    }

    private void checkHandleBy(final Status expectedStatus, final String client) {
        if (Objects.isNull(client)) {
            throw new ValidationDomainException("Parameter client cannot be null.");
        }
        if (Objects.isNull(expectedStatus)) {
            throw new ValidationDomainException("Parameter expected status cannot be null.");
        }
        if (getStatus().isNotEqual(expectedStatus)) {
            throw new ValidationDomainException("Invalid task state. Expected '%s' but was '%s'.".formatted(expectedStatus, getStatus()));
        }
        if (!getLock().isLockedBy(client)) {
            throw new ValidationDomainException("Task is not locked by the client '%s'.".formatted(client));
        }
    }

    private void schedule(final Instant scheduledAt, final String client) {
        checkSchedule(client);
        setStatus(Status.SCHEDULED);
        setScheduledAt(scheduledAt);
        addLog(LogRecord.info("Task has been scheduled."));
        unlock();
    }

    private void cancel(final Instant canceledAt, final String failedReason, final String client) {
        checkCancel(client);
        setCanceledAt(canceledAt);
        setStatus(Status.CANCELED);
        setFailedReason(failedReason);
        addLog(LogRecord.info("Task has been canceled."));
        unlock();
    }

    public void start(final Instant startedAt, final String client) {
        checkStart(client);
        setStartedAt(startedAt);
        setStatus(Status.PROCESSING);
        addLog(LogRecord.info("Task has been started."));
        setLock(getLock().prolong());
    }

    public void process(final Instant heartbeatAt, final String client) {
        checkProcess(client);
        setHeartbeatAt(heartbeatAt);
        setStatus(Status.PROCESSING);
        addLog(LogRecord.info("Task is being processed."));
        setLock(getLock().prolong());
    }

    public void finish(final Instant finishedAt, final Slot output, final List<LogRecord> logs, final String client) {
        checkFinish(output, client);
        setFinishedAt(finishedAt);
        setStatus(Status.FINISHED);
        setOutput(output);
        setHandledBy(client);
        addLogs(logs);
        addLog(LogRecord.info("Task has been finished."));
        unlock();
    }

    public void fail(final Instant failedAt, final String failedReason, final String client) {
        checkFail(client);
        if (getFailures() < getSettings().maxFailures()) {
            setStatus(Status.SCHEDULED);
            setScheduledAt(failedAt.plusSeconds(getSettings().failureRetryTimeoutSeconds()));
            setStartedAt(null);
            setHeartbeatAt(null);
            setFinishedAt(null);
            setFailedAt(failedAt);
            setFailures(getFailures() + 1);
            setFailedReason(failedReason);
            setOutput(null);
            addLog(LogRecord.info("Task has been failed."));
            unlock();
        } else {
            setStatus(Status.ABORTED);
            setAbortedAt(Instant.now());
            setFinishedAt(null);
            setFailedAt(failedAt);
            setFailedReason(failedReason);
            setOutput(null);
            addLog(LogRecord.info("Task has been aborted."));
            unlock();
        }
    }

    private void abort(final Instant abortedAt, final String failedReason, final String client) {
        checkAbort(client);
        setStatus(Status.ABORTED);
        setAbortedAt(abortedAt);
        setFailedReason(failedReason);
        addLog(LogRecord.info("Task has been aborted."));
        unlock();
    }

    private void complete(final Instant completedAt, final String client) {
        checkComplete(client);
        setStatus(Status.COMPLETED);
        setCompletedAt(completedAt);
        addLog(LogRecord.info("Task has been completed."));
        unlock();
    }

    public Task attachToParent(final Task parent) {
        if (Objects.isNull(parent)) {
            throw new ValidationDomainException("Parameter parent cannot be null.");
        }
        setParentId(parent.getId());
        return this;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(final UUID parentId) {
        this.parentId = parentId;
    }

    public Barrier addStartBarrier(final List<UUID> taskIdList) {
        return new Barrier(
                UUID.randomUUID(),
                getId(),
                taskIdList,
                Barrier.Type.START,
                Barrier.Status.WAITING,
                Instant.now(),
                Instant.now(),
                null,
                Lock.free()
        );
    }

    public Barrier addFinishBarrier(final List<UUID> taskIdList) {
        return new Barrier(
                UUID.randomUUID(),
                getId(),
                taskIdList,
                Barrier.Type.FINISH,
                Barrier.Status.WAITING,
                Instant.now(),
                Instant.now(),
                null,
                Lock.free()
        );
    }

    public Slot getOutput() {
        return output;
    }

    public void setOutput(final Slot output) {
        this.output = output;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(final Instant failedAt) {
        this.failedAt = failedAt;
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

    public boolean isCompleted() {
        return Status.COMPLETED.equals(getStatus());
    }

    public boolean isFinalized() {
        return Status.ABORTED.equals(getStatus()) || Status.CANCELED.equals(getStatus());
    }

    public boolean isTerminated() {
        return isCompleted() || isFinalized();
    }

    public Instant getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(final Instant canceledAt) {
        this.canceledAt = canceledAt;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(final String handledBy) {
        this.handledBy = handledBy;
    }

    public void unlock() {
        setUpdatedAt(Instant.now());
        setLock(getLock().unlock());
    }

    public void unblock(final int lockedTimeoutSeconds) {
        if (lockedTimeoutSeconds <= 0) {
            throw new ValidationDomainException("Parameter lockedTimeoutSeconds must be more then zero.");
        }

        final Instant lockedTimeout = Instant.now().minus(lockedTimeoutSeconds, ChronoUnit.SECONDS);

        if (getLock().isLockedBefore(lockedTimeout)) {
            if (Status.CREATED.equals(getStatus())) {
                addLog(LogRecord.info("Execution of task is blocked in CREATED state, has been unlocked and returned to queue."));
                setCreatedAt(Instant.now());
            }
            if (Status.SCHEDULED.equals(getStatus())) {
                addLog(LogRecord.info("Execution of task is blocked in SCHEDULED state, has been unlocked and returned to queue."));
                setScheduledAt(Instant.now());
            }
            if (Status.PROCESSING.equals(getStatus())) {
                addLog(LogRecord.info("Execution of task is blocked in PROCESSING state, has been unlocked and returned to queue."));
                setStatus(Status.SCHEDULED);
                setScheduledAt(Instant.now());
                setStartedAt(null);
                setHeartbeatAt(null);
            }
            if (Status.FINISHED.equals(getStatus())) {
                addLog(LogRecord.info("Execution of task is blocked in FINISHED state, has been unlocked and returned to queue."));
                setFinishedAt(Instant.now());
            }
            if (Status.CANCELED.equals(getStatus())) {
                addLog(LogRecord.info("Execution of task is blocked in CANCELED state, has been unlocked and returned to queue."));
                setCanceledAt(Instant.now());
            }
            if (Status.ABORTED.equals(getStatus())) {
                addLog(LogRecord.info("Execution of task is blocked in ABORTED state, has been unlocked and returned to queue."));
                setAbortedAt(Instant.now());
            }
            if (Status.COMPLETED.equals(getStatus())) {
                addLog(LogRecord.info("Execution of task is blocked in COMPLETED state, has been unlocked and returned to queue."));
                setCompletedAt(Instant.now());
            }

            unlock();
        }
    }

    public void releaseBarrier(final Barrier barrier, final String client) {
        if (Objects.isNull(barrier)) {
            throw new ValidationDomainException("Parameter barrier cannot be null.");
        }
        if (barrier.isNotReleased()) {
            throw new ValidationDomainException("Barrier is not released");
        }

        if (barrier.getType().equals(Barrier.Type.START)) {
            switch (barrier.getStatus()) {
                case COMPLETED -> schedule(Instant.now(), client);
                case FAILED -> cancel(Instant.now(), BARRIER_IS_FAILED.formatted(barrier.getId()), client);
            }
        }

        if (barrier.getType().equals(Barrier.Type.FINISH)) {
            switch (barrier.getStatus()) {
                case COMPLETED -> complete(Instant.now(), client);
                case FAILED -> abort(Instant.now(), BARRIER_IS_FAILED.formatted(barrier.getId()), client);
            }
        }
    }

    public List<LogRecord> getLogs() {
        return logs;
    }

    public void setLogs(final List<LogRecord> logs) {
        this.logs = logs;
    }

    public void addLog(final LogRecord log) {
        this.logs.add(log);
    }

    public void addLogs(final Collection<LogRecord> logs) {
        this.logs.addAll(logs);
    }

    public enum Status {
        CREATED,
        SCHEDULED,
        PROCESSING,
        FINISHED,
        ABORTED,
        CANCELED,
        COMPLETED;

        public boolean isNotEqual(final Status other) {
            return !this.equals(other);
        }
    }
}
