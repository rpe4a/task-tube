package com.example.tasktube.client.sandbox.tube;

import com.example.tasktube.client.sdk.task.Task1;
import com.example.tasktube.client.sdk.task.Value;

public class ChildTaskReturnInteger0String extends Task1<Integer, String> {
    @Override
    public Value<Integer> run(final String arg0) throws Exception {
        return constant(arg0.length());
    }
}
