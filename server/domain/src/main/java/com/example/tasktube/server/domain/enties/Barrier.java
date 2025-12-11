package com.example.tasktube.server.domain.enties;

import com.example.tasktube.server.domain.exceptions.ValidationDomainException;
import com.example.tasktube.server.domain.values.Lock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
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

    public void setReleased(final boolean released) {
        this.released = released;
    }

    public boolean isNotReleased() {
        return !isReleased();
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
        if (Objects.isNull(client)) {
            throw new ValidationDomainException("Client cannot be null.");
        }
        if (!getLock().isLockedBy(client)) {
            throw new ValidationDomainException("Barrier is not locked by the client '%s'.".formatted(client));
        }
        if (isReleased()) {
            throw new ValidationDomainException("Barrier is already released.".formatted(client));
        }

        setReleased(true);
        setReleasedAt(Instant.now());
        setUpdatedAt(Instant.now());
        unlock();
    }

    public void unlock() {
        setUpdatedAt(Instant.now());
        setLock(lock.unlock());
    }

    public void unblock(final int lockedTimeoutSeconds) {
        if (lockedTimeoutSeconds <= 0) {
            throw new ValidationDomainException("Lock timeout must be more then zero.");
        }

        final Instant lockedTimeout = Instant.now().minus(lockedTimeoutSeconds, ChronoUnit.SECONDS);

        if (getLock().isLockedBefore(lockedTimeout)) {
            setUpdatedAt(Instant.now());
            unlock();
        }
    }

    public enum Type {
        START,
        FINISH
    }
}
