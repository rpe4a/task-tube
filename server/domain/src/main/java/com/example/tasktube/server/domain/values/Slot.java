package com.example.tasktube.server.domain.values;

import java.util.Map;
import java.util.UUID;

public class Slot {
    private Map<String, Object> metadata;

    private SlotType type;

    private Object value;

    private String valueReferenceType;

    private UUID taskReference;

    private String taskReferenceType;

    private Boolean isFill;

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Slot setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public SlotType getType() {
        return type;
    }

    public Slot setType(final SlotType type) {
        this.type = type;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public Slot setValue(final Object value) {
        this.value = value;
        return this;
    }

    public String getValueReferenceType() {
        return valueReferenceType;
    }

    public Slot setValueReferenceType(final String valueTypeReference) {
        this.valueReferenceType = valueTypeReference;
        return this;
    }

    public Boolean getFill() {
        return isFill;
    }

    public Slot setFill(final Boolean fill) {
        isFill = fill;
        return this;
    }

    public UUID getTaskReference() {
        return taskReference;
    }

    public Slot setTaskReference(final UUID taskId) {
        this.taskReference = taskId;
        return this;
    }

    public String getTaskReferenceType() {
        return taskReferenceType;
    }

    public Slot setTaskReferenceType(final String taskTypeReference) {
        this.taskReferenceType = taskTypeReference;
        return this;
    }

    public boolean isNothingSlot() {
        return SlotType.NOTHING.equals(type);
    }

    public boolean isConstantSlot() {
        return SlotType.CONSTANT.equals(type);
    }

    public boolean isTaskSlot() {
        return SlotType.TASK.equals(type);
    }

    public boolean isListSlot() {
        return SlotType.LIST.equals(type);
    }

    public enum SlotType {
        NOTHING,
        CONSTANT,
        TASK,
        LIST
    }
}
