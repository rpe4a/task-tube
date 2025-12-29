package com.example.tasktube.client.sdk.core.publisher;

import com.example.tasktube.client.sdk.core.http.TaskTubeClient;
import com.example.tasktube.client.sdk.core.task.slot.SlotValueSerializer;
import com.example.tasktube.client.sdk.core.task.TaskConfiguration;
import com.example.tasktube.client.sdk.core.task.TaskRecord;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class TaskTubePublisher {
    private final TaskTubeClient client;
    private final SlotValueSerializer slotSerializer;
    private final TaskRecord.Builder<?> builder;
    private final TaskConfiguration[] configurations;

    TaskTubePublisher(
            @Nonnull final TaskTubeClient client,
            @Nonnull final SlotValueSerializer slotSerializer,
            @Nonnull final TaskRecord.Builder<?> builder,
            @Nonnull final TaskConfiguration[] configurations
    ) {
        this.client = Objects.requireNonNull(client);
        this.slotSerializer = Objects.requireNonNull(slotSerializer);
        this.builder = Objects.requireNonNull(builder);
        this.configurations = Objects.requireNonNull(configurations);
    }

    @Nonnull
    public Optional<UUID> pushIn(@Nonnull final String tube) {
        Preconditions.checkArgument(StringUtils.isNotBlank(tube));

        builder.setTube(tube);

        final TaskRecord<?> record = builder.build();

        record.configure(configurations);

        return client.pushTask(tube, record.toRequest(slotSerializer));
    }
}
