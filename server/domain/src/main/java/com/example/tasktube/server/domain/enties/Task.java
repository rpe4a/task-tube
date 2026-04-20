package com.example.tasktube.server.domain.enties;

import com.example.tasktube.server.domain.events.DomainEvent;
import com.example.tasktube.server.domain.events.logs.BarrierAddedEvent;
import com.example.tasktube.server.domain.events.logs.LogRecordsAddedEvent;
import com.example.tasktube.server.domain.events.logs.TaskChildrenAddedEvent;
import com.example.tasktube.server.domain.exceptions.ValidationDomainException;
import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.values.Lock;
import com.example.tasktube.server.domain.values.TaskSettings;
import com.example.tasktube.server.domain.values.argument.Argument;
import com.example.tasktube.server.domain.values.slot.Slot;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Task extends Entity<UUID> {

    public static final String BARRIER_IS_FAILED = "Barrier '%s' has FAILED state.";
    private final List<LogRecord> logs = new LinkedList<>();
    private String name;
    private String tube;
    private Status status;
    private String correlationId;
    private UUID parentId;
    private List<Slot> input;
    private Slot output;
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

    private Task(final UUID id,
                 final String name,
                 final String tube,
                 final String correlationId,
                 final UUID parentId,
                 final Status status,
                 final List<Slot> input,
                 final Slot output,
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
                 final String handledBy
    ) {
        super(id);
        this.name = name;
        this.tube = tube;
        this.correlationId = correlationId;
        this.parentId = parentId;
        this.status = status;
        this.input = input;
        this.output = output;
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
        this.settings = Optional
                .ofNullable(settings)
                .orElse(TaskSettings.getDefault());

        addLog("Task has been created.", createdAt);
    }

    public Task() {
        super(UUID.randomUUID());
    }

    public static Task pushNew(
            final UUID id,
            final String name,
            final String tube,
            final String correlationId,
            final List<Slot> input,
            final Instant createdAt,
            final TaskSettings settings,
            final List<UUID> waitingTaskIdList,
            final String client
    ) {
        final Task task = new Task(
                id,
                name,
                tube,
                correlationId,
                null,
                Status.CREATED,
                input,
                null,
                Instant.now(),
                createdAt,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                new Lock(Instant.now(), true, client),
                settings,
                null
        );

        task.addWaitingTasks(waitingTaskIdList, client);

        return task;
    }

    private void addWaitingTasks(final List<UUID> waitingTaskIdList, final String client) {
        if (waitingTaskIdList.isEmpty()) {
            schedule(Instant.now(), client);
        } else {
            addEvent(BarrierAddedEvent.create(getId(), waitingTaskIdList, Barrier.Type.START));
            addLog(String.format("Waiting '%s' tasks before being scheduled.", waitingTaskIdList.size()));
            unlock();
        }
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
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
        checkHandleBy(Collections.singletonList(Status.CREATED), client);
    }

    private void checkCancel(final String client) {
        checkHandleBy(Collections.singletonList(Status.CREATED), client);
    }

    private void checkStart(final String client) {
        checkHandleBy(Collections.singletonList(Status.SCHEDULED), client);
    }

    private void checkProcess(final String client) {
        checkHandleBy(Collections.singletonList(Status.PROCESSING), client);
    }

    private void checkFinish(final Slot output, final String client) {
        checkHandleBy(Collections.singletonList(Status.PROCESSING), client);
        if (Objects.isNull(output)) {
            throw new ValidationDomainException("Parameter output cannot be null.");
        }
    }

    private void checkFail(final String client) {
        checkHandleBy(Collections.singletonList(Status.PROCESSING), client);
    }

    private void checkAbort(final String client) {
        checkHandleBy(List.of(Status.PROCESSING, Status.FINISHED), client);
    }

    private void checkComplete(final String client) {
        checkHandleBy(Collections.singletonList(Status.FINISHED), client);
    }

    private void checkHandleBy(final List<Status> expectedStatuses, final String client) {
        if (Objects.isNull(client)) {
            throw new ValidationDomainException("Parameter client cannot be null.");
        }
        if (Objects.isNull(expectedStatuses)) {
            throw new ValidationDomainException("Parameter expected status cannot be null.");
        }
        if (!expectedStatuses.contains(getStatus())) {
            throw new ValidationDomainException("'%s' is invalid task state.".formatted(getStatus()));
        }
        if (!getLock().isLockedBy(client)) {
            throw new ValidationDomainException("Task is not locked by the client '%s'.".formatted(client));
        }
    }

    private void schedule(final Instant scheduledAt, final String client) {
        checkSchedule(client);
        setStatus(Status.SCHEDULED);
        setScheduledAt(scheduledAt);
        addLog("Task has been scheduled.");
        unlock();
    }

    private void cancel(final Instant canceledAt, final String failedReason, final String client) {
        checkCancel(client);
        setCanceledAt(canceledAt);
        setStatus(Status.CANCELED);
        setFailedReason(failedReason);
        addLog("Task has been canceled.");
        unlock();
    }

    public void start(final Instant startedAt, final String client) {
        checkStart(client);
        setStartedAt(startedAt);
        setHeartbeatAt(startedAt);
        setStatus(Status.PROCESSING);
        addLog("Task has been started.");
        setLock(getLock().prolong());
    }

    public void process(final Instant heartbeatAt, final String client) {
        checkProcess(client);
        setHeartbeatAt(heartbeatAt);
        setStatus(Status.PROCESSING);
        addLog("Task is being processed.");
        setLock(getLock().prolong());
    }

    public void finish(
            final Instant finishedAt,
            final Slot output,
            final List<Task> children,
            final List<LogRecord> logs,
            final String client
    ) {
        checkFinish(output, client);
        setFinishedAt(finishedAt);
        setStatus(Status.FINISHED);
        setOutput(output);
        setHandledBy(client);

        if (!logs.isEmpty()) {
            addLogs(logs);
        }

        addLog("Task has been finished.", finishedAt);

        if (!children.isEmpty()) {
            children.forEach(c -> c.attachToParent(this));
            addEvent(new TaskChildrenAddedEvent(getId(), children));
            addLog(String.format("Waiting '%s' children before being terminated.", children.size()));
        } else {
            complete(Instant.now(), client);
        }

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
            addLog("Task has been failed.");
            unlock();
        } else {
            abort(failedAt, failedReason, client);
        }
    }

    public void abort(final Instant abortedAt, final String failedReason, final String client) {
        checkAbort(client);
        setStatus(Status.ABORTED);
        setAbortedAt(abortedAt);
        setFailedReason(failedReason);
        addLog("Task has been aborted.");
        unlock();
    }

    private void complete(final Instant completedAt, final String client) {
        checkComplete(client);
        setStatus(Status.COMPLETED);
        setCompletedAt(completedAt);
        addLog("Task has been completed.");
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
                addLog("Task has stuck in CREATED state. It has been unlocked and returned to the queue.");
                setCreatedAt(Instant.now());
            }
            if (Status.SCHEDULED.equals(getStatus())) {
                addLog("Task has stuck in SCHEDULED state. It has been unlocked and returned to the queue.");
                setScheduledAt(Instant.now());
            }
            if (Status.PROCESSING.equals(getStatus())) {
                addLog("Task has stuck in PROCESSING state. It has been unlocked and returned to the queue.");
                setStatus(Status.SCHEDULED);
                setScheduledAt(Instant.now());
                setStartedAt(null);
                setHeartbeatAt(null);
            }
            if (Status.FINISHED.equals(getStatus())) {
                addLog("Task has stuck in FINISHED state. It has been unlocked and returned to the queue.");
                setFinishedAt(Instant.now());
            }
            if (Status.CANCELED.equals(getStatus())) {
                addLog("Task has stuck in CANCELED state. It has been unlocked and returned to the queue.");
                setCanceledAt(Instant.now());
            }
            if (Status.ABORTED.equals(getStatus())) {
                addLog("Task has stuck in ABORTED state. It has been unlocked and returned to the queue.");
                setAbortedAt(Instant.now());
            }
            if (Status.COMPLETED.equals(getStatus())) {
                addLog("Task has stuck in COMPLETED state. It has been unlocked and returned to the queue.");
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

    public void addLog(final String message) {
        this.logs.add(LogRecord.info(getId(), message));
    }

    public void addLog(final String message, final Instant timestamp) {
        this.logs.add(LogRecord.info(getId(), message, timestamp));
    }

    public void addLogs(final Collection<LogRecord> logs) {
        this.logs.addAll(logs);
    }

    @Override
    public List<DomainEvent> pullEvents() {
        final List<DomainEvent> events = new ArrayList<>(super.pullEvents());

        if (!logs.isEmpty()) {
            events.add(new LogRecordsAddedEvent(logs));
        }
        return events;
    }

    public List<Argument> getArguments(final IArgumentFiller argumentFiller) {
        if (Objects.isNull(argumentFiller)) {
            throw new ValidationDomainException("Parameter argumentFiller cannot be null.");
        }

        return getInput().stream()
                .map(slot -> slot.fill(argumentFiller))
                .toList();
    }

    public boolean isExpired(final String client) {
        checkProcess(client);

        final Instant timeout = getStartedAt().plus(settings.timeoutSeconds(), ChronoUnit.SECONDS);
        final Instant now = Instant.now();

        return now.isAfter(timeout);
    }

    public boolean isProcessing() {
        return getStatus().equals(Status.PROCESSING);
    }

    public boolean isLocked() {
        return getLock().locked();
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
