package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.TaskDto;

import java.util.Optional;
import java.util.UUID;

public interface ITubeService {
    UUID push(TaskDto task);

    Optional<PopTaskDto> pop(String tube, final String client);
}
