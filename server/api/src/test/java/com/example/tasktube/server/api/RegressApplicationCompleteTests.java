package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.application.models.TaskSettingsDto;
import com.example.tasktube.server.application.services.TaskService;
import com.example.tasktube.server.domain.enties.Barrier;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest(
        classes = RestApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class RegressApplicationCompleteTests extends AbstractRegressApplicationTests {

    @Test
    void shouldCompletedTaskWithoutChildren() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = createdTaskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT);
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList.get(0);
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        final Optional<Task> taskCompleted = taskRepository.get(finishedTaskId);
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(taskCompleted.get().getCompletedAt().toEpochMilli()).isEqualTo(completedAt.toEpochMilli());
        assertThat(taskCompleted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskCompleted.get().getFinishBarrier()).isNull();
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldCompletedTaskScheduleFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = createdTaskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT);
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList.get(0);
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        assertThatThrownBy(() -> taskService.scheduleTask(taskId, Instant.now(), CLIENT));
    }

    @Test
    void shouldCompletedTaskStartFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = createdTaskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT);
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList.get(0);
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        assertThatThrownBy(() -> taskService.startTask(taskId, Instant.now(), CLIENT));
    }

    @Test
    void shouldCompletedTaskProcessFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = createdTaskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT);
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList.get(0);
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        assertThatThrownBy(() -> taskService.processTask(taskId, Instant.now(), CLIENT));
    }

    @Test
    void shouldCompletedTaskFailFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = createdTaskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT);
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList.get(0);
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        assertThatThrownBy(() -> taskService.failTask(taskId, Instant.now(), "Fail reason", CLIENT));
    }

    @Test
    void shouldCompletedTaskFinishFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = createdTaskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT);
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList.get(0);
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        assertThatThrownBy(() -> taskService.finishTask(TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT)));
    }

    @Test
    void shouldCompletedTaskTwoTimeFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = createdTaskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT);
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList.get(0);
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        assertThatThrownBy(() -> taskService.completeTask(taskId, Instant.now(), CLIENT));
    }

    @Test
    void shouldWaitReleaseFinishBarrierTaskWithChildWithoutWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList1 = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = createdTaskIdList1.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(popTask.get().id());

        final PushTaskDto child = TestUtils.createPushTaskDto();
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT, List.of(child));
        taskService.finishTask(finishTask);

        Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();

        final Optional<Barrier> finishedBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList.isEmpty()).isFalse();
        assertThat(finishedTaskIdList.size()).isEqualTo(1);
        assertThat(finishedTaskIdList.getFirst()).isEqualTo(taskFinished.get().getId());

        taskFinished = taskRepository.get(finishedTaskIdList.getFirst());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();
        assertThat(taskFinished.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        taskService.completeTask(finishedTaskIdList.getFirst(), Instant.now(), instanceIdProvider.get());

        taskFinished = taskRepository.get(finishedTaskIdList.getFirst());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();
        assertThat(taskFinished.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldCompletedTaskWithChildWithoutWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList1 = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = createdTaskIdList1.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(popTask.get().id());

        final PushTaskDto child = TestUtils.createPushTaskDto();
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT, List.of(child));
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();

        Optional<Barrier> finishedBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> createdTaskIdList2 = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());
        final UUID createdChildTaskId = createdTaskIdList2.getFirst();
        assertThat(child.id()).isEqualTo(createdChildTaskId);

        taskService.scheduleTask(createdChildTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask = tubeService.pop(child.tube(), CLIENT);

        taskService.startTask(popChildTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask.get().id(), Instant.now(), CLIENT);

        final FinishTaskDto childFinishTask = TestUtils.createFinishTaskDto(popChildTask.get().id(), CLIENT);
        taskService.finishTask(childFinishTask);

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList.isEmpty()).isFalse();
        assertThat(finishedTaskIdList.size()).isEqualTo(2);
        for (final UUID finishedTaskId : finishedTaskIdList) {
            taskService.completeTask(finishedTaskId, Instant.now(), instanceIdProvider.get());
        }

        final Optional<Task> childTaskCompleted = taskRepository.get(childFinishTask.taskId());
        assertThat(childTaskCompleted.isEmpty()).isFalse();
        assertThat(childTaskCompleted.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTaskCompleted.get().getCompletedAt()).isNotNull();
        assertThat(childTaskCompleted.get().getFinishBarrier()).isNull();
        assertThat(childTaskCompleted.get().getLock()).isEqualTo(Lock.free());

        Optional<Task> taskCompleted = taskRepository.get(finishTask.taskId());
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskCompleted.get().getCompletedAt()).isNull();
        assertThat(taskCompleted.get().getAbortedAt()).isNull();
        assertThat(taskCompleted.get().getFinishBarrier()).isNotNull();
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedBarrierIdList = jobService.getBarrierIdList(10, instanceIdProvider.get());
        assertThat(finishedBarrierIdList.isEmpty()).isFalse();
        assertThat(finishedBarrierIdList.size()).isEqualTo(1);
        finishedBarrier = barrierRepository.get(finishedBarrierIdList.getFirst());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskCompleted.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskCompleted.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        barrierService.releaseBarrier(finishedBarrierIdList.getFirst(), instanceIdProvider.get());

        finishedBarrier = barrierRepository.get(taskCompleted.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskCompleted.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskCompleted.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isTrue();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNotNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedTaskIdList2 = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList2.isEmpty()).isFalse();
        assertThat(finishedTaskIdList2.size()).isEqualTo(1);
        assertThat(finishedTaskIdList2.getFirst()).isEqualTo(taskCompleted.get().getId());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList2.getFirst();
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        taskCompleted = taskRepository.get(finishedTaskId);
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(taskCompleted.get().getCompletedAt().toEpochMilli()).isEqualTo(completedAt.toEpochMilli());
        assertThat(taskCompleted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskCompleted.get().getFinishBarrier()).isNotNull();
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldCompletedTaskWithChildrenWithoutWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList1 = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        taskService.scheduleTask(createdTaskIdList1.getFirst(), Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final PushTaskDto child1 = TestUtils.createPushTaskDto();
        final PushTaskDto child2 = TestUtils.createPushTaskDto();
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(popTask.get().id(), CLIENT, List.of(child1, child2));
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();

        Optional<Barrier> finishedBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> createdChildrenTaskIdList2 = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());
        assertThat(createdChildrenTaskIdList2.isEmpty()).isFalse();
        assertThat(createdChildrenTaskIdList2.size()).isEqualTo(2);
        for (final UUID createdChildTaskId : createdChildrenTaskIdList2) {
            taskService.scheduleTask(createdChildTaskId, Instant.now(), instanceIdProvider.get());

            final Optional<PopTaskDto> popChildTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

            taskService.startTask(popChildTask.get().id(), Instant.now(), CLIENT);

            taskService.processTask(popChildTask.get().id(), Instant.now(), CLIENT);

            final FinishTaskDto childFinishTask = TestUtils.createFinishTaskDto(popChildTask.get().id(), CLIENT);
            taskService.finishTask(childFinishTask);
        }

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList.isEmpty()).isFalse();
        assertThat(finishedTaskIdList.size()).isEqualTo(3);
        for (final UUID finishedTaskId : finishedTaskIdList) {
            taskService.completeTask(finishedTaskId, Instant.now(), instanceIdProvider.get());
        }

        final Optional<Task> childTask1Completed = taskRepository.get(child1.id());
        assertThat(childTask1Completed.isEmpty()).isFalse();
        assertThat(childTask1Completed.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTask1Completed.get().getCompletedAt()).isNotNull();
        assertThat(childTask1Completed.get().getFinishBarrier()).isNull();
        assertThat(childTask1Completed.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> childTask2Created = taskRepository.get(child2.id());
        assertThat(childTask2Created.isEmpty()).isFalse();
        assertThat(childTask2Created.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTask2Created.get().getCompletedAt()).isNotNull();
        assertThat(childTask2Created.get().getFinishBarrier()).isNull();
        assertThat(childTask2Created.get().getLock()).isEqualTo(Lock.free());

        Optional<Task> taskCompleted = taskRepository.get(finishTask.taskId());
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskCompleted.get().getCompletedAt()).isNull();
        assertThat(taskCompleted.get().getAbortedAt()).isNull();
        assertThat(taskCompleted.get().getFinishBarrier()).isNotNull();
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedBarrierIdList = jobService.getBarrierIdList(10, instanceIdProvider.get());
        assertThat(finishedBarrierIdList.isEmpty()).isFalse();
        assertThat(finishedBarrierIdList.size()).isEqualTo(1);
        finishedBarrier = barrierRepository.get(finishedBarrierIdList.getFirst());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskCompleted.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskCompleted.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        barrierService.releaseBarrier(finishedBarrierIdList.getFirst(), instanceIdProvider.get());

        finishedBarrier = barrierRepository.get(taskCompleted.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskCompleted.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskCompleted.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isTrue();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNotNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedTaskIdList2 = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList2.isEmpty()).isFalse();
        assertThat(finishedTaskIdList2.size()).isEqualTo(1);
        assertThat(finishedTaskIdList2.getFirst()).isEqualTo(taskCompleted.get().getId());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList2.getFirst();
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        taskCompleted = taskRepository.get(finishedTaskId);
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(taskCompleted.get().getCompletedAt().toEpochMilli()).isEqualTo(completedAt.toEpochMilli());
        assertThat(taskCompleted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskCompleted.get().getFinishBarrier()).isNotNull();
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldAbortedTaskWithChildrenWithoutWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList1 = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        taskService.scheduleTask(createdTaskIdList1.getFirst(), Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final PushTaskDto child1 = TestUtils.createPushTaskDto(new TaskSettingsDto(0, 1));
        final PushTaskDto child2 = TestUtils.createPushTaskDto(new TaskSettingsDto(0, 1));
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(popTask.get().id(), CLIENT, List.of(child1, child2));
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();

        Optional<Barrier> finishedBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> createdChildrenTaskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());
        assertThat(createdChildrenTaskIdList.isEmpty()).isFalse();
        assertThat(createdChildrenTaskIdList.size()).isEqualTo(2);

        final UUID createdChild1 = createdChildrenTaskIdList.getFirst();

        taskService.scheduleTask(createdChild1, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask1 = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        final FinishTaskDto childFinishTask = TestUtils.createFinishTaskDto(popChildTask1.get().id(), CLIENT);
        taskService.finishTask(childFinishTask);

        final UUID createdChild2 = createdChildrenTaskIdList.getLast();

        taskService.scheduleTask(createdChild2, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask2 = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popChildTask2.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask2.get().id(), Instant.now(), CLIENT);

        final Instant failedAt = Instant.now();
        final String failedReason = "Child task is failed.";
        taskService.failTask(popChildTask2.get().id(), failedAt, failedReason, CLIENT);

        final List<UUID> finishedTaskIdList = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList.isEmpty()).isFalse();
        assertThat(finishedTaskIdList.size()).isEqualTo(2);
        for (final UUID finishedTaskId : finishedTaskIdList) {
            taskService.completeTask(finishedTaskId, Instant.now(), instanceIdProvider.get());
        }

        final Optional<Task> childTask1Completed = taskRepository.get(child1.id());
        assertThat(childTask1Completed.isEmpty()).isFalse();
        assertThat(childTask1Completed.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTask1Completed.get().getCompletedAt()).isNotNull();
        assertThat(childTask1Completed.get().getFinishBarrier()).isNull();
        assertThat(childTask1Completed.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> childTask2Created = taskRepository.get(child2.id());
        assertThat(childTask2Created.isEmpty()).isFalse();
        assertThat(childTask2Created.get().getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(childTask2Created.get().getCompletedAt()).isNull();
        assertThat(childTask2Created.get().getFinishedAt()).isNull();
        assertThat(childTask2Created.get().getAbortedAt()).isNotNull();
        assertThat(childTask2Created.get().getFailedAt().toEpochMilli()).isEqualTo(failedAt.toEpochMilli());
        assertThat(childTask2Created.get().getFailedReason()).isEqualTo(failedReason);
        assertThat(childTask2Created.get().getFinishBarrier()).isNull();
        assertThat(childTask2Created.get().getLock()).isEqualTo(Lock.free());

        Optional<Task> taskCompleted = taskRepository.get(finishTask.taskId());
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskCompleted.get().getCompletedAt()).isNull();
        assertThat(taskCompleted.get().getAbortedAt()).isNull();
        assertThat(taskCompleted.get().getFinishBarrier()).isNotNull();
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedBarrierIdList = jobService.getBarrierIdList(10, instanceIdProvider.get());
        assertThat(finishedBarrierIdList.isEmpty()).isFalse();
        assertThat(finishedBarrierIdList.size()).isEqualTo(1);
        finishedBarrier = barrierRepository.get(finishedBarrierIdList.getFirst());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskCompleted.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskCompleted.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        barrierService.releaseBarrier(finishedBarrierIdList.getFirst(), instanceIdProvider.get());

        finishedBarrier = barrierRepository.get(taskCompleted.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskCompleted.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskCompleted.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isTrue();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNotNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedTaskIdList2 = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList2.isEmpty()).isFalse();
        assertThat(finishedTaskIdList2.size()).isEqualTo(1);
        assertThat(finishedTaskIdList2.getFirst()).isEqualTo(taskCompleted.get().getId());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList2.getFirst();
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        taskCompleted = taskRepository.get(finishedTaskId);
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(taskCompleted.get().getFailedReason()).isEqualTo(TaskService.CHILDREN_ARE_FINALIZED);
        assertThat(taskCompleted.get().getAbortedAt().toEpochMilli()).isEqualTo(completedAt.toEpochMilli());
        assertThat(taskCompleted.get().getCompletedAt()).isNull();
        assertThat(taskCompleted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskCompleted.get().getFinishBarrier()).isNotNull();
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldAbortedParentTaskWithChildrenWithWaitingTasksAndCancelChild() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList1 = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        taskService.scheduleTask(createdTaskIdList1.getFirst(), Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final PushTaskDto child1 = TestUtils.createPushTaskDto(new TaskSettingsDto(0, 1));
        final PushTaskDto child2 = TestUtils.createPushTaskDto(List.of(child1.id()), new TaskSettingsDto(0, 1));
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(popTask.get().id(), CLIENT, List.of(child1, child2));
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();

        Optional<Barrier> finishedBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> childTaskCreated = taskRepository.get(child2.id());
        assertThat(childTaskCreated.isEmpty()).isFalse();
        assertThat(childTaskCreated.get().getStartBarrier()).isNotNull();

        Optional<Barrier> startChildBarrier = barrierRepository.get(childTaskCreated.get().getStartBarrier());
        assertThat(startChildBarrier.isPresent()).isTrue();
        assertThat(startChildBarrier.get().getId()).isEqualTo(childTaskCreated.get().getStartBarrier());
        assertThat(startChildBarrier.get().getTaskId()).isEqualTo(childTaskCreated.get().getId());
        assertThat(startChildBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id()));
        assertThat(startChildBarrier.get().getType()).isEqualTo(Barrier.Type.START);
        assertThat(startChildBarrier.get().isReleased()).isFalse();
        assertThat(startChildBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(startChildBarrier.get().getCreatedAt()).isNotNull();
        assertThat(startChildBarrier.get().getReleasedAt()).isNull();
        assertThat(startChildBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> createdChildrenTaskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());
        assertThat(createdChildrenTaskIdList.isEmpty()).isFalse();
        assertThat(createdChildrenTaskIdList.size()).isEqualTo(2);

        final UUID createdChild1 = createdChildrenTaskIdList.getFirst();

        taskService.scheduleTask(createdChild1, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask1 = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        final Instant failedAt = Instant.now();
        final String failedReason = "Child task is failed.";
        taskService.failTask(popChildTask1.get().id(), failedAt, failedReason, CLIENT);

        final List<UUID> barrierIdList = jobService.getBarrierIdList(10, instanceIdProvider.get());
        assertThat(barrierIdList.isEmpty()).isFalse();
        assertThat(barrierIdList.size()).isEqualTo(2);

        barrierService.releaseBarrier(barrierIdList.getFirst(), instanceIdProvider.get());
        barrierService.releaseBarrier(barrierIdList.getLast(), instanceIdProvider.get());

        startChildBarrier = barrierRepository.get(childTaskCreated.get().getStartBarrier());
        assertThat(startChildBarrier.isPresent()).isTrue();
        assertThat(startChildBarrier.get().getId()).isEqualTo(childTaskCreated.get().getStartBarrier());
        assertThat(startChildBarrier.get().getTaskId()).isEqualTo(childTaskCreated.get().getId());
        assertThat(startChildBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id()));
        assertThat(startChildBarrier.get().getType()).isEqualTo(Barrier.Type.START);
        assertThat(startChildBarrier.get().isReleased()).isTrue();
        assertThat(startChildBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(startChildBarrier.get().getCreatedAt()).isNotNull();
        assertThat(startChildBarrier.get().getReleasedAt()).isNotNull();
        assertThat(startChildBarrier.get().getLock()).isEqualTo(Lock.free());

        final UUID createdChild2 = createdChildrenTaskIdList.getLast();

        final Instant canceledAt = Instant.now();
        taskService.scheduleTask(createdChild2, canceledAt, instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask2 = tubeService.pop(pushTaskDto.tube(), CLIENT);
        assertThat(popChildTask2.isEmpty()).isTrue();

        final Optional<Task> childTask2Canceled = taskRepository.get(createdChild2);
        assertThat(childTask2Canceled.isEmpty()).isFalse();
        assertThat(childTask2Canceled.get().getStatus()).isEqualTo(Task.Status.CANCELED);
        assertThat(childTask2Canceled.get().getCompletedAt()).isNull();
        assertThat(childTask2Canceled.get().getFinishedAt()).isNull();
        assertThat(childTask2Canceled.get().getCanceledAt().toEpochMilli()).isEqualTo(canceledAt.toEpochMilli());
        assertThat(childTask2Canceled.get().getFailedAt()).isNull();
        assertThat(childTask2Canceled.get().getFailedReason()).isEqualTo(TaskService.WAITING_TASKS_ARE_FINALIZED);
        assertThat(childTask2Canceled.get().getFinishBarrier()).isNull();
        assertThat(childTask2Canceled.get().getStartBarrier()).isNotNull();
        assertThat(childTask2Canceled.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> childTask1Aborted = taskRepository.get(child1.id());
        assertThat(childTask1Aborted.isEmpty()).isFalse();
        assertThat(childTask1Aborted.get().getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(childTask1Aborted.get().getAbortedAt()).isNotNull();
        assertThat(childTask1Aborted.get().getFinishBarrier()).isNull();
        assertThat(childTask1Aborted.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedBarrierIdList = jobService.getBarrierIdList(10, instanceIdProvider.get());
        assertThat(finishedBarrierIdList.isEmpty()).isFalse();
        assertThat(finishedBarrierIdList.size()).isEqualTo(1);
        finishedBarrier = barrierRepository.get(finishedBarrierIdList.getFirst());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        barrierService.releaseBarrier(finishedBarrierIdList.getFirst(), instanceIdProvider.get());

        finishedBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isTrue();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNotNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedTaskIdList2 = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList2.isEmpty()).isFalse();
        assertThat(finishedTaskIdList2.size()).isEqualTo(1);
        assertThat(finishedTaskIdList2.getFirst()).isEqualTo(taskFinished.get().getId());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList2.getFirst();
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        final Optional<Task> taskCompleted = taskRepository.get(finishedTaskId);
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(taskCompleted.get().getFailedReason()).isEqualTo(TaskService.CHILDREN_ARE_FINALIZED);
        assertThat(taskCompleted.get().getAbortedAt().toEpochMilli()).isEqualTo(completedAt.toEpochMilli());
        assertThat(taskCompleted.get().getCompletedAt()).isNull();
        assertThat(taskCompleted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskCompleted.get().getFinishBarrier()).isNotNull();
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldCompletedParentTaskWithChildrenWithWaitingTasksAndCompleteChild() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> createdTaskIdList1 = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        taskService.scheduleTask(createdTaskIdList1.getFirst(), Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final PushTaskDto child1 = TestUtils.createPushTaskDto(new TaskSettingsDto(0, 1));
        final PushTaskDto child2 = TestUtils.createPushTaskDto(List.of(child1.id()), new TaskSettingsDto(0, 1));
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(popTask.get().id(), CLIENT, List.of(child1, child2));
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getFinishBarrier()).isNotNull();

        Optional<Barrier> finishedBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> childTaskCreated = taskRepository.get(child2.id());
        assertThat(childTaskCreated.isEmpty()).isFalse();
        assertThat(childTaskCreated.get().getStartBarrier()).isNotNull();

        Optional<Barrier> startChildBarrier = barrierRepository.get(childTaskCreated.get().getStartBarrier());
        assertThat(startChildBarrier.isPresent()).isTrue();
        assertThat(startChildBarrier.get().getId()).isEqualTo(childTaskCreated.get().getStartBarrier());
        assertThat(startChildBarrier.get().getTaskId()).isEqualTo(childTaskCreated.get().getId());
        assertThat(startChildBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id()));
        assertThat(startChildBarrier.get().getType()).isEqualTo(Barrier.Type.START);
        assertThat(startChildBarrier.get().isReleased()).isFalse();
        assertThat(startChildBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(startChildBarrier.get().getCreatedAt()).isNotNull();
        assertThat(startChildBarrier.get().getReleasedAt()).isNull();
        assertThat(startChildBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> createdChildrenTaskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());
        assertThat(createdChildrenTaskIdList.isEmpty()).isFalse();
        assertThat(createdChildrenTaskIdList.size()).isEqualTo(2);

        final UUID createdChild1 = createdChildrenTaskIdList.getFirst();

        taskService.scheduleTask(createdChild1, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask1 = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        final FinishTaskDto childFinishTask1 = TestUtils.createFinishTaskDto(popChildTask1.get().id(), CLIENT);
        taskService.finishTask(childFinishTask1);

        final List<UUID> finishedTaskIdList1 = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList1.isEmpty()).isFalse();
        assertThat(finishedTaskIdList1.size()).isEqualTo(2);
        assertThat(finishedTaskIdList1).hasSameElementsAs(List.of(childFinishTask1.taskId(), taskFinished.get().getId()));

        for (final UUID task : finishedTaskIdList1) {
            taskService.completeTask(task, Instant.now(), instanceIdProvider.get());
        }

        final List<UUID> barrierIdList = jobService.getBarrierIdList(10, instanceIdProvider.get());
        assertThat(barrierIdList.isEmpty()).isFalse();
        assertThat(barrierIdList.size()).isEqualTo(2);

        barrierService.releaseBarrier(barrierIdList.getFirst(), instanceIdProvider.get());
        barrierService.releaseBarrier(barrierIdList.getLast(), instanceIdProvider.get());

        startChildBarrier = barrierRepository.get(childTaskCreated.get().getStartBarrier());
        assertThat(startChildBarrier.isPresent()).isTrue();
        assertThat(startChildBarrier.get().getId()).isEqualTo(childTaskCreated.get().getStartBarrier());
        assertThat(startChildBarrier.get().getTaskId()).isEqualTo(childTaskCreated.get().getId());
        assertThat(startChildBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id()));
        assertThat(startChildBarrier.get().getType()).isEqualTo(Barrier.Type.START);
        assertThat(startChildBarrier.get().isReleased()).isTrue();
        assertThat(startChildBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(startChildBarrier.get().getCreatedAt()).isNotNull();
        assertThat(startChildBarrier.get().getReleasedAt()).isNotNull();
        assertThat(startChildBarrier.get().getLock()).isEqualTo(Lock.free());

        final UUID createdChild2 = createdChildrenTaskIdList.getLast();

        final Instant scheduledAt = Instant.now();
        taskService.scheduleTask(createdChild2, scheduledAt, instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask2 = tubeService.pop(pushTaskDto.tube(), CLIENT);
        assertThat(popChildTask2.isEmpty()).isFalse();

        taskService.startTask(popChildTask2.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask2.get().id(), Instant.now(), CLIENT);

        final FinishTaskDto childFinishTask2 = TestUtils.createFinishTaskDto(popChildTask2.get().id(), CLIENT);
        taskService.finishTask(childFinishTask2);

        final List<UUID> finishedTaskIdList2 = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList2.isEmpty()).isFalse();
        assertThat(finishedTaskIdList2.size()).isEqualTo(2);
        assertThat(finishedTaskIdList2).hasSameElementsAs(List.of(childFinishTask2.taskId(), taskFinished.get().getId()));

        for (final UUID task : finishedTaskIdList2) {
            taskService.completeTask(task, Instant.now(), instanceIdProvider.get());
        }

        final Optional<Task> childTask1Completed = taskRepository.get(child1.id());
        assertThat(childTask1Completed.isEmpty()).isFalse();
        assertThat(childTask1Completed.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTask1Completed.get().getAbortedAt()).isNull();
        assertThat(childTask1Completed.get().getCompletedAt()).isNotNull();
        assertThat(childTask1Completed.get().getFinishBarrier()).isNull();
        assertThat(childTask1Completed.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> childTask2Scheduled = taskRepository.get(createdChild2);
        assertThat(childTask2Scheduled.isEmpty()).isFalse();
        assertThat(childTask2Scheduled.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTask2Scheduled.get().getAbortedAt()).isNull();
        assertThat(childTask2Scheduled.get().getCompletedAt()).isNotNull();
        assertThat(childTask2Scheduled.get().getFinishedAt()).isNotNull();
        assertThat(childTask2Scheduled.get().getScheduledAt().toEpochMilli()).isEqualTo(scheduledAt.toEpochMilli());
        assertThat(childTask2Scheduled.get().getFailedAt()).isNull();
        assertThat(childTask2Scheduled.get().getFailedReason()).isNull();
        assertThat(childTask2Scheduled.get().getFinishBarrier()).isNull();
        assertThat(childTask2Scheduled.get().getStartBarrier()).isNotNull();
        assertThat(childTask2Scheduled.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedBarrierIdList = jobService.getBarrierIdList(10, instanceIdProvider.get());
        assertThat(finishedBarrierIdList.isEmpty()).isFalse();
        assertThat(finishedBarrierIdList.size()).isEqualTo(1);
        finishedBarrier = barrierRepository.get(finishedBarrierIdList.getFirst());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isFalse();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNull();
        assertThat(finishedBarrier.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        barrierService.releaseBarrier(finishedBarrierIdList.getFirst(), instanceIdProvider.get());

        finishedBarrier = barrierRepository.get(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.isPresent()).isTrue();
        assertThat(finishedBarrier.get().getId()).isEqualTo(taskFinished.get().getFinishBarrier());
        assertThat(finishedBarrier.get().getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.get().getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.get().getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.get().isReleased()).isTrue();
        assertThat(finishedBarrier.get().getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.get().getReleasedAt()).isNotNull();
        assertThat(finishedBarrier.get().getLock()).isEqualTo(Lock.free());

        final List<UUID> finishedTaskIdList3 = jobService.getTaskIdList(Task.Status.FINISHED, 10, instanceIdProvider.get());
        assertThat(finishedTaskIdList3.isEmpty()).isFalse();
        assertThat(finishedTaskIdList3.size()).isEqualTo(1);
        assertThat(finishedTaskIdList3.getFirst()).isEqualTo(taskFinished.get().getId());

        final Instant completedAt = Instant.now();
        final UUID finishedTaskId = finishedTaskIdList3.getFirst();
        taskService.completeTask(finishedTaskId, completedAt, instanceIdProvider.get());

        final Optional<Task> taskCompleted = taskRepository.get(finishedTaskId);
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(taskCompleted.get().getFailedReason()).isNull();
        assertThat(taskCompleted.get().getCompletedAt().toEpochMilli()).isEqualTo(completedAt.toEpochMilli());
        assertThat(taskCompleted.get().getAbortedAt()).isNull();
        assertThat(taskCompleted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskCompleted.get().getFinishBarrier()).isNotNull();
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());
    }
}