package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.PushTaskDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITubeService {
    UUID push(PushTaskDto task);

    Optional<PopTaskDto> pop(String tube, String client);

    List<PopTaskDto> popList(String tube, String client, int count);
}
