package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.application.port.in.ITaskTubeService;
import com.example.tasktube.server.domain.enties.TaskTube;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskTubeService implements ITaskTubeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTubeService.class);

    private final ITaskTubeRepository taskTubeRepository;

    public TaskTubeService(
            final ITaskTubeRepository taskTubeRepository
    ) {
        this.taskTubeRepository = Objects.requireNonNull(taskTubeRepository);
    }

    @Override
    public void requestTermination(final String correlationId, final UUID taskId) {
        if (StringUtils.isEmpty(correlationId)) {
            throw new ApplicationException("Parameter correlationId name cannot be null or empty.");
        }
        if (Objects.isNull(taskId)) {
            throw new ApplicationException("Parameter taskId name cannot be null.");
        }
        LOGGER.info("Termination is requested for correlationId: '{}' and taskId: '{}'.", correlationId, taskId);

        final Optional<TaskTube> taskTube = taskTubeRepository.get(correlationId, taskId);

        if (taskTube.isEmpty()) {
            taskTubeRepository.insert(new TaskTube(correlationId, taskId, true, false));
        } else {
            taskTubeRepository.update(taskTube.get().setTerminationRequested(true));
        }
    }
}
