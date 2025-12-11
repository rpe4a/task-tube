package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IJobRepository;
import com.google.common.base.Strings;
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

    private final IJobRepository jobRepository;

    public JobService(
            final IJobRepository jobRepository
    ) {
        this.jobRepository = Objects.requireNonNull(jobRepository);
    }

    @Override
    @Transactional
    public List<UUID> getTaskIdList(final Task.Status status, final int count, final String client) {
        if (count <= 0) {
            throw new ApplicationException("Parameter count must be more than zero.");
        }
        if (Strings.isNullOrEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }
        if (Objects.isNull(status)) {
            throw new ApplicationException("Parameter status cannot be null.");
        }
        return jobRepository.lockTaskIdList(status, count, client);
    }

    @Override
    @Transactional
    public List<UUID> getLockedTaskIdList(final int count, final int lockedTimeoutSeconds) {
        if (count <= 0) {
            throw new ApplicationException("Parameter count must be more than zero.");
        }
        if (lockedTimeoutSeconds <= 0) {
            throw new ApplicationException("Parameter lockedTimeoutSeconds must be  more than zero.");
        }
        return jobRepository.getLockedTaskIdList(count, lockedTimeoutSeconds);
    }

    @Override
    @Transactional
    public List<UUID> getBarrierIdList(final int count, final String client) {
        if (count <= 0) {
            throw new ApplicationException("Parameter count must be more than zero.");
        }
        if (Strings.isNullOrEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }
        return jobRepository.lockBarrierIdList(count, client);
    }

    @Override
    @Transactional
    public List<UUID> getLockedBarrierIdList(final int count, final int lockedTimeoutSeconds) {
        if (count <= 0) {
            throw new ApplicationException("Parameter count must be more than zero.");
        }
        if (lockedTimeoutSeconds <= 0) {
            throw new ApplicationException("Parameter lockedTimeoutSeconds must be more than zero.");
        }
        return jobRepository.getLockedBarrierIdList(count, lockedTimeoutSeconds);
    }
}
