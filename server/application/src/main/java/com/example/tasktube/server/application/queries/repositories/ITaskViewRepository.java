package com.example.tasktube.server.application.queries.repositories;

import com.example.tasktube.server.application.queries.views.ParentTaskView;
import com.example.tasktube.server.application.queries.views.TaskTubeTreeNodeView;
import com.example.tasktube.server.application.queries.views.TaskTubeTaskView;
import com.example.tasktube.server.application.queries.views.TaskTubeView;
import com.example.tasktube.server.domain.enties.Task;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITaskViewRepository {

    List<ParentTaskView> getParentTaskList(
            @Nullable UUID taskId,
            @Nullable String taskName,
            @Nullable String tube,
            @Nullable Task.Status status,
            @Nullable Instant createdFrom,
            @Nullable Instant createdTo,
            @Nullable final String sort,
            @Nullable final String by,
            int page,
            int size
    );

    List<TaskTubeView> getTaskTube(@Nullable String correlationId);

    Optional<TaskTubeTaskView> getTaskTubeTask(@Nullable String correlationId, @Nullable UUID taskId);

    List<TaskTubeTreeNodeView> getTaskTubeTreeNode(@Nullable String correlationId, @Nullable UUID taskId);
}
