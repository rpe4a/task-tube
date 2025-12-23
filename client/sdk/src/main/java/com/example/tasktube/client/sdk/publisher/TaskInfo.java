package com.example.tasktube.client.sdk.publisher;

import com.example.tasktube.client.sdk.dto.TaskRequest;
import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.slot.SlotValueSerializer;
import com.example.tasktube.client.sdk.task.TaskSettings;
import com.example.tasktube.client.sdk.task.Value;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TaskInfo {
    private final List<Slot> slots = new LinkedList<>();
    private TaskSettings setting = TaskSettings.DEFAULT();
    private UUID id;
    private String name;
    private String tube;

    TaskInfo() {
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

    public String getTube() {
        return tube;
    }

    void setTube(final String tube) {
        this.tube = tube;
    }

    public TaskSettings getSetting() {
        return setting;
    }

    void setSetting(final TaskSettings setting) {
        this.setting = setting;
    }

    void addSlot(final Slot slot) {
        slots.add(slot);
    }

    List<Slot> getSlots() {
        return List.copyOf(slots);
    }

    public static final class Builder {
        private final TaskInfo taskInfo = new TaskInfo();
        private final SlotValueSerializer slotValueSerializer;

        public Builder(final SlotValueSerializer slotValueSerializer) {
            this.slotValueSerializer = Objects.requireNonNull(slotValueSerializer);
        }

        public TaskInfo.Builder setId(final UUID id) {
            taskInfo.setId(id);
            return this;
        }

        public TaskInfo.Builder setName(final String name) {
            taskInfo.setName(name);
            return this;
        }

        public TaskInfo.Builder setSettings(final TaskSettings setting) {
            taskInfo.setSetting(setting);
            return this;
        }

        public TaskInfo.Builder setTube(final String tube) {
            taskInfo.setTube(tube);
            return this;
        }

        public Builder setSlot(final Value<?> value) {
            taskInfo.addSlot(slotValueSerializer.map(value));
            return this;
        }

        public TaskInfo build() {
            return taskInfo;
        }

        public TaskRequest getRequest() {
            return new TaskRequest(
                    taskInfo.getId(),
                    taskInfo.getName(),
                    taskInfo.getTube(),
                    taskInfo.getSlots()
                            .toArray(new Slot[0]),
                    null,
                    Instant.now(),
                    taskInfo.getSetting()
            );
        }
    }
}
