package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.application.port.in.ITubeService;
import com.example.tasktube.server.domain.enties.Tube;
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

    private final ITubeRepository repository;

    public TubeService(final ITubeRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Transactional
    public UUID push(final PushTaskDto task) {
        Preconditions.checkNotNull(task);

        LOGGER.debug("Push task: '{}'.", task);
        return repository
                .push(
                        Tube.pushTask(
                                task.name(),
                                task.queue(),
                                task.input(),
                                task.createAt()
                        )
                )
                .getId();
    }

    @Override
    public Optional<TaskDto> pop(final String tube, final String client) {
        Preconditions.checkArgument(Strings.isNotEmpty(tube));
        Preconditions.checkArgument(Strings.isNotEmpty(client));

        LOGGER.debug("Pop task from '{}' by '{}' client.", tube, client);
        return repository
                .pop(tube, client)
                .map(TaskDto::from);
    }
}
