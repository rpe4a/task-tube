package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.application.port.in.IBarrierService;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BarrierService implements IBarrierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BarrierService.class);

    private final IBarrierRepository barrierRepository;
    private final ITaskRepository taskRepository;

    public BarrierService(
            final IBarrierRepository barrierRepository,
            final ITaskRepository taskRepository
    ) {
        this.barrierRepository = Objects.requireNonNull(barrierRepository);
        this.taskRepository = Objects.requireNonNull(taskRepository);
    }

    @Override
    @Transactional
    public void releaseBarrier(final UUID barrierId, final String client) {
        if (Objects.isNull(barrierId)) {
            throw new ApplicationException("Parameter barrierId cannot be null.");
        }
        if (Strings.isNullOrEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }
        LOGGER.info("Release barrier id: '{}'.", barrierId);

        final Barrier barrier = barrierRepository.get(barrierId).orElseThrow();

        if (barrier.isReleased()) {
            LOGGER.debug("Barrier '{}' has already been released.", barrierId);
            barrier.unlock();
        } else {
            if (barrier.getWaitFor().isEmpty()) {
                LOGGER.debug("Barrier '{}' doesn't have any waiting tasks.", barrierId);
                barrier.release(client);
            } else {
                LOGGER.debug("Barrier '{}' has '{}' waiting tasks.", barrierId, barrier.getWaitFor().size());
                final List<Task> tasks = taskRepository.get(barrier.getWaitFor());
                final boolean allTasksTerminatedState = tasks.stream().allMatch(Task::isTerminated);
                if (allTasksTerminatedState) {
                    LOGGER.debug("Barrier '{}' has released.", barrierId);
                    barrier.release(client);
                }else {
                    LOGGER.debug("Not all tasks have terminated state.");
                    barrier.unlock();
                }
            }
        }

        barrierRepository.update(barrier);
    }

    @Override
    @Transactional
    public void unlockBarrier(final UUID barrierId, final int lockedTimeoutSeconds) {
        if (Objects.isNull(barrierId)) {
            throw new ApplicationException("Parameter barrierId cannot be null.");
        }
        if (lockedTimeoutSeconds <= 0) {
            throw new ApplicationException("Parameter lockedTimeoutSeconds must be positive.");
        }
        LOGGER.warn("Locked barrier id: '{}'.", barrierId);

        final Barrier barrier = barrierRepository.get(barrierId).orElseThrow();

        barrier.unblock(lockedTimeoutSeconds);

        barrierRepository.update(barrier);
    }
}
