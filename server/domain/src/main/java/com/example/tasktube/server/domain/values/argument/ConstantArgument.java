package com.example.tasktube.server.domain.values.argument;

public final class ConstantArgument extends Argument {
    private Object value;
    private String valueReferenceType;
    public ConstantArgument() {
        super(ArgumentType.CONSTANT);
    }

    public Object getValue() {
        return value;
    }

    public ConstantArgument setValue(final Object value) {
        this.value = value;
        return this;
    }

    public String getValueReferenceType() {
        return valueReferenceType;
    }

    public ConstantArgument setValueReferenceType(final String valueTypeReference) {
        this.valueReferenceType = valueTypeReference;
        return this;
    }
}
