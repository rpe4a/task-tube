package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.application.port.in.ITaskTubeService;
import com.example.tasktube.server.domain.enties.LogRecord;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.enties.TaskTube;
import com.example.tasktube.server.domain.events.logs.LogRecordsAddedEvent;
import com.example.tasktube.server.domain.port.out.IEventPublisher;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.port.out.ITaskTubeRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskTubeService implements ITaskTubeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTubeService.class);

    private final ITaskRepository taskRepository;
    private final ITaskTubeRepository taskTubeRepository;
    private final IEventPublisher eventPublisher;

    public TaskTubeService(
            final ITaskRepository taskRepository,
            final ITaskTubeRepository taskTubeRepository,
            final IEventPublisher eventPublisher
    ) {
        this.taskRepository = Objects.requireNonNull(taskRepository);
        this.taskTubeRepository = Objects.requireNonNull(taskTubeRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    @Transactional
    public void requestTermination(final String correlationId, final UUID taskId) {
        if (StringUtils.isEmpty(correlationId)) {
            throw new ApplicationException("Parameter correlationId cannot be null or empty.");
        }
        if (Objects.isNull(taskId)) {
            throw new ApplicationException("Parameter taskId name cannot be null.");
        }
        LOGGER.info("Termination is requested for taskId: '{}' and correlationId: '{}'.", taskId, correlationId);

        final Optional<TaskTube> taskTubeOpt = taskTubeRepository.find(correlationId, taskId);

        if (taskTubeOpt.isPresent()) {
            final TaskTube taskTube = taskTubeOpt.get();

            taskTube.terminateRequested();

            taskTubeRepository.update(taskTube);
        } else {
            final TaskTube taskTube = new TaskTube(correlationId, taskId);

            taskTube.terminateRequested();

            taskTubeRepository.create(taskTube);
        }
    }

    @Transactional
    @Override
    public void terminate(final UUID id, final String client) {
        if (StringUtils.isEmpty(client)) {
            throw new ApplicationException("Parameter client name cannot be null or empty.");
        }
        if (Objects.isNull(id)) {
            throw new ApplicationException("Parameter id cannot be null.");
        }
        LOGGER.info("Start terminate tasktubes with id '{}' by client '{}'.", id, client);

        final TaskTube taskTube = taskTubeRepository.get(id).get();

        LOGGER.info("Terminate tasks with correlation id '{}' by client '{}'.", taskTube.getCorrelationId(), client);
        final List<UUID> tasks = taskTubeRepository.terminate(taskTube, client);

        LOGGER.info("'{}' tasks have been terminated by client '{}'.", tasks.size(), client);
        if (tasks.isEmpty()) {
            final Task headTask = taskRepository.get(taskTube.getTaskId()).get();

            if (headTask.isTerminated()) {
                LOGGER.info("TaskTube with id: '{}' has been terminated.", taskTube.getId());
                taskTube.terminated();
            }
        } else {
            final LogRecordsAddedEvent logTerminationEvents = new LogRecordsAddedEvent(
                    tasks.stream()
                            .map(tId -> LogRecord.info(tId, "Task has been terminated."))
                            .toList()
            );

            eventPublisher.publish(logTerminationEvents);
        }

        LOGGER.info("TaskTube with id: '{}' has been unlocked.", taskTube.getId());
        taskTube.unlock();

        taskTubeRepository.update(taskTube);
    }
}
