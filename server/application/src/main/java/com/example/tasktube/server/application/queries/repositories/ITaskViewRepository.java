package com.example.tasktube.server.application.queries.repositories;

import com.example.tasktube.server.application.queries.views.ParentTaskView;
import com.example.tasktube.server.domain.enties.Task;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ITaskViewRepository {

    List<ParentTaskView> getParentTaskList(
            @Nullable UUID taskId,
            @Nullable String taskName,
            @Nullable String tube,
            @Nullable Task.Status status,
            @Nullable Instant createdFrom,
            @Nullable Instant createdTo,
            int page,
            int size
    );
}
