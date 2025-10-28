package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class JobService implements IJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);
    private final ITaskRepository repository;

    public JobService(final ITaskRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    @Transactional
    public void scheduleTask() {
        final List<Task> tasks = repository.getTasksForScheduling("worker", 10);
        if (tasks.isEmpty()) {
            LOGGER.info("There aren't any tasks to schedule.");
            return;
        }

        LOGGER.debug("There '{}' tasks to schedule.", tasks.size());
        for (final Task task : tasks) {
            task.schedule();
        }

        repository.schedule(tasks);
    }
}
