package com.example.tasktube.server.application.port.in;

import java.util.UUID;

public interface ITaskTubeService {
    void requestTermination(String correlationId, UUID taskId);
}
