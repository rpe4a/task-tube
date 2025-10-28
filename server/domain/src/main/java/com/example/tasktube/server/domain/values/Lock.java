package com.example.tasktube.server.domain.values;

import java.time.Instant;

public class Lock {
    private final Instant lockedAt;
    private final boolean locked;
    private final String lockedBy;

    public Lock() {
        this.lockedAt = null;
        this.locked = false;
        this.lockedBy = null;
    }
    public Lock(final Instant lockedAt, final boolean locked, final String lockedBy) {
        this.lockedAt = lockedAt;
        this.locked = locked;
        this.lockedBy = lockedBy;
    }

    public Instant getLockedAt() {
        return lockedAt;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public Lock unlock() {
        return new Lock(null, false, null);
    }
}
