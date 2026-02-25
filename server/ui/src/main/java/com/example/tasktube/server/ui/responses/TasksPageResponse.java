package com.example.tasktube.server.ui.responses;

public record TasksPageResponse(
        TasksPageDto[] tasks,
        long totalCount
) {
}
