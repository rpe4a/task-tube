package com.example.tasktube.client.sandbox.workflow;

import com.example.tasktube.client.sdk.task.Task1;
import com.example.tasktube.client.sdk.task.TaskResult;
import com.example.tasktube.client.sdk.task.Value;

public class ExampleMainTask extends Task1<String, String> {

    @Override
    public Value<String> run(final String arg0) {
        final TaskResult<Integer> childTaskResult0 = pushIn(new ExampleChildTask0());

        final TaskResult<Integer> childTaskResult1 =
                pushIn(new ExampleChildTask1(), constant("Hello World"),
                        waitFor(childTaskResult0),
                        configure().maxCountOfFailures(5),
                        configure().failureRetryTimeoutSeconds(120),
                        configure().timeoutSeconds(60 * 30)
                );

        final TaskResult<Integer> childTaskResult2 = pushIn(new ExampleChildTask2(), childTaskResult1, childTaskResult0);

        final TaskResult<Integer> childTaskResult3 = pushIn(new ExampleChildTask1List(), list(childTaskResult0, childTaskResult1, childTaskResult2));

        final TaskResult<Integer> childTaskResult4 = pushIn(new ExampleChildTask1List(), list(constant(1), constant(2), childTaskResult2));

        return nothing();
    }
}
