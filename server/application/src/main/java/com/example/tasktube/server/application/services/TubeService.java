package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.application.port.in.ITubeService;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
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

    @Override
    @Transactional
    public UUID push(final PushTaskDto pushTaskDto) {
        if (Objects.isNull(pushTaskDto)) {
            throw new ApplicationException("Parameter pushTaskDto cannot be null.");
        }
        LOGGER.info("Push task: '{}'.", pushTaskDto);
        final Task task = pushTaskDto.to(true);

        if (pushTaskDto.waitTasks() != null && !pushTaskDto.waitTasks().isEmpty()) {
            LOGGER.debug("Task has '{}' waiting tasks.", pushTaskDto.waitTasks().size());

            final Barrier barrier = task.addStartBarrier(pushTaskDto.waitTasks());
            barrierRepository.save(barrier);
        }

        return tubeRepository
                .push(task)
                .getId();
    }

    @Override
    @Transactional
    public Optional<PopTaskDto> pop(final String tube, final String client) {
        if (Strings.isEmpty(tube)) {
            throw new ApplicationException("Parameter tube name cannot be null or empty.");
        }
        if (Strings.isEmpty(client)) {
            throw new ApplicationException("Parameter client name cannot be null or empty.");
        }
        LOGGER.info("Pop task from '{}' by '{}' client.", tube, client);

        return tubeRepository
                .pop(tube, client)
                .map(PopTaskDto::from);
    }
}
