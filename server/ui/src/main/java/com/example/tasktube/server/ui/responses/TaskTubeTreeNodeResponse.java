package com.example.tasktube.server.ui.responses;

import java.time.Instant;
import java.util.UUID;

public record TaskTubeTreeNodeResponse(
        TaskTubeTreeNode root,
        TaskTubeTreeNode[] children
) {
}
