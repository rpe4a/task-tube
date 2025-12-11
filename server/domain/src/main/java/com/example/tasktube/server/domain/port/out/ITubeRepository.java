package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.Task;

import java.util.List;
import java.util.Optional;

public interface ITubeRepository {
    Task push(Task task);

    void push(List<Task> tasks);

    Optional<Task> pop(String tube, String client);
}
