package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;

import java.util.List;
import java.util.UUID;

public interface IJobRepository {
    List<Barrier> lockBarrierList(Barrier.Status status, int count, String client);

    List<UUID> getLockedBarrierIdList(int count, int lockedTimeoutSeconds);

    List<UUID> lockTaskIdList(Task.Status status, int count, String client);

    List<UUID> getLockedTaskIdList(int count, int lockedTimeoutSeconds);

}
