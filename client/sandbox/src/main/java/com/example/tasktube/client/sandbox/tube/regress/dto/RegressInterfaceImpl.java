package com.example.tasktube.client.sandbox.tube.regress.dto;

public class RegressInterfaceImpl implements IRegressInterface {

    private String value;

    public RegressInterfaceImpl() {
    }

    @Override
    public String getValue() {
        return value;
    }

    public RegressInterfaceImpl setValue(final String value) {
        this.value = value;
        return this;
    }
}
