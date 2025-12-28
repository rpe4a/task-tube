package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.http.dto.TaskRequest;
import com.example.tasktube.client.sdk.task.slot.Slot;
import com.example.tasktube.client.sdk.task.slot.SlotValueSerializer;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TaskRecord<TResult> {

    private final List<Value<?>> arguments = new LinkedList<>();
    private final List<UUID> waitForTasks = new LinkedList<>();
    private final TaskSettings setting = TaskSettings.DEFAULT();
    private UUID id;
    private UUID parentId;
    private String name;
    private String tube;
    private String correlationId;

    private TaskRecord() {
        this(UUID.randomUUID());
    }

    private TaskRecord(@Nonnull final UUID id) {
        this.id = Objects.requireNonNull(id);
    }

    @Nonnull
    public TaskSettings getSetting() {
        return setting;
    }

    @Nonnull
    public UUID getId() {
        return id;
    }

    private void setId(@Nonnull final UUID id) {
        this.id = id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    private void setName(@Nonnull final String name) {
        this.name = name;
    }

    @Nonnull
    public TaskResult<TResult> getResult() {
        return new TaskResult<>(id);
    }

    void addArgument(@Nonnull final Value<?> value) {
        arguments.add(Objects.requireNonNull(value));
    }

    void waitFor(@Nonnull final UUID taskId) {
        waitForTasks.add(Objects.requireNonNull(taskId));
    }

    public void configure(@Nonnull final TaskConfiguration[] configurations) {
        for (final TaskConfiguration configuration : Objects.requireNonNull(configurations)) {
            Objects.requireNonNull(configuration).applyTo(this);
        }
    }

    @Nonnull
    public UUID getParentId() {
        return parentId;
    }

    private void setParentId(@Nonnull final UUID parentId) {
        this.parentId = parentId;
    }

    @Nonnull
    public String getTube() {
        return tube;
    }

    private void setTube(@Nonnull final String tube) {
        this.tube = tube;
    }

    @Nonnull
    public String getCorrelationId() {
        return correlationId;
    }

    private void setCorrelationId(@Nonnull final String correlationId) {
        this.correlationId = correlationId;
    }

    @Nonnull
    public TaskRequest toRequest(@Nonnull final SlotValueSerializer slotSerializer) {
        Preconditions.checkNotNull(slotSerializer);
        return new TaskRequest(
                getId(),
                getName(),
                getTube(),
                getCorrelationId(),
                arguments.stream()
                        .map(v -> v.serialize(slotSerializer))
                        .toList()
                        .toArray(new Slot[0]),
                waitForTasks.toArray(new UUID[0]),
                Instant.now(),
                getSetting()
        );
    }

    public static final class Builder<TResult> {
        private final TaskRecord<TResult> taskRecord;

        public Builder() {
            this.taskRecord = new TaskRecord<>();
        }

        @Nonnull
        public Builder<TResult> setId(@Nonnull final UUID id) {
            taskRecord.setId(id);
            return this;
        }

        @Nonnull
        public Builder<TResult> setName(@Nonnull final String name) {
            Preconditions.checkArgument(StringUtils.isNotBlank(name));
            taskRecord.setName(name);
            return this;
        }

        @Nonnull
        public Builder<TResult> setTube(@Nonnull final String tube) {
            Preconditions.checkArgument(StringUtils.isNotBlank(tube));
            taskRecord.setTube(tube);
            return this;
        }

        @Nonnull
        public Builder<TResult> setParent(@Nonnull final UUID parentId) {
            taskRecord.setParentId(Objects.requireNonNull(parentId));
            return this;
        }

        @Nonnull
        public Builder<TResult> setArgument(@Nonnull final Value<?> arg) {
            taskRecord.addArgument(Objects.requireNonNull(arg));
            return this;
        }

        @Nonnull
        public Builder<TResult> setCorrelationId(@Nonnull final String correlationId) {
            Preconditions.checkArgument(StringUtils.isNotBlank(correlationId));
            taskRecord.setCorrelationId(correlationId);
            return this;
        }

        @Nonnull
        public TaskRecord<TResult> build() {
            return taskRecord;
        }

    }

}
