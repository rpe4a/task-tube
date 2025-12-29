package com.example.tasktube.client.sandbox.tube;

import com.example.tasktube.client.sdk.core.task.Task2;
import com.example.tasktube.client.sdk.core.task.Value;

public class ChildTaskReturnInteger0Integer1Integer extends Task2<Integer, Integer, Integer> {

    @Override
    public Value<Integer> run(final Integer arg0, final Integer arg1) throws Exception {
        return constant(arg0 + arg1);
    }
}
