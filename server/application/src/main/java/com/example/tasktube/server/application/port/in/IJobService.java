package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.domain.enties.Task;

import java.util.List;
import java.util.UUID;

public interface IJobService {
    List<UUID> getTaskIdList(Task.Status status, int count, String client);

    List<UUID> getBarrierIdList(int count, String client);
}
