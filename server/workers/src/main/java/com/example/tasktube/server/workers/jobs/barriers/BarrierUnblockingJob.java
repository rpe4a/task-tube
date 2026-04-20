package com.example.tasktube.server.workers.jobs.barriers;

import com.example.tasktube.server.application.port.in.IBarrierService;
import com.example.tasktube.server.application.port.in.IJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class BarrierUnblockingJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BarrierUnblockingJob.class);

    private final IJobService jobService;
    private final IBarrierService barrierService;

    @Value("${spring.application.jobs.barriers.locked.count}")
    private int count;

    @Value("${spring.application.jobs.barriers.locked.timeoutSeconds}")
    private int timeoutSeconds;

    public BarrierUnblockingJob(
            final IJobService jobService,
            final IBarrierService barrierService
    ) {
        this.jobService = Objects.requireNonNull(jobService);
        this.barrierService = Objects.requireNonNull(barrierService);
    }

    @Scheduled(fixedDelayString = "${spring.application.jobs.barriers.locked.delay}")
    public void run() {
        LOGGER.info("Start find locked barriers.");
        final List<UUID> lockedBarrierIdList = jobService.getLockedBarrierIdList(count, timeoutSeconds);

        if(lockedBarrierIdList.isEmpty()) {
            LOGGER.info("No locked barriers found.");
            return;
        }

        LOGGER.info("List of barriers: '{}'.", lockedBarrierIdList);
        for (final UUID barrierId : lockedBarrierIdList) {
            barrierService.unblock(barrierId, timeoutSeconds);
        }
    }
}