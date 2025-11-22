package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.application.models.TaskSettingsDto;
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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(
        classes = RestApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class RegressApplicationTests {

    public static final String CLIENT = "client";

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
    void shouldStartTaskSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());
        assertThat(taskPopped.isEmpty()).isFalse();

        final Instant startedAt = Instant.now();
        taskService.startTask(taskPopped.get().getId(), startedAt, CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());
        assertThat(taskProcessing.isEmpty()).isFalse();
        assertThat(taskProcessing.get().getStatus()).isEqualTo(Task.Status.PROCESSING);
        assertThat(taskProcessing.get().getStartedAt().toEpochMilli()).isEqualTo(startedAt.toEpochMilli());
        assertThat(taskProcessing.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskPopped.get().getUpdatedAt().toEpochMilli());
        assertThat(taskProcessing.get().getLock().isLockedBy(CLIENT)).isTrue();
        assertThat(taskProcessing.get().getLock().lockedAt().toEpochMilli()).isGreaterThan(taskPopped.get().getLock().lockedAt().toEpochMilli());
    }

    @Test
    void shouldProcessTaskSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());
        assertThat(taskPopped.isEmpty()).isFalse();

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());
        assertThat(taskProcessing.isEmpty()).isFalse();

        final Instant heartbeatAt1 = Instant.now();
        taskService.processTask(taskProcessing.get().getId(), heartbeatAt1, CLIENT);

        final Optional<Task> taskHeartbeat1 = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskHeartbeat1.isEmpty()).isFalse();
        assertThat(taskHeartbeat1.get().getStatus()).isEqualTo(Task.Status.PROCESSING);
        assertThat(taskHeartbeat1.get().getHeartbeatAt().toEpochMilli()).isEqualTo(heartbeatAt1.toEpochMilli());
        assertThat(taskHeartbeat1.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskProcessing.get().getUpdatedAt().toEpochMilli());
        assertThat(taskHeartbeat1.get().getLock().isLockedBy(CLIENT)).isTrue();
        assertThat(taskHeartbeat1.get().getLock().lockedAt().toEpochMilli()).isGreaterThan(taskProcessing.get().getLock().lockedAt().toEpochMilli());

        final Instant heartbeatAt2 = Instant.now();
        taskService.processTask(taskProcessing.get().getId(), heartbeatAt2, CLIENT);

        final Optional<Task> taskHeartbeat2 = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskHeartbeat2.isEmpty()).isFalse();
        assertThat(taskHeartbeat2.get().getStatus()).isEqualTo(Task.Status.PROCESSING);
        assertThat(taskHeartbeat2.get().getHeartbeatAt().toEpochMilli()).isEqualTo(heartbeatAt2.toEpochMilli());
        assertThat(taskHeartbeat2.get().getHeartbeatAt().toEpochMilli()).isGreaterThan(taskHeartbeat1.get().getHeartbeatAt().toEpochMilli());
        assertThat(taskHeartbeat2.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskHeartbeat1.get().getUpdatedAt().toEpochMilli());
        assertThat(taskHeartbeat2.get().getLock().isLockedBy(CLIENT)).isTrue();
        assertThat(taskHeartbeat2.get().getLock().lockedAt().toEpochMilli()).isGreaterThan(taskHeartbeat1.get().getLock().lockedAt().toEpochMilli());
    }

    @Test
    void shouldFinishedTaskWithoutChildrenSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT);
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskFinished.get().getFinishedAt().toEpochMilli()).isEqualTo(finishTask.finishedAt().toEpochMilli());
        assertThat(taskFinished.get().getHeartbeatAt().toEpochMilli()).isEqualTo(taskHeartbeat.get().getHeartbeatAt().toEpochMilli());
        assertThat(taskFinished.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskHeartbeat.get().getUpdatedAt().toEpochMilli());
        assertThat(taskFinished.get().getFinishBarrier()).isNull();
        assertThat(taskFinished.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldFinishedTaskWithOneChildrenWithoutWaitingTasksSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final TaskDto child = TestUtils.createTaskDto();
        final List<TaskDto> children = List.of(child);
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(
                taskHeartbeat.get().getId(),
                CLIENT,
                children
        );
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();

        final Optional<Barrier> finishBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishBarrier.isPresent()).isTrue();
        assertThat(finishBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        final List<UUID> childrenIdList = children.stream().map(x -> x.id()).toList();
        assertThat(finishBarrier.get().getWaitFor()).isEqualTo(childrenIdList);
        assertThat(finishBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishBarrier.get().isReleased()).isFalse();
        assertThat(finishBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishBarrier.get().getReleasedAt()).isNull();
        assertThat(finishBarrier.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> taskChild = taskRepository.get(child.id());
        assertThat(taskChild.isPresent()).isTrue();
        assertThat(taskChild.get().getId()).isEqualTo(child.id());
        assertThat(taskChild.get().getName()).isEqualTo(child.name());
        assertThat(taskChild.get().getTube()).isEqualTo(child.tube());
        assertThat(taskChild.get().getStatus()).isEqualTo(Task.Status.CREATED);
        assertThat(taskChild.get().getParentId()).isEqualTo(taskFinished.get().getId());
        assertThat(taskChild.get().getInput()).isEqualTo(child.input());
        assertThat(taskChild.get().getOutput()).isNull();
        assertThat(taskChild.get().isRoot()).isFalse();
        assertThat(taskChild.get().getStartBarrier()).isNull();
        assertThat(taskChild.get().getFinishBarrier()).isNull();
        assertThat(taskChild.get().getUpdatedAt()).isNotNull();
        assertThat(taskChild.get().getCreatedAt().toEpochMilli()).isEqualTo(child.createdAt().toEpochMilli());
        assertThat(taskChild.get().getScheduledAt()).isNull();
        assertThat(taskChild.get().getCanceledAt()).isNull();
        assertThat(taskChild.get().getStartedAt()).isNull();
        assertThat(taskChild.get().getHeartbeatAt()).isNull();
        assertThat(taskChild.get().getFinishedAt()).isNull();
        assertThat(taskChild.get().getFailedAt()).isNull();
        assertThat(taskChild.get().getAbortedAt()).isNull();
        assertThat(taskChild.get().getCompletedAt()).isNull();
        assertThat(taskChild.get().getFailures()).isEqualTo(0);
        assertThat(taskChild.get().getFailedReason()).isNull();
        assertThat(taskChild.get().getLock()).isEqualTo(Lock.free());
        assertThat(taskChild.get().getSettings()).isEqualTo(TaskSettings.getDefault());
    }

    @Test
    void shouldFinishedTaskWithOneChildrenWithWaitingTasksSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final UUID waitTaskId = UUID.randomUUID();
        final TaskDto child = TestUtils.createTaskDto(List.of(waitTaskId));
        final List<TaskDto> children = List.of(child);
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(
                taskHeartbeat.get().getId(),
                CLIENT,
                children
        );
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();

        final Optional<Barrier> finishBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishBarrier.isPresent()).isTrue();

        final Optional<Task> taskChild = taskRepository.get(child.id());
        assertThat(taskChild.get().getStartBarrier()).isNotNull();

        final Optional<Barrier> startBarrier = barrierRepository.get(taskChild.get().getStartBarrier());
        assertThat(startBarrier.isPresent()).isTrue();
        assertThat(startBarrier.get().getId()).isEqualTo(taskChild.get().getStartBarrier());
        assertThat(startBarrier.get().getTaskId()).isEqualTo(taskChild.get().getId());
        assertThat(startBarrier.get().getWaitFor()).isEqualTo(List.of(waitTaskId));
        assertThat(startBarrier.get().getType()).isEqualTo(Barrier.Type.START);
        assertThat(startBarrier.get().isReleased()).isFalse();
        assertThat(startBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(startBarrier.get().getCreatedAt()).isNotNull();
        assertThat(startBarrier.get().getReleasedAt()).isNull();
        assertThat(startBarrier.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldFinishedTaskWithTwoChildrenWithWaitingTasksSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final UUID waitTaskId1 = UUID.randomUUID();
        final UUID waitTaskId2 = UUID.randomUUID();
        final TaskDto child1 = TestUtils.createTaskDto(List.of(waitTaskId1));
        final TaskDto child2 = TestUtils.createTaskDto(List.of(waitTaskId2));
        final List<TaskDto> children = List.of(child1, child2);
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(
                taskHeartbeat.get().getId(),
                CLIENT,
                children
        );
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();

        final Optional<Barrier> finishBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishBarrier.isPresent()).isTrue();
        assertThat(finishBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));

        final Optional<Task> taskChild1 = taskRepository.get(child1.id());
        assertThat(taskChild1.get().getStartBarrier()).isNotNull();

        final Optional<Barrier> startBarrier1 = barrierRepository.get(taskChild1.get().getStartBarrier());
        assertThat(startBarrier1.isPresent()).isTrue();
        assertThat(startBarrier1.get().getId()).isEqualTo(taskChild1.get().getStartBarrier());
        assertThat(startBarrier1.get().getTaskId()).isEqualTo(taskChild1.get().getId());
        assertThat(startBarrier1.get().getWaitFor()).isEqualTo(List.of(waitTaskId1));
        assertThat(startBarrier1.get().getType()).isEqualTo(Barrier.Type.START);
        assertThat(startBarrier1.get().isReleased()).isFalse();
        assertThat(startBarrier1.get().getUpdatedAt()).isNotNull();
        assertThat(startBarrier1.get().getCreatedAt()).isNotNull();
        assertThat(startBarrier1.get().getReleasedAt()).isNull();
        assertThat(startBarrier1.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> taskChild2 = taskRepository.get(child2.id());
        assertThat(taskChild2.get().getStartBarrier()).isNotNull();

        final Optional<Barrier> startBarrier2 = barrierRepository.get(taskChild2.get().getStartBarrier());
        assertThat(startBarrier2.isPresent()).isTrue();
        assertThat(startBarrier2.get().getId()).isEqualTo(taskChild2.get().getStartBarrier());
        assertThat(startBarrier2.get().getTaskId()).isEqualTo(taskChild2.get().getId());
        assertThat(startBarrier2.get().getWaitFor()).isEqualTo(List.of(waitTaskId2));
        assertThat(startBarrier2.get().getType()).isEqualTo(Barrier.Type.START);
        assertThat(startBarrier2.get().isReleased()).isFalse();
        assertThat(startBarrier2.get().getUpdatedAt()).isNotNull();
        assertThat(startBarrier2.get().getCreatedAt()).isNotNull();
        assertThat(startBarrier2.get().getReleasedAt()).isNull();
        assertThat(startBarrier2.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldFailTaskSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        final Instant failedAt = Instant.now();
        final String failReasonMessage = "Task is failed";
        taskService.failTask(taskProcessing.get().getId(), failedAt, failReasonMessage, CLIENT);

        final Optional<Task> taskScheduled = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskScheduled.isPresent()).isTrue();
        assertThat(taskScheduled.get().getId()).isEqualTo(taskProcessing.get().getId());
        assertThat(taskScheduled.get().getName()).isEqualTo(taskProcessing.get().getName());
        assertThat(taskScheduled.get().getTube()).isEqualTo(taskProcessing.get().getTube());
        assertThat(taskScheduled.get().getStatus()).isEqualTo(Task.Status.SCHEDULED);
        assertThat(taskScheduled.get().getParentId()).isNull();
        assertThat(taskScheduled.get().getInput()).isEqualTo(taskProcessing.get().getInput());
        assertThat(taskScheduled.get().getOutput()).isNull();
        assertThat(taskScheduled.get().isRoot()).isTrue();
        assertThat(taskScheduled.get().getStartBarrier()).isNull();
        assertThat(taskScheduled.get().getFinishBarrier()).isNull();
        assertThat(taskScheduled.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskProcessing.get().getUpdatedAt().toEpochMilli());
        assertThat(taskScheduled.get().getCreatedAt().toEpochMilli()).isEqualTo(taskProcessing.get().getCreatedAt().toEpochMilli());
        assertThat(taskScheduled.get().getScheduledAt()).isNotNull();
        assertThat(taskScheduled.get().getScheduledAt().toEpochMilli()).isGreaterThan(taskProcessing.get().getScheduledAt().toEpochMilli());
        assertThat(taskScheduled.get().getCanceledAt()).isNull();
        assertThat(taskScheduled.get().getStartedAt()).isNull();
        assertThat(taskScheduled.get().getHeartbeatAt()).isNull();
        assertThat(taskScheduled.get().getFinishedAt()).isNull();
        assertThat(taskScheduled.get().getFailedAt().toEpochMilli()).isEqualTo(failedAt.toEpochMilli());
        assertThat(taskScheduled.get().getAbortedAt()).isNull();
        assertThat(taskScheduled.get().getCompletedAt()).isNull();
        assertThat(taskScheduled.get().getFailures()).isEqualTo(1);
        assertThat(taskScheduled.get().getFailedReason()).isEqualTo(failReasonMessage);
        assertThat(taskScheduled.get().getLock()).isEqualTo(Lock.free());
        assertThat(taskScheduled.get().getSettings()).isEqualTo(TaskSettings.getDefault());
    }

    @Test
    void shouldFailTaskAndRetryTimeoutSecondsSuccessfully() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(3, 15);
        final TaskDto taskDto = TestUtils.createTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        final Instant failedAt = Instant.now();
        final String failReasonMessage = "Task is failed";
        taskService.failTask(taskProcessing.get().getId(), failedAt, failReasonMessage, CLIENT);

        final Optional<Task> taskScheduled = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskScheduled.isPresent()).isTrue();
        assertThat(taskScheduled.get().getScheduledAt()).isNotNull();
        assertThat(taskScheduled.get().getScheduledAt().toEpochMilli()).isEqualTo(failedAt.plusSeconds(taskSettingsDto.failureRetryTimeoutSeconds()).toEpochMilli());
        assertThat(taskScheduled.get().getFailedAt().toEpochMilli()).isEqualTo(failedAt.toEpochMilli());
        assertThat(taskScheduled.get().getSettings()).isNotEqualTo(TaskSettings.getDefault());
        assertThat(taskScheduled.get().getSettings()).isEqualTo(new TaskSettings(taskSettingsDto.maxFailures(), taskSettingsDto.failureRetryTimeoutSeconds()));
    }

    @Test
    void shouldFailTaskAndPostponeTaskSuccessfully() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        final Instant failedAt = Instant.now();
        final String failReasonMessage = "Task is failed";
        taskService.failTask(taskProcessing.get().getId(), failedAt, failReasonMessage, CLIENT);

        final Optional<Task> taskScheduled = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskScheduled.isPresent()).isTrue();
        assertThat(taskScheduled.get().getScheduledAt().toEpochMilli()).isGreaterThan(Instant.now().toEpochMilli());

        final Optional<PopTaskDto> popFailedTask = tubeService.pop(taskScheduled.get().getTube(), CLIENT);
        assertThat(popFailedTask.isPresent()).isFalse();
    }

    @Test
    void shouldFailTaskWaitOneSecondAndPopTaskSuccessfully() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(3, 1);
        final TaskDto taskDto = TestUtils.createTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        final Instant failedAt = Instant.now();
        final String failReasonMessage = "Task is failed";
        taskService.failTask(taskProcessing.get().getId(), failedAt, failReasonMessage, CLIENT);

        final Optional<Task> taskScheduled = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskScheduled.isPresent()).isTrue();
        assertThat(taskScheduled.get().getScheduledAt().toEpochMilli()).isGreaterThan(Instant.now().toEpochMilli());

        final Optional<PopTaskDto> popFailedTask1 = tubeService.pop(taskScheduled.get().getTube(), CLIENT);
        assertThat(popFailedTask1.isPresent()).isFalse();

        TestUtils.await(taskSettingsDto.failureRetryTimeoutSeconds(), TimeUnit.SECONDS);

        final Optional<PopTaskDto> popFailedTask2 = tubeService.pop(taskScheduled.get().getTube(), CLIENT);
        assertThat(popFailedTask2.isPresent()).isTrue();
    }

    @Test
    void shouldFailTaskTwoTimesSuccessfully() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(2, 1);
        final TaskDto taskDto = TestUtils.createTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        final Instant failedAt1 = Instant.now();
        final String failReasonMessage1 = "Task is failed";
        taskService.failTask(taskProcessing.get().getId(), failedAt1, failReasonMessage1, CLIENT);

        final Optional<Task> taskScheduled1 = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskScheduled1.isPresent()).isTrue();
        assertThat(taskScheduled1.get().getScheduledAt().toEpochMilli()).isGreaterThan(Instant.now().toEpochMilli());

        TestUtils.await(taskSettingsDto.failureRetryTimeoutSeconds(), TimeUnit.SECONDS);

        final Optional<PopTaskDto> popFailedTask1 = tubeService.pop(taskPopped.get().getTube(), CLIENT);
        assertThat(popFailedTask1.isPresent()).isTrue();

        taskService.startTask(popFailedTask1.get().id(), Instant.now(), CLIENT);

        final Instant failedAt2 = Instant.now();
        final String failReasonMessage2 = "Task is failed2";
        taskService.failTask(popFailedTask1.get().id(), failedAt2, failReasonMessage2, CLIENT);

        final Optional<Task> taskScheduled2 = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskScheduled2.isPresent()).isTrue();
        assertThat(taskScheduled2.get().getScheduledAt().toEpochMilli()).isGreaterThan(Instant.now().toEpochMilli());

        TestUtils.await(taskSettingsDto.failureRetryTimeoutSeconds(), TimeUnit.SECONDS);

        final Optional<PopTaskDto> popFailedTask2 = tubeService.pop(taskPopped.get().getTube(), CLIENT);
        assertThat(popFailedTask2.isPresent()).isTrue();

        final Optional<Task> taskScheduled3 = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskScheduled3.isPresent()).isTrue();
        assertThat(taskScheduled3.get().getScheduledAt()).isNotNull();
        assertThat(taskScheduled3.get().getScheduledAt().toEpochMilli()).isEqualTo(failedAt2.plusSeconds(taskSettingsDto.failureRetryTimeoutSeconds()).toEpochMilli());
        assertThat(taskScheduled3.get().getFailedAt().toEpochMilli()).isEqualTo(failedAt2.toEpochMilli());
        assertThat(taskScheduled3.get().getFailures()).isEqualTo(2);
        assertThat(taskScheduled3.get().getFailedReason()).isEqualTo(failReasonMessage2);
        assertThat(taskScheduled3.get().getSettings()).isNotEqualTo(TaskSettings.getDefault());
        assertThat(taskScheduled3.get().getSettings()).isEqualTo(new TaskSettings(taskSettingsDto.maxFailures(), taskSettingsDto.failureRetryTimeoutSeconds()));
    }

    @Test
    void shouldAbortTaskSuccessfully() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(1, 1);
        final TaskDto taskDto = TestUtils.createTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        final Instant failedAt1 = Instant.now();
        final String failReasonMessage1 = "Task is failed";
        taskService.failTask(taskProcessing.get().getId(), failedAt1, failReasonMessage1, CLIENT);

        final Optional<Task> taskScheduled1 = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskScheduled1.isPresent()).isTrue();
        assertThat(taskScheduled1.get().getScheduledAt().toEpochMilli()).isGreaterThan(Instant.now().toEpochMilli());

        TestUtils.await(taskSettingsDto.failureRetryTimeoutSeconds(), TimeUnit.SECONDS);

        final Optional<PopTaskDto> popFailedTask1 = tubeService.pop(taskPopped.get().getTube(), CLIENT);
        assertThat(popFailedTask1.isPresent()).isTrue();

        taskService.startTask(popFailedTask1.get().id(), Instant.now(), CLIENT);

        final Optional<Task> taskFailed = taskRepository.get(popFailedTask1.get().id());
        assertThat(taskFailed.isPresent()).isTrue();

        final Instant failedAt2 = Instant.now();
        final String failReasonMessage2 = "Task is aborted";
        taskService.failTask(popFailedTask1.get().id(), failedAt2, failReasonMessage2, CLIENT);

        final Optional<Task> taskAborted = taskRepository.get(taskProcessing.get().getId());
        assertThat(taskAborted.isPresent()).isTrue();
        assertThat(taskAborted.get().getId()).isEqualTo(taskProcessing.get().getId());
        assertThat(taskAborted.get().getName()).isEqualTo(taskProcessing.get().getName());
        assertThat(taskAborted.get().getTube()).isEqualTo(taskProcessing.get().getTube());
        assertThat(taskAborted.get().getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(taskAborted.get().getParentId()).isNull();
        assertThat(taskAborted.get().getInput()).isEqualTo(taskProcessing.get().getInput());
        assertThat(taskAborted.get().getOutput()).isNull();
        assertThat(taskAborted.get().isRoot()).isTrue();
        assertThat(taskAborted.get().getStartBarrier()).isNull();
        assertThat(taskAborted.get().getFinishBarrier()).isNull();
        assertThat(taskAborted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFailed.get().getUpdatedAt().toEpochMilli());
        assertThat(taskAborted.get().getCreatedAt().toEpochMilli()).isEqualTo(taskFailed.get().getCreatedAt().toEpochMilli());
        assertThat(taskAborted.get().getScheduledAt().toEpochMilli()).isEqualTo(taskFailed.get().getScheduledAt().toEpochMilli());
        assertThat(taskAborted.get().getCanceledAt()).isNull();
        assertThat(taskAborted.get().getStartedAt()).isNotNull();
        assertThat(taskAborted.get().getHeartbeatAt()).isNull();
        assertThat(taskAborted.get().getFinishedAt()).isNull();
        assertThat(taskAborted.get().getFailedAt().toEpochMilli()).isEqualTo(failedAt2.toEpochMilli());
        assertThat(taskAborted.get().getAbortedAt()).isNotNull();
        assertThat(taskAborted.get().getAbortedAt().toEpochMilli()).isGreaterThan(taskFailed.get().getScheduledAt().toEpochMilli());
        assertThat(taskAborted.get().getCompletedAt()).isNull();
        assertThat(taskAborted.get().getFailures()).isEqualTo(1);
        assertThat(taskAborted.get().getFailedReason()).isEqualTo(failReasonMessage2);
        assertThat(taskAborted.get().getLock()).isEqualTo(Lock.free());
        assertThat(taskAborted.get().getSettings()).isEqualTo(new TaskSettings(taskSettingsDto.maxFailures(), taskSettingsDto.failureRetryTimeoutSeconds()));
    }
}