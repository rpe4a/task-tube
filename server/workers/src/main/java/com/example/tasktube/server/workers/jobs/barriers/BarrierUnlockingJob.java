package com.example.tasktube.server.workers.jobs.barriers;

import com.example.tasktube.server.application.port.in.IBarrierService;
import com.example.tasktube.server.application.port.in.IJobService;
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
public class BarrierUnlockingJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BarrierUnlockingJob.class);

    private final IJobService jobService;
    private final IBarrierService barrierService;
    private final InstanceIdProvider instanceId;

    @Value("${spring.application.jobs.barriers.locked.delay}")
    private long delay;

    @Value("${spring.application.jobs.barriers.locked.count}")
    private int count;

    @Value("${spring.application.jobs.barriers.locked.timeoutSeconds}")
    private int timeoutSeconds;

    public BarrierUnlockingJob(
            final IJobService jobService,
            final IBarrierService barrierService,
            final InstanceIdProvider instanceId) {
        this.jobService = Objects.requireNonNull(jobService);
        this.barrierService = Objects.requireNonNull(barrierService);
        this.instanceId = Objects.requireNonNull(instanceId);
    }

    @Scheduled(fixedDelayString = "${spring.application.jobs.barriers.releasing.delay}")
    public void run() {
        LOGGER.info("Start find locked barriers.");
        final List<UUID> lockedBarrierIdList = jobService.getLockedBarrierIdList(count, timeoutSeconds);

        if(lockedBarrierIdList.isEmpty()) {
            LOGGER.info("No locked barriers found.");
            return;
        }

        LOGGER.info("List of barriers: '{}'.", lockedBarrierIdList);
        for (final UUID barrierId : lockedBarrierIdList) {
            barrierService.unlockBarrier(barrierId, timeoutSeconds);
        }
    }
}