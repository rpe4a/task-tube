package com.example.tasktube.server.workers.jobs.tasks;

import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.application.port.in.ITaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class TaskUnlockingJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskUnlockingJob.class);

    private final IJobService jobService;
    private final ITaskService taskService;

    @Value("${spring.application.jobs.tasks.locked.delay}")
    private long delay;

    @Value("${spring.application.jobs.tasks.locked.count}")
    private int count;

    @Value("${spring.application.jobs.tasks.locked.timeoutSeconds}")
    private int timeoutSeconds;

    public TaskUnlockingJob(
            final IJobService jobService,
            final ITaskService taskService
    ) {
        this.jobService = Objects.requireNonNull(jobService);
        this.taskService = Objects.requireNonNull(taskService);
    }

    @Scheduled(fixedDelayString = "${spring.application.jobs.tasks.scheduling.delay}")
    public void run() {
        LOGGER.info("Start find locked tasks.");
        final List<UUID> lockedTaskIdList = jobService.getLockedTaskIdList(count, timeoutSeconds);

        if(lockedTaskIdList.isEmpty()) {
            LOGGER.info("No locked tasks found.");
            return;
        }

        LOGGER.info("List of tasks: '{}'.", lockedTaskIdList);
        for (final UUID taskId : lockedTaskIdList) {
            taskService.unlockTask(taskId, timeoutSeconds);
        }
    }
}