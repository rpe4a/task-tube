package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.application.utils.SlotUtils;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import com.example.tasktube.server.domain.values.argument.Argument;
import com.example.tasktube.server.domain.values.slot.Slot;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
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
    private final IArgumentFiller argumentFiller;

    public TaskService(
            final ITubeRepository tubeRepository,
            final ITaskRepository taskRepository,
            final IBarrierRepository barrierRepository,
            final IArgumentFiller argumentFiller

    ) {
        this.tubeRepository = Objects.requireNonNull(tubeRepository);
        this.taskRepository = Objects.requireNonNull(taskRepository);
        this.barrierRepository = Objects.requireNonNull(barrierRepository);
        this.argumentFiller = Objects.requireNonNull(argumentFiller);
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

        final List<Argument> arguments = new LinkedList<>();

        for (final Slot slot : task.getInput()) {
            arguments.add(slot.fill(argumentFiller));
        }

        task.start(startedAt, client);

        taskRepository.update(task);

        return arguments;
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

                child.attachToParent(task);

                barriers.add(child.addStartBarrier(waitingTaskIdList));
                children.add(child);
            });

            tubeRepository.push(children);
        }

        barriers.add(
                task.addFinishBarrier(
                        children.stream()
                                .map(Task::getId)
                                .toList()
                )
        );

        barrierRepository.save(barriers);

        task.finish(taskDto.finishedAt(), taskDto.output(), taskDto.client());

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
