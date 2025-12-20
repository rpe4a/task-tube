package com.example.tasktube.client.sdk.publisher;

import com.example.tasktube.client.sdk.TaskTubeClient;
import com.example.tasktube.client.sdk.task.TaskSetting;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public final class TaskTubePublisher {
    private final TaskTubeClient client;
    private final TaskInfo.Builder builder;

    TaskTubePublisher(final TaskTubeClient client, final TaskInfo.Builder builder) {
        this.client = Objects.requireNonNull(client);
        this.builder = Objects.requireNonNull(builder);
    }

    public TaskTubePublisher settings(final TaskSetting setting) {
        Preconditions.checkNotNull(setting);

        builder.setSettings(setting);

        return this;
    }

    public TaskInfo pushIn(final String tube) {
        Preconditions.checkArgument(StringUtils.isNotBlank(tube));
        builder.setTube(tube);

        client.pushTask(tube, builder.getRequest());

        return builder.build();
    }
}
