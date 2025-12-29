package com.example.tasktube.sandboxspring.tube.test;

import com.example.tasktube.client.sdk.core.task.Task1;
import com.example.tasktube.client.sdk.core.task.Value;

public class ChildTaskReturnInteger0String extends Task1<Integer, String> {
    @Override
    public Value<Integer> run(final String arg0) throws Exception {
        return constant(arg0.length());
    }
}
