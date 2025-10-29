package com.example.tasktube.server.domain.enties;

import com.example.tasktube.server.domain.values.Lock;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class Tube {
    private Tube() {
    }

    public static Task pushTask(
            final String name,
            final String queue,
            final Map<String, Object> input,
            final Instant createdAt) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(name));
        Preconditions.checkArgument(StringUtils.isNotEmpty(queue));
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(createdAt);

        return new Task(
                UUID.randomUUID(),
                name,
                queue,
                Task.Status.CREATED,
                input,
                true,
                Instant.now(),
                createdAt,
                null,
                null,
                null,
                null,
                new Lock()
        );
    }
}
