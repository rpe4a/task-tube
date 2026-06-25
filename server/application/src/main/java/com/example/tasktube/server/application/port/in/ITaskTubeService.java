package com.example.tasktube.server.application.port.in;

import java.util.UUID;

public interface ITaskTubeService {

    void requestTermination(final String correlationId, final UUID taskId);

    void terminate(final UUID id, final String client);
}
