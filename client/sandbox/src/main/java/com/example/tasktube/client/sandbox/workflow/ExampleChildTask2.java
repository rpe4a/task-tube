package com.example.tasktube.client.sandbox.workflow;

import com.example.tasktube.client.sdk.task.Task2;
import com.example.tasktube.client.sdk.task.Value;

public class ExampleChildTask2 extends Task2<Integer, Integer, Integer> {

    @Override
    public Value<Integer> run(final Integer arg0, final Integer arg1) throws Exception {
        return constant(arg0 + arg1);
    }
}
