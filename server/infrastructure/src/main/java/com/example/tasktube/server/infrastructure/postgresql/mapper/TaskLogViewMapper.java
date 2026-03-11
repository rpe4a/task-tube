package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.application.queries.views.ParentTaskView;
import com.example.tasktube.server.application.queries.views.TaskLogView;
import com.example.tasktube.server.domain.enties.Task;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

@Service
public class TaskLogViewMapper {

    public TaskLogView getTaskLogView(final ResultSet rs) throws SQLException {
        final TaskLogView taskView = new TaskLogView();
        taskView.setId(rs.getObject("id", UUID.class));
        taskView.setTaskId(rs.getObject("task_id", UUID.class));
        taskView.setType(rs.getString("type"));
        taskView.setLevel(rs.getString("level"));
        taskView.setTimestamp(Instant.ofEpochMilli(rs.getTimestamp("timestamp").getTime()));
        taskView.setMessage(rs.getString("message"));
        taskView.setExceptionMessage(rs.getString("exceptionMessage"));
        taskView.setExceptionStackTrace(rs.getString("exceptionStackTrace"));
        taskView.setTotalCount(rs.getInt("total_count"));
        return taskView;
    }
}
