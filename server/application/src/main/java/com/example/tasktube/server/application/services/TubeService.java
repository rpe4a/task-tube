package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.application.port.in.ITubeService;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TubeService implements ITubeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TubeService.class);

    private final ITubeRepository tubeRepository;
    private final IBarrierRepository barrierRepository;

    public TubeService(
            final ITubeRepository tubeRepository,
            final IBarrierRepository barrierRepository
    ) {
        this.tubeRepository = Objects.requireNonNull(tubeRepository);
        this.barrierRepository = Objects.requireNonNull(barrierRepository);
    }

    @Transactional
    public UUID push(final TaskDto taskDto) {
        Preconditions.checkNotNull(taskDto);
        LOGGER.debug("Push task: '{}'.", taskDto);
        final Task task = taskDto.to(true);

        if (taskDto.waitTasks() != null && !taskDto.waitTasks().isEmpty()) {
            final Barrier barrier = task.addStartBarrier(taskDto.waitTasks());
            barrierRepository.save(barrier);
        }

        return tubeRepository
                .push(taskDto.to(true))
                .getId();
    }

    @Override
    public Optional<PopTaskDto> pop(final String tube, final String client) {
        Preconditions.checkArgument(Strings.isNotEmpty(tube));
        Preconditions.checkArgument(Strings.isNotEmpty(client));
        LOGGER.debug("Pop task from '{}' by '{}' client.", tube, client);

        return tubeRepository
                .pop(tube, client)
                .map(PopTaskDto::from);
    }
}
