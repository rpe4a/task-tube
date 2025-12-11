package com.example.tasktube.server.application.port.in;

import java.util.UUID;

public interface IBarrierService {
    void releaseBarrier(UUID barrierId, String client);

    void unlockBarrier(UUID barrierId, int lockedTimeoutSeconds);
}
