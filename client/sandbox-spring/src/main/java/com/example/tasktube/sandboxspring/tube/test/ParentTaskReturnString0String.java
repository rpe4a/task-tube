package com.example.tasktube.sandboxspring.tube.test;

import com.example.tasktube.client.sdk.core.IInstanceIdProvider;
import com.example.tasktube.client.sdk.core.http.ITaskTubeClient;
import com.example.tasktube.client.sdk.core.task.Task1;
import com.example.tasktube.client.sdk.core.task.TaskResult;
import com.example.tasktube.client.sdk.core.task.Value;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParentTaskReturnString0String extends Task1<String, String> {

    private ITaskTubeClient ITaskTubeClient;
    private ObjectMapper objectMapper;
    private IInstanceIdProvider provider;

    public void setITaskTubeClient(final ITaskTubeClient ITaskTubeClient) {
        this.ITaskTubeClient = ITaskTubeClient;
    }

    public void setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setIInstanceIdProvider(final IInstanceIdProvider provider) {
        this.provider = provider;
    }

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
