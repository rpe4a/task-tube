package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.port.in.IBarrierService;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BarrierService implements IBarrierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BarrierService.class);
    private final IBarrierRepository barrierRepository;
    private final ITaskRepository taskRepository;

    public BarrierService(
            final IBarrierRepository barrierRepository,
            final ITaskRepository taskRepository
    ) {
        this.barrierRepository = barrierRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public void releaseBarrier(final UUID barrierId, final String client) {
        Preconditions.checkNotNull(barrierId);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));
        LOGGER.info("Release barrier id: '{}'.", barrierId);

        final Optional<Barrier> barrier = barrierRepository.get(barrierId);
        if (barrier.isEmpty()){
            LOGGER.debug("No barrier found for id: '{}'.", barrierId);
            return;
        }

        if (barrier.get().isReleased()) {
            LOGGER.debug("Barrier '{}' has already been released.", barrierId);
            return;
        }

        if (barrier.get().getWaitFor().isEmpty()) {
            LOGGER.debug("Barrier '{}' doesn't have any waiting tasks.", barrierId);
            barrier.get().release(client);
        } else {
            LOGGER.debug("Barrier '{}' has '{}' waiting tasks.", barrierId, barrier.get().getWaitFor().size());
            final List<Task> tasks = taskRepository.get(barrier.get().getWaitFor());
            final boolean allTasksTerminatedState = tasks.stream().allMatch(Task::isTerminated);
            if (allTasksTerminatedState) {
                LOGGER.debug("Barrier '{}' has released.", barrierId);
                barrier.get().release(client);
            }
        }

        barrierRepository.release(barrier.orElse(null));
    }
}
