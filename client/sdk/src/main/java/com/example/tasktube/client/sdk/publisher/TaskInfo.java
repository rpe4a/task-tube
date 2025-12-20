package com.example.tasktube.client.sdk.publisher;

import com.example.tasktube.client.sdk.dto.TaskRequest;
import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.slot.SlotValueMapper;
import com.example.tasktube.client.sdk.task.TaskSetting;
import com.example.tasktube.client.sdk.task.Value;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TaskInfo {
    private final List<Slot> slots = new LinkedList<>();
    private TaskSetting setting = TaskSetting.DEFAULT();
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

    public TaskSetting getSetting() {
        return setting;
    }

    void setSetting(final TaskSetting setting) {
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
        private final SlotValueMapper slotValueMapper;

        public Builder(final SlotValueMapper slotValueMapper) {
            this.slotValueMapper = Objects.requireNonNull(slotValueMapper);
        }

        public TaskInfo.Builder setId(final UUID id) {
            taskInfo.setId(id);
            return this;
        }

        public TaskInfo.Builder setName(final String name) {
            taskInfo.setName(name);
            return this;
        }

        public TaskInfo.Builder setSettings(final TaskSetting setting) {
            taskInfo.setSetting(setting);
            return this;
        }

        public TaskInfo.Builder setTube(final String tube) {
            taskInfo.setTube(tube);
            return this;
        }

        public Builder setSlot(final Value<?> value) {
            taskInfo.addSlot(slotValueMapper.map(value));
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
