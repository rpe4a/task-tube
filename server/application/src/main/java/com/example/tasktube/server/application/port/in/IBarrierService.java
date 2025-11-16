package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.application.models.SchedulingDto;
import com.example.tasktube.server.domain.enties.Task;

import java.util.List;
import java.util.UUID;

public interface IBarrierService {
    void releaseBarrier(UUID barrierId, String client);
}
