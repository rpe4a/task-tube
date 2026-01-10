package com.example.tasktube.server.workers.jobs.barriers;

import com.example.tasktube.server.application.port.in.IBarrierService;
import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.domain.enties.Barrier;
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
public class BarrierReleasingJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BarrierReleasingJob.class);

    private final IJobService jobService;
    private final IBarrierService barrierService;
    private final InstanceIdProvider instanceId;

    @Value("${spring.application.jobs.barriers.releasing.delay}")
    private long delay;

    @Value("${spring.application.jobs.barriers.releasing.count}")
    private int count;

    public BarrierReleasingJob(
            final IJobService jobService,
            final IBarrierService barrierService,
            final InstanceIdProvider instanceId) {
        this.jobService = Objects.requireNonNull(jobService);
        this.barrierService = Objects.requireNonNull(barrierService);
        this.instanceId = Objects.requireNonNull(instanceId);
    }

    @Scheduled(fixedDelayString = "${spring.application.jobs.barriers.releasing.delay}")
    public void run() {
        LOGGER.info("Start releasing barriers.");
        final List<UUID> waitingBarrierIdList = jobService.getBarrierIdList(Barrier.Status.WAITING, count, instanceId.get());

        if (waitingBarrierIdList.isEmpty()) {
            LOGGER.info("No barriers found.");
            return;
        }

        LOGGER.info("List of barriers: '{}'.", waitingBarrierIdList);
        for (final UUID barrierId : waitingBarrierIdList) {
            barrierService.release(barrierId, instanceId.get());
        }
    }
}