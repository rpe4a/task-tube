package com.example.tasktube.client.sdk.task;

import com.google.common.base.Preconditions;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class TaskRecord<TResult> {

    private final List<Value<?>> args = new LinkedList<>();
    private final List<UUID> waitForTasks = new LinkedList<>();
    private final TaskSetting setting = TaskSetting.DEFAULT();
    private UUID id;
    private UUID parentId;
    private String name;
    private String tube;

    private TaskRecord() {
        setId(UUID.randomUUID());
    }

    private TaskRecord(final UUID id) {
        this.id = id;
    }

    public TaskSetting getSetting() {
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

    void setArg(final Value<?> value) {
        args.add(value);
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

    public void applyConfiguration(final TaskConfiguration configuration) {
        Preconditions.checkNotNull(configuration);
        if (TaskConfiguration.WaitForTask.class.isAssignableFrom(configuration.getClass())) {
            final TaskConfiguration.WaitForTask waitForTask = (TaskConfiguration.WaitForTask) configuration;
            waitForTasks.add(waitForTask.getId());
        } else if (TaskConfiguration.MaxCountOfFailures.class.isAssignableFrom(configuration.getClass())) {
            final TaskConfiguration.MaxCountOfFailures maxCountOfFailures = (TaskConfiguration.MaxCountOfFailures) configuration;
            setting.setMaxFailures(maxCountOfFailures.getValue());
        } else if (TaskConfiguration.FailureRetryTimeoutSeconds.class.isAssignableFrom(configuration.getClass())) {
            final TaskConfiguration.FailureRetryTimeoutSeconds failureRetryTimeoutSeconds = (TaskConfiguration.FailureRetryTimeoutSeconds) configuration;
            setting.setFailureRetryTimeoutSeconds(failureRetryTimeoutSeconds.getValue());
        } else {
            throw new IllegalArgumentException("Unsupported configuration type: " + configuration.getClass());
        }
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

        public <A0> Builder<TResult> setArg(final Constant<A0> arg0) {
            taskRecord.setArg(arg0);
            return this;
        }

        public TaskRecord<TResult> build() {
            return taskRecord;
        }
    }

}
