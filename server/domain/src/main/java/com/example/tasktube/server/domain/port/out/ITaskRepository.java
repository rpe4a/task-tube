package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITaskRepository {
    Optional<Task> getById(UUID id);

    List<Task> getTasksForScheduling(String client, int count);

    void schedule(List<Task> tasks);

    void start(Task task);

    void process(Task task);

    void finish(Task task);

}
