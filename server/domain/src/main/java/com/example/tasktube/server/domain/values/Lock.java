package com.example.tasktube.server.domain.values;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.time.Instant;

public record Lock(Instant lockedAt, boolean locked, String lockedBy) {
    public Lock() {
        this(null, false, null);
    }

    public static Lock free() {
        return new Lock();
    }

    public Lock unlock() {
        return free();
    }

    public boolean isLockedBy(final String who) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(who));

        return locked && who.equals(lockedBy());
    }

    public boolean isFree() {
        return !locked && Strings.isNullOrEmpty(lockedBy);
    }

    public Lock prolong() {
        return new Lock(Instant.now(), true, lockedBy());
    }

    public boolean isLockedBefore(final Instant date) {
        return locked && lockedAt.isBefore(date);
    }
}
