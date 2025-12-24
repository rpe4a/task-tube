package com.example.tasktube.client.sandbox.workflow;

import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;

public class ChildTaskReturnInteger extends Task0<Integer> {

    @Override
    public String getName() {
        return "test_name";
    }


    @Override
    public Value<Integer> run() throws Exception {
        return constant(0);
    }
}
