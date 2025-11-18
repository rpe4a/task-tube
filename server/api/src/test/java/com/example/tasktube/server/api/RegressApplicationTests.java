package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.application.port.in.IBarrierService;
import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.application.port.in.ITubeService;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.values.Lock;
import com.example.tasktube.server.domain.values.TaskSettings;
import com.example.tasktube.server.infrastructure.configuration.InstanceIdProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
class RegressApplicationTests {

    @Autowired
    ITubeService tubeService;

    @Autowired
    IJobService jobService;

    @Autowired
    ITaskService taskService;

    @Autowired
    IBarrierService barrierService;

    @Autowired
    ITaskRepository taskRepository;

    @Autowired
    IBarrierRepository barrierRepository;

    @Autowired
    InstanceIdProvider instanceIdProvider;

    @Autowired
    JdbcTemplate db;

    @BeforeEach
    void setup() {
        final String deleteTasks = """
                    delete from tasks
                """;

        final String deleteBarriers = """
                    delete from barriers
                """;

        db.execute(deleteTasks);
        db.execute(deleteBarriers);
    }

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
    void shouldScheduledTaskWithoutBarrierSuccessfully() {
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
    void shouldScheduledTaskWithBarrierSuccessFully() {
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