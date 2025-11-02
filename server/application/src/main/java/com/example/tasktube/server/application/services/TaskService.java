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
        LOGGER.debug("Get task by id: '{}'.", taskId);

        return taskRepository.getById(taskId);
    }

    @Override
    @Transactional
    public void startTask(final UUID taskId, final String client, final Instant startedAt) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(startedAt);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));
        LOGGER.debug("Start task id: '{}'.", taskId);

        final Task task = taskRepository.getById(taskId).orElseThrow();

        task.start(client, startedAt);

        taskRepository.start(task);
    }

    @Override
    @Transactional
    public void processTask(final UUID taskId, final String client, final Instant processedAt) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(processedAt);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));
        LOGGER.debug("Process task id: '{}'.", taskId);

        final Task task = taskRepository.getById(taskId).orElseThrow();

        task.process(client, processedAt);

        taskRepository.process(task);
    }

    @Override
    @Transactional
    public void finishTask(final FinishTaskDto taskDto) {
        Preconditions.checkNotNull(taskDto);
        LOGGER.debug("Finish task id: '{}'.", taskDto.taskId());

        final Task task = taskRepository.getById(taskDto.taskId()).orElseThrow();

        final List<Task> children = new ArrayList<>();
        final List<Barrier> barriers = new ArrayList<>();
        if (taskDto.children() != null && !taskDto.children().isEmpty()) {
            taskDto.children().forEach(childDto -> {
                final Task child = childDto.to(false);

                child.attachParent(task);

                if (childDto.waitTasks() != null && !childDto.waitTasks().isEmpty()) {
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

        task.finish(taskDto.client(), taskDto.finishedAt(), taskDto.output());

        taskRepository.finish(task);
    }
}
