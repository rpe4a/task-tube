package com.example.tasktube.server.workers.jobs;

import com.example.tasktube.server.application.models.SchedulingDto;
import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.infrastructure.services.InstanceIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulingTaskJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingTaskJob.class);
    private final IJobService service;
    private final InstanceIdProvider instanceId;

    @Value("${spring.application.jobs.scheduling.delay}")
    private long delay;

    @Value("${spring.application.jobs.scheduling.count}")
    private int count;

    public SchedulingTaskJob(final IJobService service, final InstanceIdProvider instanceId) {
        this.service = service;
        this.instanceId = instanceId;
    }

    @Scheduled(fixedDelayString = "${spring.application.jobs.scheduling.delay}")
    public void run() {
        LOGGER.info("Start scheduling tasks.");
        service.scheduleTask(new SchedulingDto(instanceId.get(), count));
    }
}