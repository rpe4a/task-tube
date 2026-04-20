package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.domain.enties.LogRecord;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.port.out.IEventPublisher;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.values.argument.Argument;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TaskService implements ITaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final ITaskRepository taskRepository;
    private final IArgumentFiller argumentFiller;
    private final IEventPublisher eventPublisher;

    public TaskService(
            final ITaskRepository taskRepository,
            final IArgumentFiller argumentFiller,
            final IEventPublisher eventPublisher

    ) {
        this.taskRepository = Objects.requireNonNull(taskRepository);
        this.argumentFiller = Objects.requireNonNull(argumentFiller);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    @Transactional
    public List<Argument> startTask(final UUID taskId, final Instant startedAt, final String client) {
        if (Objects.isNull(taskId)) {
            throw new ApplicationException("Parameter taskId cannot be null.");
        }
        if (Objects.isNull(startedAt)) {
            throw new ApplicationException("Parameter startedAt cannot be null.");
        }
        if (Strings.isNullOrEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }
        LOGGER.info("Start task id: '{}'.", taskId);

        final Task task = taskRepository.get(taskId).orElseThrow();

        task.start(startedAt, client);

        taskRepository.update(task);
        eventPublisher.publish(task.pullEvents());

        return task.getArguments(argumentFiller);
    }

    @Override
    @Transactional
    public void processTask(final UUID taskId, final Instant processedAt, final String client) {
        if (Objects.isNull(taskId)) {
            throw new ApplicationException("Parameter taskId cannot be null.");
        }
        if (Objects.isNull(processedAt)) {
            throw new ApplicationException("Parameter processedAt cannot be null.");
        }
        if (Strings.isNullOrEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }
        LOGGER.info("Process task id: '{}'.", taskId);

        final Task task = taskRepository.get(taskId).orElseThrow();

        task.process(processedAt, client);

        taskRepository.update(task);
        eventPublisher.publish(task.pullEvents());
    }

    @Override
    @Transactional
    public void finishTask(final FinishTaskDto taskDto) {
        if (Objects.isNull(taskDto)) {
            throw new ApplicationException("Parameter taskDto cannot be null.");
        }
        LOGGER.info("Finish task id: '{}'.", taskDto.taskId());

        final Task task = taskRepository.get(taskDto.taskId()).orElseThrow();

        final List<Task> children =
                taskDto.children() == null
                        ? Collections.emptyList()
                        : taskDto.children().stream().map(c ->
                                Task.pushNew(
                                        c.id(),
                                        c.name(),
                                        c.tube(),
                                        c.correlationId(),
                                        c.input(),
                                        c.createdAt(),
                                        c.getSettings(),
                                        c.getWaitingTaskIdList(),
                                        taskDto.client()
                                )
                        )
                        .toList();

        final List<LogRecord> logs = new LinkedList<>();
        if (!CollectionUtils.isEmpty(taskDto.logs())) {
            logs.addAll(taskDto.logs().stream().map(l -> l.to(task.getId())).toList());
        }

        task.finish(
                taskDto.finishedAt(),
                taskDto.output(),
                children,
                logs,
                taskDto.client()
        );

        taskRepository.update(task);
        eventPublisher.publish(task.pullEvents());
    }

    @Override
    @Transactional
    public void failTask(final UUID taskId, final Instant failedAt, final String failedReason, final String client) {
        if (Objects.isNull(taskId)) {
            throw new ApplicationException("Parameter taskId cannot be null.");
        }
        if (Objects.isNull(failedAt)) {
            throw new ApplicationException("Parameter failedAt cannot be null.");
        }
        if (Strings.isNullOrEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }
        if (Strings.isNullOrEmpty(failedReason)) {
            throw new ApplicationException("Parameter failedReason cannot be null or empty.");
        }
        LOGGER.info("Fail task id: '{}'.", taskId);

        final Task task = taskRepository.get(taskId).orElseThrow();

        task.fail(failedAt, failedReason, client);

        taskRepository.update(task);
        eventPublisher.publish(task.pullEvents());
    }

    @Override
    @Transactional
    public void unlockTask(final UUID taskId, final int lockedTimeoutSeconds) {
        if (Objects.isNull(taskId)) {
            throw new ApplicationException("Parameter taskId cannot be null.");
        }
        if (lockedTimeoutSeconds <= 0) {
            throw new ApplicationException("Parameter lockedTimeoutSeconds must be more than zero.");
        }
        LOGGER.warn("Locked task id: '{}'.", taskId);

        final Task task = taskRepository.get(taskId).orElseThrow();

        task.unblock(lockedTimeoutSeconds);

        taskRepository.update(task);
        eventPublisher.publish(task.pullEvents());
    }
}
