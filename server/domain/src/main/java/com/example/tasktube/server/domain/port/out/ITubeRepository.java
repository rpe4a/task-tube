package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.Task;

import java.util.Optional;

public interface ITubeRepository {
    Task push(Task task);

    Optional<Task> pop(String tube, String lockedBy);
}
