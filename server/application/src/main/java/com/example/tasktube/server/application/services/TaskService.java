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
    public void scheduleTask(final UUID taskId, final Instant scheduledAt, final String client) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(scheduledAt);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));
        LOGGER.info("Schedule task id: '{}'.", taskId);

        final Task task = taskRepository.get(taskId).orElseThrow();
        if (task.hasStartBarrier()) {
            LOGGER.debug("Start barrier id: '{}'.", task.getStartBarrier());

            final Barrier startBarrier = barrierRepository.get(task.getStartBarrier()).orElseThrow();
            if (startBarrier.isNotReleased()) {
                LOGGER.debug("Start barrier is not released.");

                task.unlock();
            } else {
                LOGGER.debug("Start barrier is released.");

                final List<Task> waitingTasks = taskRepository.get(startBarrier.getWaitFor());
                if (waitingTasks.stream().anyMatch(Task::isAborted)) {
                    LOGGER.debug("Task is canceled.");

                    task.cancel(scheduledAt, client);
                }
                if (waitingTasks.stream().allMatch(Task::isFinalized)) {
                    LOGGER.debug("Task is scheduled.");

                    task.schedule(scheduledAt, client);
                }
            }
        } else {
            LOGGER.debug("Start barrier is empty. Task is scheduled.");

            task.schedule(scheduledAt, client);
        }

        taskRepository.schedule(task);
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
    public void completeTask(final UUID taskId, final Instant completedAt, final String client) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(completedAt);
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

                    task.abort(completedAt, client);
                }
                if (childrenTasks.stream().allMatch(Task::isFinalized)) {
                    LOGGER.debug("Task is completed.");

                    task.complete(completedAt, client);
                }
            }
        } else {
            LOGGER.debug("Finish barrier is empty. Task is completed.");

            task.complete(completedAt, client);
        }

        taskRepository.complete(task);
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
