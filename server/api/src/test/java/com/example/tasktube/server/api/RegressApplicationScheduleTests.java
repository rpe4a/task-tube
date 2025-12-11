package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.Lock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest(
        classes = RestApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class RegressApplicationScheduleTests extends AbstractRegressApplicationTests {

    @Test
    void shouldCreatedTaskScheduleWithoutLockFailed() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        assertThatThrownBy(() -> taskService.scheduleTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldCreatedTaskStartFailed() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        assertThatThrownBy(() -> taskService.startTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldCreatedTaskProcessFailed() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        assertThatThrownBy(() -> taskService.processTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldCreatedTaskFinishFailed() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        assertThatThrownBy(() -> taskService.finishTask(new FinishTaskDto(taskId, null, new HashMap<>(), CLIENT, Instant.now())));
    }

    @Test
    void shouldCreatedTaskFailFailed() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        assertThatThrownBy(() -> taskService.failTask(taskId, Instant.now(), "fail reason", instanceIdProvider.get()));
    }

    @Test
    void shouldCreatedTaskCompleteFailed() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        assertThatThrownBy(() -> taskService.completeTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldScheduledTaskWithoutBarrier() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final Optional<Task> pushTask = taskRepository.get(taskId);
        assertThat(pushTask.isPresent()).isTrue();
        assertThat(pushTask.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(pushTask.get().getStartBarrier()).isNull();

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());
        assertThat(taskIdList).isNotEmpty();
        assertThat(taskIdList.size()).isEqualTo(1);

        final UUID createdTaskId = taskIdList.get(0);
        final Optional<Task> taskLocked = taskRepository.get(createdTaskId);
        assertThat(taskLocked.isPresent()).isTrue();
        assertThat(taskLocked.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(taskLocked.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<Task> taskScheduled = taskRepository.get(taskId);
        assertThat(taskScheduled.isPresent()).isTrue();
        assertThat(taskScheduled.get().getStatus()).isEqualTo(Task.Status.SCHEDULED);
        assertThat(taskScheduled.get().getScheduledAt()).isNotNull();
        assertThat(taskScheduled.get().getCanceledAt()).isNull();
        assertThat(taskScheduled.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskLocked.get().getUpdatedAt().toEpochMilli());
        assertThat(taskScheduled.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldScheduledTaskWithBarrierFail() {
        final TaskDto taskDto = TestUtils.createTaskDto(List.of(UUID.randomUUID()));

        final UUID taskId = tubeService.push(taskDto);

        final Optional<Task> pushTask = taskRepository.get(taskId);
        assertThat(pushTask.isPresent()).isTrue();
        assertThat(pushTask.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(pushTask.get().getStartBarrier()).isNotNull();

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());
        assertThat(taskIdList).isNotEmpty();
        assertThat(taskIdList.size()).isEqualTo(1);

        final UUID createdTaskId = taskIdList.get(0);
        final Optional<Task> taskLocked = taskRepository.get(createdTaskId);
        assertThat(taskLocked.isPresent()).isTrue();
        assertThat(taskLocked.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(taskLocked.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<Task> taskNotScheduled = taskRepository.get(taskId);
        assertThat(taskNotScheduled.isPresent()).isTrue();
        assertThat(taskNotScheduled.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(taskNotScheduled.get().getScheduledAt()).isNull();
        assertThat(taskNotScheduled.get().getCanceledAt()).isNull();
        assertThat(taskNotScheduled.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskLocked.get().getUpdatedAt().toEpochMilli());
        assertThat(taskNotScheduled.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldScheduledTaskWithBarrier() {
        final TaskDto taskDto = TestUtils.createTaskDto(List.of(UUID.randomUUID()));

        final UUID taskId = tubeService.push(taskDto);

        final Optional<Task> pushTask = taskRepository.get(taskId);
        assertThat(pushTask.isPresent()).isTrue();
        assertThat(pushTask.get().getStatus()).isEqualTo(Task.Status.CREATED);

        final List<UUID> barrierIdList = jobService.getBarrierIdList(10, instanceIdProvider.get());
        assertThat(barrierIdList).isNotEmpty();
        assertThat(barrierIdList.size()).isEqualTo(1);

        final UUID startBarrierId = barrierIdList.get(0);
        final Optional<Barrier> startBarrier = barrierRepository.get(startBarrierId);
        assertThat(startBarrier.isPresent()).isTrue();
        assertThat(startBarrier.get().getType()).isEqualTo(Barrier.Type.START);
        assertThat(startBarrier.get().getId()).isEqualTo(pushTask.get().getStartBarrier());
        assertThat(startBarrier.get().isNotReleased()).isTrue();
        assertThat(startBarrier.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        barrierService.releaseBarrier(startBarrierId, instanceIdProvider.get());

        final Optional<Barrier> startReleasedBarrier = barrierRepository.get(startBarrierId);
        assertThat(startReleasedBarrier.isPresent()).isTrue();
        assertThat(startReleasedBarrier.get().isReleased()).isTrue();
        assertThat(startReleasedBarrier.get().getReleasedAt()).isNotNull();
        assertThat(startReleasedBarrier.get().getUpdatedAt().toEpochMilli()).isGreaterThan(startBarrier.get().getUpdatedAt().toEpochMilli());
        assertThat(startReleasedBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());
        assertThat(taskIdList).isNotEmpty();
        assertThat(taskIdList.size()).isEqualTo(1);

        final UUID createdTaskId = taskIdList.get(0);
        final Optional<Task> taskLocked = taskRepository.get(createdTaskId);
        assertThat(taskLocked.isPresent()).isTrue();
        assertThat(taskLocked.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(taskLocked.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<Task> taskScheduled = taskRepository.get(taskId);
        assertThat(taskScheduled.isPresent()).isTrue();
        assertThat(taskScheduled.get().getStatus()).isEqualTo(Task.Status.SCHEDULED);
        assertThat(taskScheduled.get().getScheduledAt()).isNotNull();
        assertThat(taskScheduled.get().getCanceledAt()).isNull();
        assertThat(taskScheduled.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskLocked.get().getUpdatedAt().toEpochMilli());
        assertThat(taskScheduled.get().getLock()).isEqualTo(Lock.free());
    }
}
