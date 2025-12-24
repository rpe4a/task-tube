package com.example.tasktube.client.sandbox.workflow;

import com.example.tasktube.client.sdk.task.Task1;
import com.example.tasktube.client.sdk.task.Value;

import java.util.List;

public class ChildTaskReturnInteger0ListOfInteger extends Task1<Integer, List<Integer>> {

    @Override
    public Value<Integer> run(final List<Integer> arg0) throws Exception {
        return constant(arg0.size());
    }
}
