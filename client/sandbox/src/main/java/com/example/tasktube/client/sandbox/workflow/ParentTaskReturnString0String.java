package com.example.tasktube.client.sandbox.workflow;

import com.example.tasktube.client.sdk.task.Task1;
import com.example.tasktube.client.sdk.task.TaskResult;
import com.example.tasktube.client.sdk.task.Value;

public class ParentTaskReturnString0String extends Task1<String, String> {

    @Override
    public Value<String> run(final String arg0) {
        final TaskResult<Integer> childTaskResult0 = pushIn(new ChildTaskReturnInteger());

        final TaskResult<Integer> childTaskResult1 =
                pushIn(new ChildTaskReturnInteger0String(), constant("Hello World"),
                        waitFor(childTaskResult0),
                        configure().maxCountOfFailures(5),
                        configure().failureRetryTimeoutSeconds(120),
                        configure().timeoutSeconds(60 * 30)
                );

        final TaskResult<Integer> childTaskResult2 = pushIn(new ChildTaskReturnInteger0Integer1Integer(), childTaskResult1, childTaskResult0);

        final TaskResult<Integer> childTaskResult3 = pushIn(new ChildTaskReturnInteger0ListOfInteger(), list(childTaskResult0, childTaskResult1, childTaskResult2));

        final TaskResult<Integer> childTaskResult4 = pushIn(new ChildTaskReturnInteger0ListOfInteger(), list(constant(1), constant(2), childTaskResult2));

        return nothing();
    }
}
