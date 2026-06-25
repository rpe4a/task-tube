package com.example.tasktube.server.workers.jobs.tasktubes;

import com.example.tasktube.server.application.models.TaskTubeDto;
import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.application.port.in.ITaskTubeService;
import com.example.tasktube.server.infrastructure.configuration.InstanceIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class TaskTubeTerminatingJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTubeTerminatingJob.class);

    private final IJobService jobService;
    private final ITaskTubeService taskTubeService;
    private final InstanceIdProvider instanceIdProvider;

    @Value("${spring.application.jobs.tasktubess.termination.count}")
    private int count;

    public TaskTubeTerminatingJob(
            final IJobService jobService,
            final ITaskTubeService taskTubeService,
            final InstanceIdProvider instanceIdProvider
    ) {
        this.jobService = Objects.requireNonNull(jobService);
        this.taskTubeService = Objects.requireNonNull(taskTubeService);
        this.instanceIdProvider = Objects.requireNonNull(instanceIdProvider);

    }

    @Scheduled(fixedDelayString = "${spring.application.jobs.tasks.locked.delay}")
    public void run() {
        LOGGER.info("Start find request termination tasktubes.");
        final List<UUID> lockedTaskTubeIdList = jobService.getRequestTerminationTaskTube(count, instanceIdProvider.get());

        if(lockedTaskTubeIdList.isEmpty()) {
            LOGGER.info("No request termination tasktubes found.");
            return;
        }

        LOGGER.info("List of tasktubes: '{}'.", lockedTaskTubeIdList);
        for (final UUID taskTubeId : lockedTaskTubeIdList) {
            taskTubeService.terminate(taskTubeId, instanceIdProvider.get());
        }
    }
}