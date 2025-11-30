package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.Task;

import java.util.List;
import java.util.UUID;

public interface IJobRepository {
    List<UUID> lockBarrierIdList(int count, String client);

    List<UUID> lockTaskIdList(Task.Status status, int count, String client);
}
