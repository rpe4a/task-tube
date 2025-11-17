package com.example.tasktube.server.workers.jobs;

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
import java.util.UUID;

@Component
public class TaskCompletingJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCompletingJob.class);
    private final IJobService jobService;
    private final ITaskService taskService;
    private final InstanceIdProvider instanceId;

    @Value("${spring.application.jobs.tasks.finalizing.delay}")
    private long delay;

    @Value("${spring.application.jobs.tasks.finalizing.count}")
    private int count;

    public TaskCompletingJob(
            final IJobService jobService,
            final ITaskService taskService,
            final InstanceIdProvider instanceId) {
        this.jobService = jobService;
        this.taskService = taskService;
        this.instanceId = instanceId;
    }

    @Scheduled(fixedDelayString = "${spring.application.jobs.tasks.finalizing.delay}")
    public void run() {
        LOGGER.info("Start finalizing tasks.");
        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.FINISHED, count, instanceId.get());

        LOGGER.debug("List of tasks: '{}'.", taskIdList);
        for (final UUID taskId : taskIdList) {
            taskService.completeTask(taskId, Instant.now(), instanceId.get());
        }
    }
}