package com.example.tasktube.server.application.queries.repositories;

import com.example.tasktube.server.application.queries.views.TaskLogView;

import java.util.List;
import java.util.UUID;

public interface ITaskLogViewRepository {

    List<TaskLogView> getTaskLogList(
            UUID taskId,
            int page,
            int size
    );
}
