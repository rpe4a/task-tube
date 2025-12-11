package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.Lock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(
        classes = RestApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class RegressApplicationUnlockTaskTests extends AbstractRegressApplicationTests {

    @Test
    void shouldNotGetLockedTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(task.get().getLock()).isEqualTo(Lock.free());

        task.get().setLock(new Lock(Instant.now(), true, instanceIdProvider.get()));

        taskRepository.update(task.get());

        final List<UUID> lockedTasks = jobService.getLockedTaskIdList(10, 600);
        assertThat(lockedTasks).isEmpty();
    }

    @Test
    void shouldGetLockedTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(task.get().getLock()).isEqualTo(Lock.free());

        task.get().setLock(new Lock(Instant.now().minusSeconds(600), true, instanceIdProvider.get()));

        taskRepository.update(task.get());

        final List<UUID> lockedTasks = jobService.getLockedTaskIdList(10, 600);
        assertThat(lockedTasks).hasSize(1);
    }

    @Test
    void shouldUnlockedCreatedTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(task.get().getLock()).isEqualTo(Lock.free());

        final Lock lock = new Lock(Instant.now().minusSeconds(600), true, instanceIdProvider.get());
        task.get().setLock(lock);

        taskRepository.update(task.get());

        final List<UUID> lockedTasks = jobService.getLockedTaskIdList(10, 600);
        assertThat(lockedTasks).hasSize(1);

        final Task lockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(lockedTask.getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(lockedTask.getCreatedAt().toEpochMilli()).isEqualTo(task.get().getCreatedAt().toEpochMilli());
        assertThat(lockedTask.getLock().lockedBy()).isEqualTo(lock.lockedBy());
        assertThat(lockedTask.getLock().lockedAt().toEpochMilli()).isEqualTo(lock.lockedAt().toEpochMilli());
        assertThat(lockedTask.getLock().locked()).isEqualTo(lock.locked());

        taskService.unlockTask(taskId, 600);

        final Task unlockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(unlockedTask.getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(unlockedTask.getCreatedAt().toEpochMilli()).isGreaterThan(lockedTask.getCreatedAt().toEpochMilli());
        assertThat(unlockedTask.getUpdatedAt().toEpochMilli()).isGreaterThan(lockedTask.getUpdatedAt().toEpochMilli());
        assertThat(unlockedTask.getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldUnlockedScheduledTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getLock()).isEqualTo(Lock.free());

        final Lock lock = new Lock(Instant.now().minusSeconds(600), true, instanceIdProvider.get());
        task.get().setLock(lock);
        task.get().setScheduledAt(Instant.now());
        task.get().setStatus(Task.Status.SCHEDULED);

        taskRepository.update(task.get());

        final List<UUID> lockedTasks = jobService.getLockedTaskIdList(10, 600);
        assertThat(lockedTasks).hasSize(1);

        final Task lockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(lockedTask.getStatus()).isEqualTo(Task.Status.SCHEDULED);
        assertThat(lockedTask.getCreatedAt().toEpochMilli()).isEqualTo(task.get().getCreatedAt().toEpochMilli());
        assertThat(lockedTask.getLock().lockedBy()).isEqualTo(lock.lockedBy());
        assertThat(lockedTask.getLock().lockedAt().toEpochMilli()).isEqualTo(lock.lockedAt().toEpochMilli());
        assertThat(lockedTask.getLock().locked()).isEqualTo(lock.locked());

        taskService.unlockTask(taskId, 600);

        final Task unlockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(unlockedTask.getStatus()).isEqualTo(Task.Status.SCHEDULED);
        assertThat(unlockedTask.getScheduledAt().toEpochMilli()).isGreaterThan(lockedTask.getScheduledAt().toEpochMilli());
        assertThat(unlockedTask.getUpdatedAt().toEpochMilli()).isGreaterThan(lockedTask.getUpdatedAt().toEpochMilli());
        assertThat(unlockedTask.getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldUnlockedProcessingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getLock()).isEqualTo(Lock.free());

        final Lock lock = new Lock(Instant.now().minusSeconds(600), true, instanceIdProvider.get());
        task.get().setLock(lock);
        task.get().setScheduledAt(Instant.now());
        task.get().setStartedAt(Instant.now());
        task.get().setHeartbeatAt(Instant.now());
        task.get().setStatus(Task.Status.PROCESSING);

        taskRepository.update(task.get());

        final List<UUID> lockedTasks = jobService.getLockedTaskIdList(10, 600);
        assertThat(lockedTasks).hasSize(1);

        final Task lockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(lockedTask.getStatus()).isEqualTo(Task.Status.PROCESSING);
        assertThat(lockedTask.getCreatedAt().toEpochMilli()).isEqualTo(task.get().getCreatedAt().toEpochMilli());
        assertThat(lockedTask.getLock().lockedBy()).isEqualTo(lock.lockedBy());
        assertThat(lockedTask.getLock().lockedAt().toEpochMilli()).isEqualTo(lock.lockedAt().toEpochMilli());
        assertThat(lockedTask.getLock().locked()).isEqualTo(lock.locked());

        taskService.unlockTask(taskId, 600);

        final Task unlockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(unlockedTask.getStatus()).isEqualTo(Task.Status.SCHEDULED);
        assertThat(unlockedTask.getScheduledAt().toEpochMilli()).isGreaterThan(lockedTask.getScheduledAt().toEpochMilli());
        assertThat(unlockedTask.getStartedAt()).isNull();
        assertThat(unlockedTask.getHeartbeatAt()).isNull();
        assertThat(unlockedTask.getUpdatedAt().toEpochMilli()).isGreaterThan(lockedTask.getUpdatedAt().toEpochMilli());
        assertThat(unlockedTask.getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldUnlockedFinishedTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getLock()).isEqualTo(Lock.free());

        final Lock lock = new Lock(Instant.now().minusSeconds(600), true, instanceIdProvider.get());
        task.get().setLock(lock);
        task.get().setFinishedAt(Instant.now());
        task.get().setStatus(Task.Status.FINISHED);

        taskRepository.update(task.get());

        final List<UUID> lockedTasks = jobService.getLockedTaskIdList(10, 600);
        assertThat(lockedTasks).hasSize(1);

        final Task lockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(lockedTask.getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(lockedTask.getCreatedAt().toEpochMilli()).isEqualTo(task.get().getCreatedAt().toEpochMilli());
        assertThat(lockedTask.getLock().lockedBy()).isEqualTo(lock.lockedBy());
        assertThat(lockedTask.getLock().lockedAt().toEpochMilli()).isEqualTo(lock.lockedAt().toEpochMilli());
        assertThat(lockedTask.getLock().locked()).isEqualTo(lock.locked());

        taskService.unlockTask(taskId, 600);

        final Task unlockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(unlockedTask.getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(unlockedTask.getFinishedAt().toEpochMilli()).isGreaterThan(lockedTask.getFinishedAt().toEpochMilli());
        assertThat(unlockedTask.getUpdatedAt().toEpochMilli()).isGreaterThan(lockedTask.getUpdatedAt().toEpochMilli());
        assertThat(unlockedTask.getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldUnlockedCanceledTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getLock()).isEqualTo(Lock.free());

        final Lock lock = new Lock(Instant.now().minusSeconds(600), true, instanceIdProvider.get());
        task.get().setLock(lock);
        task.get().setCanceledAt(Instant.now());
        task.get().setStatus(Task.Status.CANCELED);

        taskRepository.update(task.get());

        final List<UUID> lockedTasks = jobService.getLockedTaskIdList(10, 600);
        assertThat(lockedTasks).hasSize(1);

        final Task lockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(lockedTask.getStatus()).isEqualTo(Task.Status.CANCELED);
        assertThat(lockedTask.getCreatedAt().toEpochMilli()).isEqualTo(task.get().getCreatedAt().toEpochMilli());
        assertThat(lockedTask.getLock().lockedBy()).isEqualTo(lock.lockedBy());
        assertThat(lockedTask.getLock().lockedAt().toEpochMilli()).isEqualTo(lock.lockedAt().toEpochMilli());
        assertThat(lockedTask.getLock().locked()).isEqualTo(lock.locked());

        taskService.unlockTask(taskId, 600);

        final Task unlockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(unlockedTask.getStatus()).isEqualTo(Task.Status.CANCELED);
        assertThat(unlockedTask.getCanceledAt().toEpochMilli()).isGreaterThan(lockedTask.getCanceledAt().toEpochMilli());
        assertThat(unlockedTask.getUpdatedAt().toEpochMilli()).isGreaterThan(lockedTask.getUpdatedAt().toEpochMilli());
        assertThat(unlockedTask.getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldUnlockedAbortedTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getLock()).isEqualTo(Lock.free());

        final Lock lock = new Lock(Instant.now().minusSeconds(600), true, instanceIdProvider.get());
        task.get().setLock(lock);
        task.get().setAbortedAt(Instant.now());
        task.get().setStatus(Task.Status.ABORTED);

        taskRepository.update(task.get());

        final List<UUID> lockedTasks = jobService.getLockedTaskIdList(10, 600);
        assertThat(lockedTasks).hasSize(1);

        final Task lockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(lockedTask.getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(lockedTask.getCreatedAt().toEpochMilli()).isEqualTo(task.get().getCreatedAt().toEpochMilli());
        assertThat(lockedTask.getLock().lockedBy()).isEqualTo(lock.lockedBy());
        assertThat(lockedTask.getLock().lockedAt().toEpochMilli()).isEqualTo(lock.lockedAt().toEpochMilli());
        assertThat(lockedTask.getLock().locked()).isEqualTo(lock.locked());

        taskService.unlockTask(taskId, 600);

        final Task unlockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(unlockedTask.getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(unlockedTask.getAbortedAt().toEpochMilli()).isGreaterThan(lockedTask.getAbortedAt().toEpochMilli());
        assertThat(unlockedTask.getUpdatedAt().toEpochMilli()).isGreaterThan(lockedTask.getUpdatedAt().toEpochMilli());
        assertThat(unlockedTask.getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldUnlockedCompletedTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getLock()).isEqualTo(Lock.free());

        final Lock lock = new Lock(Instant.now().minusSeconds(600), true, instanceIdProvider.get());
        task.get().setLock(lock);
        task.get().setCompletedAt(Instant.now());
        task.get().setStatus(Task.Status.COMPLETED);

        taskRepository.update(task.get());

        final List<UUID> lockedTasks = jobService.getLockedTaskIdList(10, 600);
        assertThat(lockedTasks).hasSize(1);

        final Task lockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(lockedTask.getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(lockedTask.getCreatedAt().toEpochMilli()).isEqualTo(task.get().getCreatedAt().toEpochMilli());
        assertThat(lockedTask.getLock().lockedBy()).isEqualTo(lock.lockedBy());
        assertThat(lockedTask.getLock().lockedAt().toEpochMilli()).isEqualTo(lock.lockedAt().toEpochMilli());
        assertThat(lockedTask.getLock().locked()).isEqualTo(lock.locked());

        taskService.unlockTask(taskId, 600);

        final Task unlockedTask = taskRepository.get(taskId).orElseThrow();
        assertThat(unlockedTask.getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(unlockedTask.getCompletedAt().toEpochMilli()).isGreaterThan(lockedTask.getCompletedAt().toEpochMilli());
        assertThat(unlockedTask.getUpdatedAt().toEpochMilli()).isGreaterThan(lockedTask.getUpdatedAt().toEpochMilli());
        assertThat(unlockedTask.getLock()).isEqualTo(Lock.free());
    }
}
