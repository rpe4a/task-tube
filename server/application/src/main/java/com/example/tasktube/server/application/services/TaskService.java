package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.application.utils.SlotUtils;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TaskService implements ITaskService {
    public static final String CHILDREN_ARE_FINALIZED = "Some children are finalized.";
    public static final String WAITING_TASKS_ARE_FINALIZED = "Some waiting tasks are finalized.";

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
    public void scheduleTask(final UUID taskId, final Instant scheduledAt, final String client) {
        if (Objects.isNull(taskId)) {
            throw new ApplicationException("Parameter taskId cannot be null.");
        }
        if (Objects.isNull(scheduledAt)) {
            throw new ApplicationException("Parameter scheduledAt cannot be null.");
        }
        if (Strings.isNullOrEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }
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
                if (waitingTasks.stream().anyMatch(Task::isFinalized)) {
                    LOGGER.debug("Task is canceled.");

                    task.cancel(scheduledAt, WAITING_TASKS_ARE_FINALIZED, client);
                }
                if (waitingTasks.stream().allMatch(Task::isCompleted)) {
                    LOGGER.debug("Task is scheduled.");

                    task.schedule(scheduledAt, client);
                }
            }
        } else {
            LOGGER.debug("Start barrier is empty. Task is scheduled.");

            task.schedule(scheduledAt, client);
        }

        taskRepository.update(task);
    }

    @Override
    @Transactional
    public void startTask(final UUID taskId, final Instant startedAt, final String client) {
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
    }

    @Override
    @Transactional
    public void finishTask(final FinishTaskDto taskDto) {
        if (Objects.isNull(taskDto)) {
            throw new ApplicationException("Parameter taskDto cannot be null.");
        }
        LOGGER.info("Finish task id: '{}'.", taskDto.taskId());

        final Task task = taskRepository.get(taskDto.taskId()).orElseThrow();

        final List<Task> children = new ArrayList<>();
        final List<Barrier> barriers = new ArrayList<>();
        if (taskDto.children() != null && !taskDto.children().isEmpty()) {
            LOGGER.debug("Task has '{}' children.", taskDto.children().size());

            taskDto.children().forEach(childDto -> {
                final Task child = childDto.to(false);
                final List<UUID> waitingTaskIdList = new ArrayList<>();
                if (childDto.waitTasks() != null && !childDto.waitTasks().isEmpty()) {
                    waitingTaskIdList.addAll(childDto.waitTasks());
                }
                if (childDto.input() != null && !childDto.input().isEmpty()) {
                    final List<UUID> taskSlots = SlotUtils.getTaskIdList(childDto.input());
                    waitingTaskIdList.addAll(taskSlots);
                }

                child.attachParent(task);

                if (!waitingTaskIdList.isEmpty()) {
                    barriers.add(child.addStartBarrier(waitingTaskIdList));
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

        taskRepository.update(task);
    }

    @Override
    @Transactional
    public void completeTask(final UUID taskId, final Instant completedAt, final String client) {
        if (Objects.isNull(taskId)) {
            throw new ApplicationException("Parameter taskId cannot be null.");
        }
        if (Objects.isNull(completedAt)) {
            throw new ApplicationException("Parameter completedAt cannot be null.");
        }
        if (Strings.isNullOrEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }
        LOGGER.info("Complete task id: '{}'.", taskId);

        final Task task = taskRepository.get(taskId).orElseThrow();
        if (task.hasFinishBarrier()) {
            LOGGER.debug("Finish barrier id: '{}'.", task.getFinishBarrier());

            final Barrier finishBarrier = barrierRepository.get(task.getFinishBarrier()).orElseThrow();
            if (finishBarrier.isNotReleased()) {
                LOGGER.debug("Finish barrier is not released.");

                task.unlock();
            } else {
                LOGGER.debug("Finish barrier is released.");

                final List<Task> childrenTasks = taskRepository.get(finishBarrier.getWaitFor());
                if (childrenTasks.stream().anyMatch(Task::isFinalized)) {
                    LOGGER.debug("Task is aborted.");

                    task.abort(completedAt, CHILDREN_ARE_FINALIZED, client);
                }
                if (childrenTasks.stream().allMatch(Task::isCompleted)) {
                    LOGGER.debug("Task is completed.");

                    task.complete(completedAt, client);
                }
            }
        } else {
            LOGGER.debug("Finish barrier is empty. Task is completed.");

            task.complete(completedAt, client);
        }

        taskRepository.update(task);
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
    }
}
