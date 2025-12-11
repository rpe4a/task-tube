package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IJobRepository;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class JobService implements IJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);
    private final IJobRepository jobRepository;

    public JobService(
            final IJobRepository jobRepository
    ) {
        this.jobRepository = jobRepository;
    }

    @Override
    @Transactional
    public List<UUID> getTaskIdList(final Task.Status status, final int count, final String client) {
        Preconditions.checkArgument(count > 0);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));
        Preconditions.checkNotNull(status);

        return jobRepository.lockTaskIdList(status, count, client);

    }

    @Override
    @Transactional
    public List<UUID> getLockedTaskIdList(final int count, final int lockedTimeoutSeconds) {
        Preconditions.checkArgument(count > 0);
        Preconditions.checkArgument(lockedTimeoutSeconds > 0);

        return jobRepository.getLockedTaskIdList(count, lockedTimeoutSeconds);
    }

    @Override
    @Transactional
    public List<UUID> getBarrierIdList(final int count, final String client) {
        Preconditions.checkArgument(count > 0);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(client));

        return jobRepository.lockBarrierIdList(count, client);
    }

    @Override
    @Transactional
    public List<UUID> getLockedBarrierIdList(final int count, final int lockedTimeoutSeconds) {
        Preconditions.checkArgument(count > 0);
        Preconditions.checkArgument(lockedTimeoutSeconds > 0);

        return jobRepository.getLockedBarrierIdList(count, lockedTimeoutSeconds);
    }
}
