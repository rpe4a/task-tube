package com.example.tasktube.server.ui.responses;

public record TaskTubeTaskLogsResponse(
        TaskTubeTaskLogDto[] logs,
        long totalCount
) {
}
