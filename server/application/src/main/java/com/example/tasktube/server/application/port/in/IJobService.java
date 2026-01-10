package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.domain.enties.Barrier;

import java.util.List;
import java.util.UUID;

public interface IJobService {

    List<UUID> getLockedTaskIdList(int count, final int lockedTimeoutSeconds);

    List<UUID> getBarrierIdList(Barrier.Status status, int count, String client);

    List<UUID> getLockedBarrierIdList(int count, final int lockedTimeoutSeconds);

}
