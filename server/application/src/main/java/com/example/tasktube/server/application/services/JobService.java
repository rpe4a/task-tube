package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.models.SchedulingDto;
import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.IJobRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class JobService implements IJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);
    private final ITaskRepository repository;
    private final IJobRepository jobRepository;

    public JobService(
            final ITaskRepository taskRepository,
            final IJobRepository jobRepository
    ) {
        this.repository = Objects.requireNonNull(taskRepository);
        this.jobRepository = jobRepository;
    }

    @Override
    @Transactional
    public void scheduleTask(final SchedulingDto schedulingDto) {
        final List<Task> tasks = repository.getTasksForScheduling(
                schedulingDto.worker(),
                schedulingDto.count()
        );
        
        if (tasks.isEmpty()) {
            LOGGER.info("There aren't any tasks to schedule.");
            return;
        }

        LOGGER.debug("There '{}' tasks to schedule.", tasks.size());
        for (final Task task : tasks) {
            task.schedule();
        }

        repository.schedule(tasks);
    }

    @Override
    @Transactional
    public List<UUID> getTaskIdList(final Task.Status status, final int count, final String client) {
        Preconditions.checkArgument(count > 0);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));
        Preconditions.checkNotNull(status);

        return jobRepository.getTaskIdList(status, count, client);

    }

    @Override
    @Transactional
    public List<UUID> getBarrierIdList(final int count, final String client) {
        Preconditions.checkArgument(count > 0);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));

        return jobRepository.getBarrierIdList(count, client);
    }
}
