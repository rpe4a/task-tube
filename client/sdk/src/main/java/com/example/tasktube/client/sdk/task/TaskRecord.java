package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.dto.TaskRequest;
import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.slot.SlotValueSerializer;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class TaskRecord<TResult> {

    private final List<Value<?>> args = new LinkedList<>();
    private final List<UUID> waitForTasks = new LinkedList<>();
    private final TaskSettings setting = TaskSettings.DEFAULT();
    private UUID id;
    private UUID parentId;
    private String name;
    private String tube;
    private String correlationId;

    private TaskRecord() {
        setId(UUID.randomUUID());
    }

    private TaskRecord(final UUID id) {
        this.id = id;
    }

    public TaskSettings getSetting() {
        return setting;
    }

    public UUID getId() {
        return id;
    }

    void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    void setName(final String name) {
        this.name = name;
    }

    public TaskResult<TResult> getResult() {
        return new TaskResult<>(id);
    }

    void addArg(final Value<?> value) {
        args.add(value);
    }

    void waitFor(final UUID taskId) {
        waitForTasks.add(taskId);
    }


    public void configure(final TaskConfiguration[] configurations) {
        for (final TaskConfiguration configuration : configurations) {
            configuration.applyTo(this);
        }
    }

    public UUID getParentId() {
        return parentId;
    }

    void setParentId(final UUID parentId) {
        this.parentId = parentId;
    }

    public String getTube() {
        return tube;
    }

    void setTube(final String tube) {
        this.tube = tube;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    void setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
    }

    public TaskRequest toRequest(final SlotValueSerializer slotSerializer) {
        return new TaskRequest(
                getId(),
                getName(),
                getTube(),
                getCorrelationId(),
                args.stream()
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

        public Builder<TResult> setId(final UUID id) {
            taskRecord.setId(id);
            return this;
        }

        public Builder<TResult> setName(final String name) {
            taskRecord.setName(name);
            return this;
        }

        public Builder<TResult> setTube(final String tube) {
            taskRecord.setTube(tube);
            return this;
        }

        public Builder<TResult> setParent(final UUID parentId) {
            taskRecord.setParentId(parentId);
            return this;
        }

        public Builder<TResult> setArg(final Value<?> arg0) {
            taskRecord.addArg(arg0);
            return this;
        }

        public Builder<TResult> setCorrelationId(final String correlationId) {
            taskRecord.setCorrelationId(correlationId);
            return this;
        }

        public TaskRecord<TResult> build() {
            return taskRecord;
        }

    }

}
