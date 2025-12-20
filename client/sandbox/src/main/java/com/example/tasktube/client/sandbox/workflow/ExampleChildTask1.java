package com.example.tasktube.client.sandbox.workflow;

import com.example.tasktube.client.sdk.task.Task1;
import com.example.tasktube.client.sdk.task.Value;

public class ExampleChildTask1 extends Task1<Integer, String> {

    @Override
    public Value<Integer> execute(final String arg0) throws Exception {
        return constant(arg0.length());
    }
}
