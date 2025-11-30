package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.application.models.TaskSettingsDto;
import com.example.tasktube.server.application.services.TaskService;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.Lock;
import com.example.tasktube.server.domain.values.TaskSettings;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest(
        classes = RestApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class RegressApplicationPushPopTests extends AbstractRegressApplicationTests {

    @Test
    void shouldPushTaskWithoutStartBarrierSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getId()).isEqualTo(taskId);
        assertThat(task.get().getName()).isEqualTo(taskDto.name());
        assertThat(task.get().getTube()).isEqualTo(taskDto.tube());
        assertThat(task.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(task.get().getParentId()).isNull();
        assertThat(task.get().getInput()).isEqualTo(taskDto.input());
        assertThat(task.get().getOutput()).isNull();
        assertThat(task.get().isRoot()).isTrue();
        assertThat(task.get().getStartBarrier()).isNull();
        assertThat(task.get().getFinishBarrier()).isNull();
        assertThat(task.get().getUpdatedAt()).isNotNull();
        assertThat(task.get().getCreatedAt().toEpochMilli()).isEqualTo(taskDto.createdAt().toEpochMilli());
        assertThat(task.get().getScheduledAt()).isNull();
        assertThat(task.get().getCanceledAt()).isNull();
        assertThat(task.get().getStartedAt()).isNull();
        assertThat(task.get().getHeartbeatAt()).isNull();
        assertThat(task.get().getFinishedAt()).isNull();
        assertThat(task.get().getFailedAt()).isNull();
        assertThat(task.get().getAbortedAt()).isNull();
        assertThat(task.get().getCompletedAt()).isNull();
        assertThat(task.get().getCompletedAt()).isNull();
        assertThat(task.get().getFailures()).isEqualTo(0);
        assertThat(task.get().getFailedReason()).isNull();
        assertThat(task.get().getLock()).isEqualTo(Lock.free());
        assertThat(task.get().getSettings()).isEqualTo(TaskSettings.getDefault());
    }

    @Test
    void shouldPushTaskWithStartBarrierSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto(List.of(UUID.randomUUID()));

        final UUID taskId = tubeService.push(taskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
        assertThat(task.get().getId()).isEqualTo(taskId);
        assertThat(task.get().getName()).isEqualTo(taskDto.name());
        assertThat(task.get().getTube()).isEqualTo(taskDto.tube());
        assertThat(task.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(task.get().getParentId()).isNull();
        assertThat(task.get().getInput()).isEqualTo(taskDto.input());
        assertThat(task.get().getOutput()).isNull();
        assertThat(task.get().isRoot()).isTrue();
        assertThat(task.get().getStartBarrier()).isNotNull();

        final Optional<Barrier> barrier = barrierRepository.get(task.get().getStartBarrier());
        assertThat(barrier.isPresent()).isTrue();
        assertThat(barrier.get().getId()).isEqualTo(task.get().getStartBarrier());
        assertThat(barrier.get().getTaskId()).isEqualTo(task.get().getId());
        assertThat(barrier.get().getWaitFor()).isEqualTo(taskDto.waitTasks());
        assertThat(barrier.get().getType()).isEqualTo(Barrier.Type.START);
        assertThat(barrier.get().isReleased()).isFalse();
        assertThat(barrier.get().getUpdatedAt()).isNotNull();
        assertThat(barrier.get().getCreatedAt()).isNotNull();
        assertThat(barrier.get().getReleasedAt()).isNull();
        assertThat(barrier.get().getLock()).isEqualTo(Lock.free());

        assertThat(task.get().getFinishBarrier()).isNull();
        assertThat(task.get().getUpdatedAt()).isNotNull();
        assertThat(task.get().getCreatedAt().toEpochMilli()).isEqualTo(taskDto.createdAt().toEpochMilli());
        assertThat(task.get().getScheduledAt()).isNull();
        assertThat(task.get().getCanceledAt()).isNull();
        assertThat(task.get().getStartedAt()).isNull();
        assertThat(task.get().getHeartbeatAt()).isNull();
        assertThat(task.get().getFinishedAt()).isNull();
        assertThat(task.get().getFailedAt()).isNull();
        assertThat(task.get().getAbortedAt()).isNull();
        assertThat(task.get().getCompletedAt()).isNull();
        assertThat(task.get().getCompletedAt()).isNull();
        assertThat(task.get().getFailures()).isEqualTo(0);
        assertThat(task.get().getFailedReason()).isNull();
        assertThat(task.get().getLock()).isEqualTo(Lock.free());
        assertThat(task.get().getSettings()).isEqualTo(TaskSettings.getDefault());
    }

    @Test
    void shouldPopTaskAfterPushFailed() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final Optional<Task> pushTask = taskRepository.get(taskId);
        assertThat(pushTask.isPresent()).isTrue();

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), instanceIdProvider.get());
        assertThat(popTask.isEmpty()).isTrue();
    }

    @Test
    void shouldPopTaskSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final Optional<Task> pushTask = taskRepository.get(taskId);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        final Optional<Task> taskLocked = taskRepository.get(createdTaskId);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);
        assertThat(popTask.isEmpty()).isFalse();
        assertThat(popTask.get().id()).isEqualTo(pushTask.get().getId());
        assertThat(popTask.get().name()).isEqualTo(pushTask.get().getName());
        assertThat(popTask.get().tube()).isEqualTo(pushTask.get().getTube());
        assertThat(popTask.get().input()).isEqualTo(pushTask.get().getInput());

        final Optional<Task> taskScheduled = taskRepository.get(popTask.get().id());
        assertThat(taskScheduled.isEmpty()).isFalse();
        assertThat(taskScheduled.get().getStatus()).isEqualTo(Task.Status.SCHEDULED);
        assertThat(taskScheduled.get().getScheduledAt()).isNotNull();
        assertThat(taskScheduled.get().getCanceledAt()).isNull();
        assertThat(taskScheduled.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskLocked.get().getUpdatedAt().toEpochMilli());
        assertThat(taskScheduled.get().getLock().isLockedBy(CLIENT)).isTrue();
    }

    @Test
    void shouldPopTaskOneTimeSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final Optional<Task> pushTask = taskRepository.get(taskId);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        final Optional<Task> taskLocked = taskRepository.get(createdTaskId);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);
        assertThat(popTask.isEmpty()).isFalse();

        final Optional<PopTaskDto> popTask2 = tubeService.pop(taskDto.tube(), CLIENT);
        assertThat(popTask2.isEmpty()).isTrue();
    }
}
