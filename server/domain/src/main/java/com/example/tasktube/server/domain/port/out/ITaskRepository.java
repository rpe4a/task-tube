package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITaskRepository {
    Optional<Task> get(UUID id);

    List<Task> get(List<UUID> taskIdList);

    List<Task> getTasksForScheduling(String client, int count);

    void schedule(Task task);

    void start(Task task);

    void process(Task task);

    void finish(Task task);

    void fail(Task task);

    void complete(Task task);
}
