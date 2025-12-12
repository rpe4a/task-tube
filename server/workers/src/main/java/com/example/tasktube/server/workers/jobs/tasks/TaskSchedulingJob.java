package com.example.tasktube.server.workers.jobs.tasks;

import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.infrastructure.configuration.InstanceIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class TaskSchedulingJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSchedulingJob.class);

    private final IJobService jobService;
    private final ITaskService taskService;
    private final InstanceIdProvider instanceId;

    @Value("${spring.application.jobs.tasks.scheduling.delay}")
    private long delay;

    @Value("${spring.application.jobs.tasks.scheduling.count}")
    private int count;

    public TaskSchedulingJob(
            final IJobService jobService,
            final ITaskService taskService,
            final InstanceIdProvider instanceId
    ) {
        this.jobService = Objects.requireNonNull(jobService);
        this.taskService = Objects.requireNonNull(taskService);
        this.instanceId = Objects.requireNonNull(instanceId);
    }

    @Scheduled(fixedDelayString = "${spring.application.jobs.tasks.scheduling.delay}")
    public void run() {
        LOGGER.info("Start scheduling tasks.");
        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, count, instanceId.get());

        if(taskIdList.isEmpty()) {
            LOGGER.info("No created tasks found.");
            return;
        }

        LOGGER.info("List of tasks: '{}'.", taskIdList);
        for (final UUID taskId : taskIdList) {
            taskService.scheduleTask(taskId, Instant.now(), instanceId.get());
        }
    }
}