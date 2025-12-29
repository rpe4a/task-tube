package com.example.tasktube.client.sandbox.tube;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;

public class ChildTaskReturnInteger extends Task0<Integer> {

    @Override
    public Value<Integer> run() throws Exception {
        return constant(0);
    }
}
