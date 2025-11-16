package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJobRepository {
    List<UUID> getBarrierIdList(int count, String client);

    List<UUID> getTaskIdList(Task.Status status, int count, String client);
}
