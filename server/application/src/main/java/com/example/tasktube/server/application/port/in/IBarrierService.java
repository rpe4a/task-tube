package com.example.tasktube.server.application.port.in;

import java.util.UUID;

public interface IBarrierService {
    void release(UUID barrierId, String client);

    void unblock(UUID barrierId, int lockedTimeoutSeconds);
}
