package com.example.tasktube.server.application.port.in;

import java.util.UUID;

public interface IBarrierService {
    void release(UUID barrierId, String client);

    void unlock(UUID barrierId, int lockedTimeoutSeconds);
}
