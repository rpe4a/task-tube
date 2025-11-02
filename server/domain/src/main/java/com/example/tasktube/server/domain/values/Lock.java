package com.example.tasktube.server.domain.values;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.time.Instant;

public record Lock(Instant lockedAt, boolean locked, String lockedBy) {
    public Lock() {
        this(null, false, null);
    }

    public Lock unlock() {
        return new Lock(null, false, null);
    }

    public boolean isLockedBy(final String who) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(who));

        return locked && who.equals(lockedBy());
    }
}
