package com.example.tasktube.server.domain.enties;

import com.example.tasktube.server.domain.values.Lock;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Task {
    private UUID id;
    private String name;
    private String queue;
    private Status status;
    private Map<String, Object> input;
    private boolean isRoot;
    private Instant createAt;
    private Instant updateAt;
    private Lock lock;

    public Task(final UUID id,
                final String name,
                final String queue,
                final Status status,
                final Map<String, Object> input,
                final boolean isRoot,
                final Instant createAt,
                final Instant updateAt,
                final Lock lock
    ) {
        this.id = id;
        this.name = name;
        this.queue = queue;
        this.status = status;
        this.input = input;
        this.isRoot = isRoot;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.lock = lock;
    }

    public Task() {
    }

    public static Task getRunningTask(
            final String name,
            final String queue,
            final Map<String, Object> input,
            final Instant createdAt
    ) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(name));
        Preconditions.checkArgument(StringUtils.isNotEmpty(queue));
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(createdAt);

        return new Task(
                UUID.randomUUID(),
                name,
                queue,
                Status.CREATED,
                input,
                true,
                createdAt,
                Instant.now(),
                new Lock()
        );
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

    public String getQueue() {
        return queue;
    }

    public void setQueue(final String queue) {
        this.queue = queue;
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

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(final Instant createAt) {
        this.createAt = createAt;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(final Instant updateAt) {
        this.updateAt = updateAt;
    }

    public void schedule() {
        setStatus(Status.SCHEDULED);
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(final Lock lock) {
        this.lock = lock;
    }

    public enum Status {
        SCHEDULED, CREATED
    }
}
