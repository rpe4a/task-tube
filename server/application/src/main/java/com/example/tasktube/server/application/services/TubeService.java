package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.application.models.TaskSettingsDto;
import com.example.tasktube.server.application.port.in.ITubeService;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IEventPublisher;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TubeService implements ITubeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TubeService.class);

    private final ITubeRepository tubeRepository;
    private final IEventPublisher eventPublisher;

    public TubeService(
            final ITubeRepository tubeRepository,
            final IEventPublisher eventPublisher
    ) {
        this.tubeRepository = Objects.requireNonNull(tubeRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    @Transactional
    public UUID push(final PushTaskDto pushTaskDto, final String client) {
        if (Objects.isNull(pushTaskDto)) {
            throw new ApplicationException("Parameter pushTaskDto cannot be null.");
        }
        if (StringUtils.isEmpty(client)) {
            throw new ApplicationException("Parameter client name cannot be null or empty.");
        }
        LOGGER.info("Push task: '{}' by client '{}'.", pushTaskDto, client);

        final Task task = Task.pushNew(
                pushTaskDto.id(),
                pushTaskDto.name(),
                pushTaskDto.tube(),
                pushTaskDto.correlationId(),
                pushTaskDto.input(),
                pushTaskDto.createdAt(),
                pushTaskDto.getSettings(),
                pushTaskDto.getWaitingTaskIdList(),
                client
        );

        final Task pushedTask = tubeRepository.push(task);
        eventPublisher.publish(task.pullEvents());

        return pushedTask.getId();
    }

    // TODO: add tests
    @Override
    @Transactional
    public Optional<PopTaskDto> pop(final String tube, final String client) {
        if (StringUtils.isEmpty(tube)) {
            throw new ApplicationException("Parameter tube name cannot be null or empty.");
        }
        if (StringUtils.isEmpty(client)) {
            throw new ApplicationException("Parameter client name cannot be null or empty.");
        }
        LOGGER.info("Pop task from '{}' by '{}' client.", tube, client);

        return tubeRepository
                .pop(tube, client)
                .map(PopTaskDto::from);
    }

    // TODO: add tests
    @Override
    @Transactional
    public List<PopTaskDto> popList(final String tube, final String client, final int count) {
        if (StringUtils.isEmpty(tube)) {
            throw new ApplicationException("Parameter tube name cannot be null or empty.");
        }
        if (StringUtils.isEmpty(client)) {
            throw new ApplicationException("Parameter client name cannot be null or empty.");
        }
        if (count <= 0) {
            throw new ApplicationException("Parameter count should be greater than 0.");
        }
        LOGGER.info("Pop '{}' tasks from '{}' by '{}' client.", count, tube, client);

        final List<Task> tasks = tubeRepository.popList(tube, client, count);
        LOGGER.info("Popped '{}' tasks from '{}' by '{}' client.", tasks.size(), tube, client);

        return tasks.stream()
                .map(PopTaskDto::from)
                .toList();
    }
}
