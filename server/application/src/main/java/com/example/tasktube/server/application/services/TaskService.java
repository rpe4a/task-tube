package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService implements ITaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final ITubeRepository tubeRepository;
    private final ITaskRepository taskRepository;
    private final IBarrierRepository barrierRepository;

    public TaskService(
            final ITubeRepository tubeRepository,
            final ITaskRepository taskRepository,
            final IBarrierRepository barrierRepository
    ) {
        this.tubeRepository = Objects.requireNonNull(tubeRepository);
        this.taskRepository = Objects.requireNonNull(taskRepository);
        this.barrierRepository = Objects.requireNonNull(barrierRepository);
    }

    @Override
    @Transactional
    public Optional<Task> getTaskById(final UUID taskId) {
        Preconditions.checkNotNull(taskId);
        LOGGER.info("Get task by id: '{}'.", taskId);

        return taskRepository.get(taskId);
    }

    @Override
    @Transactional
    public void startTask(final UUID taskId, final Instant startedAt, final String client) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(startedAt);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));
        LOGGER.info("Start task id: '{}'.", taskId);

        final Task task = taskRepository.get(taskId).orElseThrow();

        task.start(startedAt, client);

        taskRepository.start(task);
    }

    @Override
    @Transactional
    public void processTask(final UUID taskId, final Instant processedAt, final String client) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(processedAt);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));
        LOGGER.info("Process task id: '{}'.", taskId);

        final Task task = taskRepository.get(taskId).orElseThrow();

        task.process(processedAt, client);

        taskRepository.process(task);
    }

    @Override
    @Transactional
    public void finishTask(final FinishTaskDto taskDto) {
        Preconditions.checkNotNull(taskDto);
        LOGGER.info("Finish task id: '{}'.", taskDto.taskId());

        final Task task = taskRepository.get(taskDto.taskId()).orElseThrow();

        final List<Task> children = new ArrayList<>();
        final List<Barrier> barriers = new ArrayList<>();
        if (taskDto.children() != null && !taskDto.children().isEmpty()) {
            LOGGER.debug("Task has '{}' children.", taskDto.children().size());

            taskDto.children().forEach(childDto -> {
                final Task child = childDto.to(false);

                child.attachParent(task);

                if (childDto.waitTasks() != null && !childDto.waitTasks().isEmpty()) {
                    LOGGER.debug("Child task id '{}' has '{}' waiting tasks.", child.getId(), childDto.waitTasks().size());

                    barriers.add(child.addStartBarrier(childDto.waitTasks()));
                }

                children.add(child);
            });

            barriers.add(
                    task.addFinishBarrier(
                            children.stream()
                                    .map(Task::getId)
                                    .toList()
                    )
            );

            barrierRepository.save(barriers);
            tubeRepository.push(children);
        }

        task.finish(taskDto.finishedAt(), taskDto.output(), taskDto.client());

        taskRepository.finish(task);
    }

    @Override
    @Transactional
    public void finalizeTask(final UUID taskId, final Instant finalizedAt, final String client) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(finalizedAt);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));
        LOGGER.info("Finalize task id: '{}'.", taskId);

        final Task task = taskRepository.get(taskId).orElseThrow();
        if (task.hasFinishBarrier()) {
            LOGGER.debug("Finish barrier id: '{}'.", task.getFinishBarrier());

            final Barrier finishBarrier = barrierRepository.get(task.getFinishBarrier()).orElseThrow();
            if (finishBarrier.isNotReleased()) {
                LOGGER.debug("Finish barrier is not released.");
            } else {
                LOGGER.debug("Finish barrier is released.");

                final List<Task> childrenTasks = taskRepository.get(finishBarrier.getWaitFor());
                if (childrenTasks.stream().anyMatch(Task::isAborted)) {
                    LOGGER.debug("Task is aborted.");

                    task.abort(finalizedAt, client);
                }
                if (childrenTasks.stream().allMatch(Task::isFinalized)) {
                    LOGGER.debug("Task is finalized.");

                    task.complete(finalizedAt, client);
                }
            }
        } else {
            LOGGER.debug("Finish barrier is empty. Task is finalized.");

            task.complete(finalizedAt, client);
        }

        taskRepository.finalize(task);
    }

    @Override
    @Transactional
    public void failTask(final UUID taskId, final Instant failedAt, final String failedReason, final String client) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(failedAt);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(failedReason));
        LOGGER.info("Fail task id: '{}'.", taskId);

        final Task task = taskRepository.get(taskId).orElseThrow();

        task.fail(failedAt, failedReason, client);

        taskRepository.fail(task);
    }
}
