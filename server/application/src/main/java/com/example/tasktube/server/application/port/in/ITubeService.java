package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.application.models.TaskDto;

import java.util.Optional;
import java.util.UUID;

public interface ITubeService {
    UUID push(PushTaskDto task);

    Optional<TaskDto> pop(String tube, final String client);
}
