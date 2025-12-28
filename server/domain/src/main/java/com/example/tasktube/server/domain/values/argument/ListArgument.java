package com.example.tasktube.server.domain.values.argument;

import java.util.LinkedList;
import java.util.List;

public final class ListArgument extends Argument {
    public List<Argument> values = new LinkedList<>();

    public ListArgument() {
        super(ArgumentType.LIST);
    }

    public void add(final Argument argument) {
        values.add(argument);
    }

    public List<Argument> getValues() {
        return values;
    }

    public ListArgument setValues(final List<Argument> arguments) {
        values = arguments;
        return this;
    }
}
