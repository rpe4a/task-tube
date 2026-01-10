package com.example.tasktube.server.api.regress;

import com.example.tasktube.server.api.RestApiApplication;
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

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

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

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier finisBarrier = barrierRepository.getByTaskId(taskId).getLast();
        barrierService.release(finisBarrier.getId(), instanceIdProvider.get());

        final Optional<Task> taskCompleted = taskRepository.get(taskFinished.get().getId());
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(taskCompleted.get().getCompletedAt()).isNotNull();
        assertThat(taskCompleted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldCompletedTaskStartFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

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

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier finisBarrier = barrierRepository.getByTaskId(taskId).getLast();
        barrierService.release(finisBarrier.getId(), instanceIdProvider.get());

        assertThatThrownBy(() -> taskService.startTask(taskId, Instant.now(), CLIENT));
    }

    @Test
    void shouldCompletedTaskProcessFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

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

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier finisBarrier = barrierRepository.getByTaskId(taskId).getLast();
        barrierService.release(finisBarrier.getId(), instanceIdProvider.get());

        assertThatThrownBy(() -> taskService.processTask(taskId, Instant.now(), CLIENT));
    }

    @Test
    void shouldCompletedTaskFailFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

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

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier finisBarrier = barrierRepository.getByTaskId(taskId).getLast();
        barrierService.release(finisBarrier.getId(), instanceIdProvider.get());

        assertThatThrownBy(() -> taskService.failTask(taskId, Instant.now(), "Fail reason", CLIENT));
    }

    @Test
    void shouldCompletedTaskFinishFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

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

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier finisBarrier = barrierRepository.getByTaskId(taskId).getLast();
        barrierService.release(finisBarrier.getId(), instanceIdProvider.get());

        assertThatThrownBy(() -> taskService.finishTask(TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT)));
    }

    @Test
    void shouldWaitReleaseFinishBarrierTaskWithChildWithoutWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(popTask.get().id());

        final PushTaskDto child = TestUtils.createPushTaskDto();
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT, List.of(child));
        taskService.finishTask(finishTask);

        Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        Barrier finisBarrier = barrierRepository.getByTaskId(taskId).getLast();
        assertThat(finisBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finisBarrier.getWaitFor()).isEqualTo(List.of(child.id()));
        assertThat(finisBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finisBarrier.isReleased()).isFalse();
        assertThat(finisBarrier.getUpdatedAt()).isNotNull();
        assertThat(finisBarrier.getCreatedAt()).isNotNull();
        assertThat(finisBarrier.getReleasedAt()).isNull();
        assertThat(finisBarrier.getLock()).isEqualTo(Lock.free());

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        barrierService.release(finisBarrier.getId(), instanceIdProvider.get());

        finisBarrier = barrierRepository.getByTaskId(taskId).getLast();
        assertThat(finisBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finisBarrier.getWaitFor()).isEqualTo(List.of(child.id()));
        assertThat(finisBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finisBarrier.isReleased()).isFalse();
        assertThat(finisBarrier.getUpdatedAt()).isNotNull();
        assertThat(finisBarrier.getCreatedAt()).isNotNull();
        assertThat(finisBarrier.getReleasedAt()).isNull();
        assertThat(finisBarrier.getLock()).isEqualTo(Lock.free());

        taskFinished = taskRepository.get(taskFinished.get().getId());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskFinished.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldCompletedTaskWithChildWithoutWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(popTask.get().id());

        final PushTaskDto child = TestUtils.createPushTaskDto();
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT, List.of(child));
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        final Barrier finisBarrier = barrierRepository.getByTaskId(taskId).getLast();
        assertThat(finisBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finisBarrier.getWaitFor()).isEqualTo(List.of(child.id()));
        assertThat(finisBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finisBarrier.isReleased()).isFalse();
        assertThat(finisBarrier.getUpdatedAt()).isNotNull();
        assertThat(finisBarrier.getCreatedAt()).isNotNull();
        assertThat(finisBarrier.getReleasedAt()).isNull();
        assertThat(finisBarrier.getLock()).isEqualTo(Lock.free());

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier finishChildBarrier = barrierRepository.getByTaskId(child.id()).getFirst();
        barrierService.release(finishChildBarrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask = tubeService.pop(child.tube(), CLIENT);

        taskService.startTask(popChildTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask.get().id(), Instant.now(), CLIENT);

        final FinishTaskDto childFinishTask = TestUtils.createFinishTaskDto(popChildTask.get().id(), CLIENT);
        taskService.finishTask(childFinishTask);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startChildBarrier = barrierRepository.getByTaskId(childFinishTask.taskId()).getLast();

        barrierService.release(startChildBarrier.getId(), instanceIdProvider.get());

        final Optional<Task> childTaskCompleted = taskRepository.get(childFinishTask.taskId());
        assertThat(childTaskCompleted.isEmpty()).isFalse();
        assertThat(childTaskCompleted.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTaskCompleted.get().getCompletedAt()).isNotNull();
        assertThat(childTaskCompleted.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> taskCompleted = taskRepository.get(finishTask.taskId());
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskCompleted.get().getCompletedAt()).isNull();
        assertThat(taskCompleted.get().getAbortedAt()).isNull();
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());

        Barrier finishedBarrier = barrierRepository.getByTaskId(taskCompleted.get().getId()).getLast();
        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskCompleted.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isFalse();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNull();
        assertThat(finishedBarrier.getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        barrierService.release(finishedBarrier.getId(), instanceIdProvider.get());

        finishedBarrier = barrierRepository.getByTaskId(taskCompleted.get().getId()).getLast();
        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskCompleted.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isTrue();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNotNull();
        assertThat(finishedBarrier.getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldCompletedTaskWithChildrenWithoutWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final PushTaskDto child1 = TestUtils.createPushTaskDto();
        final PushTaskDto child2 = TestUtils.createPushTaskDto();
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(popTask.get().id(), CLIENT, List.of(child1, child2));
        taskService.finishTask(finishTask);

        Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        Barrier finishedBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).getLast();
        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isFalse();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNull();
        assertThat(finishedBarrier.getLock()).isEqualTo(Lock.free());

        final List<UUID> barriers = jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        for (final UUID barrier : barriers) {
            barrierService.release(barrier, instanceIdProvider.get());
        }

        do {
            final Optional<PopTaskDto> popChildTask = tubeService.pop(pushTaskDto.tube(), CLIENT);
            if (popChildTask.isPresent()) {
                taskService.startTask(popChildTask.get().id(), Instant.now(), CLIENT);

                taskService.processTask(popChildTask.get().id(), Instant.now(), CLIENT);

                final FinishTaskDto childFinishTask = TestUtils.createFinishTaskDto(popChildTask.get().id(), CLIENT);
                taskService.finishTask(childFinishTask);

                jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
                final Barrier finishedChildBarrier = barrierRepository.getByTaskId(popChildTask.get().id()).getLast();
                barrierService.release(finishedChildBarrier.getId(), instanceIdProvider.get());
            } else {
                break;
            }
        } while (true);

        final Optional<Task> childTask1Completed = taskRepository.get(child1.id());
        assertThat(childTask1Completed.isEmpty()).isFalse();
        assertThat(childTask1Completed.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTask1Completed.get().getCompletedAt()).isNotNull();
        assertThat(childTask1Completed.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> childTask2Completed = taskRepository.get(child2.id());
        assertThat(childTask2Completed.isEmpty()).isFalse();
        assertThat(childTask2Completed.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTask2Completed.get().getCompletedAt()).isNotNull();
        assertThat(childTask2Completed.get().getLock()).isEqualTo(Lock.free());

        taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskFinished.get().getCompletedAt()).isNull();
        assertThat(taskFinished.get().getAbortedAt()).isNull();
        assertThat(taskFinished.get().getLock()).isEqualTo(Lock.free());

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        finishedBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).getLast();
        barrierService.release(finishedBarrier.getId(), instanceIdProvider.get());

        finishedBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).getLast();
        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isTrue();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNotNull();
        assertThat(finishedBarrier.getLock()).isEqualTo(Lock.free());

        final Optional<Task> taskCompleted = taskRepository.get(taskFinished.get().getId());
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(taskCompleted.get().getCompletedAt()).isNotNull();
        assertThat(taskCompleted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldAbortedTaskWithChildrenWithoutWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final PushTaskDto child1 = TestUtils.createPushTaskDto(new TaskSettingsDto(0, 1, 10, 10));
        final PushTaskDto child2 = TestUtils.createPushTaskDto(new TaskSettingsDto(0, 1, 10, 10));
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(popTask.get().id(), CLIENT, List.of(child1, child2));
        taskService.finishTask(finishTask);

        Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        Barrier finishedBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).getLast();
        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isFalse();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNull();
        assertThat(finishedBarrier.getLock()).isEqualTo(Lock.free());

        final UUID createdChild1 = child1.id();

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startChild1Barrier = barrierRepository.getByTaskId(createdChild1).getFirst();
        barrierService.release(startChild1Barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask1 = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        final FinishTaskDto childFinishTask = TestUtils.createFinishTaskDto(popChildTask1.get().id(), CLIENT);
        taskService.finishTask(childFinishTask);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier finishedChild1Barrier = barrierRepository.getByTaskId(popChildTask1.get().id()).getLast();
        barrierService.release(finishedChild1Barrier.getId(), instanceIdProvider.get());

        final UUID createdChild2 = child2.id();

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startChild2Barrier = barrierRepository.getByTaskId(createdChild2).getFirst();
        barrierService.release(startChild2Barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask2 = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popChildTask2.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask2.get().id(), Instant.now(), CLIENT);

        final Instant failedAt = Instant.now();
        final String failedReason = "Child task is failed.";
        taskService.failTask(popChildTask2.get().id(), failedAt, failedReason, CLIENT);

        final Optional<Task> childTask1Completed = taskRepository.get(child1.id());
        assertThat(childTask1Completed.isEmpty()).isFalse();
        assertThat(childTask1Completed.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTask1Completed.get().getCompletedAt()).isNotNull();
        assertThat(childTask1Completed.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> childTask2Aborted = taskRepository.get(child2.id());
        assertThat(childTask2Aborted.isEmpty()).isFalse();
        assertThat(childTask2Aborted.get().getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(childTask2Aborted.get().getCompletedAt()).isNull();
        assertThat(childTask2Aborted.get().getFinishedAt()).isNull();
        assertThat(childTask2Aborted.get().getAbortedAt()).isNotNull();
        assertThat(childTask2Aborted.get().getFailedAt().toEpochMilli()).isEqualTo(failedAt.toEpochMilli());
        assertThat(childTask2Aborted.get().getFailedReason()).isEqualTo(failedReason);
        assertThat(childTask2Aborted.get().getLock()).isEqualTo(Lock.free());

        taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();
        assertThat(taskFinished.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskFinished.get().getCompletedAt()).isNull();
        assertThat(taskFinished.get().getAbortedAt()).isNull();
        assertThat(taskFinished.get().getLock()).isEqualTo(Lock.free());

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        finishedBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).getLast();

        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isFalse();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNull();
        assertThat(finishedBarrier.getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        barrierService.release(finishedBarrier.getId(), instanceIdProvider.get());

        finishedBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).getLast();
        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isTrue();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNotNull();
        assertThat(finishedBarrier.getLock()).isEqualTo(Lock.free());

        final Optional<Task> taskCompleted = taskRepository.get(taskFinished.get().getId());
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(taskCompleted.get().getFailedReason()).isNotNull();
        assertThat(taskCompleted.get().getAbortedAt()).isNotNull();
        assertThat(taskCompleted.get().getCompletedAt()).isNull();
        assertThat(taskCompleted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldAbortedParentTaskWithChildrenWithWaitingTasksAndCancelChild() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final PushTaskDto child1 = TestUtils.createPushTaskDto(new TaskSettingsDto(0, 1, 10, 10));
        final PushTaskDto child2 = TestUtils.createPushTaskDto(List.of(child1.id()), new TaskSettingsDto(0, 1, 10, 10));
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(popTask.get().id(), CLIENT, List.of(child1, child2));
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        final Barrier finishedBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).getLast();
        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isFalse();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNull();
        assertThat(finishedBarrier.getLock()).isEqualTo(Lock.free());

        final Optional<Task> child1TaskCreated = taskRepository.get(child1.id());
        assertThat(child1TaskCreated.isEmpty()).isFalse();

        final Optional<Task> child2TaskCreated = taskRepository.get(child2.id());
        assertThat(child2TaskCreated.isEmpty()).isFalse();

        final UUID createdChild1 = child1.id();

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        Barrier startChild1Barrier = barrierRepository.getByTaskId(createdChild1).getFirst();
        barrierService.release(startChild1Barrier.getId(), instanceIdProvider.get());

        startChild1Barrier = barrierRepository.getByTaskId(createdChild1).getFirst();
        assertThat(startChild1Barrier.getTaskId()).isEqualTo(createdChild1);
        assertThat(startChild1Barrier.getWaitFor()).isEmpty();
        assertThat(startChild1Barrier.getType()).isEqualTo(Barrier.Type.START);
        assertThat(startChild1Barrier.isReleased()).isTrue();
        assertThat(startChild1Barrier.getUpdatedAt()).isNotNull();
        assertThat(startChild1Barrier.getCreatedAt()).isNotNull();
        assertThat(startChild1Barrier.getReleasedAt()).isNotNull();
        assertThat(startChild1Barrier.getLock()).isEqualTo(Lock.free());

        final Optional<PopTaskDto> popChildTask1 = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        final Instant failedAt = Instant.now();
        final String failedReason = "Child task is failed.";
        taskService.failTask(popChildTask1.get().id(), failedAt, failedReason, CLIENT);

        final Optional<Task> childTask1Aborted = taskRepository.get(child1.id());
        assertThat(childTask1Aborted.isEmpty()).isFalse();
        assertThat(childTask1Aborted.get().getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(childTask1Aborted.get().getAbortedAt()).isNotNull();
        assertThat(childTask1Aborted.get().getLock()).isEqualTo(Lock.free());

        final UUID createdChild2 = child2.id();

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startChild2Barrier = barrierRepository.getByTaskId(createdChild2).getFirst();
        barrierService.release(startChild2Barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask2 = tubeService.pop(pushTaskDto.tube(), CLIENT);
        assertThat(popChildTask2.isEmpty()).isTrue();

        final Optional<Task> childTask2Canceled = taskRepository.get(createdChild2);
        assertThat(childTask2Canceled.isEmpty()).isFalse();
        assertThat(childTask2Canceled.get().getStatus()).isEqualTo(Task.Status.CANCELED);
        assertThat(childTask2Canceled.get().getCompletedAt()).isNull();
        assertThat(childTask2Canceled.get().getFinishedAt()).isNull();
        assertThat(childTask2Canceled.get().getCanceledAt()).isNotNull();
        assertThat(childTask2Canceled.get().getFailedAt()).isNull();
        assertThat(childTask2Canceled.get().getFailedReason()).isNotNull();
        assertThat(childTask2Canceled.get().getLock()).isEqualTo(Lock.free());

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier finishBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).getLast();
        barrierService.release(finishBarrier.getId(), instanceIdProvider.get());

        final Optional<Task> taskAborted = taskRepository.get(finishBarrier.getTaskId());
        assertThat(taskAborted.isEmpty()).isFalse();
        assertThat(taskAborted.get().getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(taskAborted.get().getFailedReason()).isNotNull();
        assertThat(taskAborted.get().getAbortedAt()).isNotNull();
        assertThat(taskAborted.get().getCompletedAt()).isNull();
        assertThat(taskAborted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskAborted.get().getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldCompletedParentTaskWithChildrenWithWaitingTasksAndCompleteChild() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popTask.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popTask.get().id(), Instant.now(), CLIENT);

        final PushTaskDto child1 = TestUtils.createPushTaskDto(new TaskSettingsDto(0, 1, 10, 10));
        final PushTaskDto child2 = TestUtils.createPushTaskDto(List.of(child1.id()), new TaskSettingsDto(0, 1, 10, 10));
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(popTask.get().id(), CLIENT, List.of(child1, child2));
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());
        assertThat(taskFinished.isEmpty()).isFalse();

        Barrier finishedBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).getLast();
        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isFalse();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNull();
        assertThat(finishedBarrier.getLock()).isEqualTo(Lock.free());

        final Optional<Task> child1TaskCreated = taskRepository.get(child1.id());
        assertThat(child1TaskCreated.isEmpty()).isFalse();

        final Optional<Task> child2TaskCreated = taskRepository.get(child2.id());
        assertThat(child2TaskCreated.isEmpty()).isFalse();

        final UUID createdChild1 = child1.id();

        Barrier startChild1Barrier = barrierRepository.getByTaskId(createdChild1).getFirst();
        assertThat(startChild1Barrier.getTaskId()).isEqualTo(createdChild1);
        assertThat(startChild1Barrier.getWaitFor()).isEmpty();
        assertThat(startChild1Barrier.getType()).isEqualTo(Barrier.Type.START);
        assertThat(startChild1Barrier.isReleased()).isFalse();
        assertThat(startChild1Barrier.getUpdatedAt()).isNotNull();
        assertThat(startChild1Barrier.getCreatedAt()).isNotNull();
        assertThat(startChild1Barrier.getReleasedAt()).isNull();
        assertThat(startChild1Barrier.getLock()).isEqualTo(Lock.free());

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        startChild1Barrier = barrierRepository.getByTaskId(createdChild1).getFirst();
        barrierService.release(startChild1Barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask1 = tubeService.pop(pushTaskDto.tube(), CLIENT);

        taskService.startTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask1.get().id(), Instant.now(), CLIENT);

        final FinishTaskDto childFinishTask1 = TestUtils.createFinishTaskDto(popChildTask1.get().id(), CLIENT);
        taskService.finishTask(childFinishTask1);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        Barrier finishChild1Barrier = barrierRepository.getByTaskId(createdChild1).getLast();
        barrierService.release(finishChild1Barrier.getId(), instanceIdProvider.get());

        finishChild1Barrier = barrierRepository.getById(finishChild1Barrier.getId()).get();
        assertThat(finishChild1Barrier.getTaskId()).isEqualTo(createdChild1);
        assertThat(finishChild1Barrier.getWaitFor()).isEmpty();
        assertThat(finishChild1Barrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishChild1Barrier.isReleased()).isTrue();
        assertThat(finishChild1Barrier.getUpdatedAt()).isNotNull();
        assertThat(finishChild1Barrier.getCreatedAt()).isNotNull();
        assertThat(finishChild1Barrier.getReleasedAt()).isNotNull();
        assertThat(finishChild1Barrier.getLock()).isEqualTo(Lock.free());

        final UUID createdChild2 = child2.id();

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startChild2Barrier = barrierRepository.getByTaskId(createdChild2).getFirst();
        barrierService.release(startChild2Barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popChildTask2 = tubeService.pop(pushTaskDto.tube(), CLIENT);
        assertThat(popChildTask2.isEmpty()).isFalse();

        taskService.startTask(popChildTask2.get().id(), Instant.now(), CLIENT);

        taskService.processTask(popChildTask2.get().id(), Instant.now(), CLIENT);

        final FinishTaskDto childFinishTask2 = TestUtils.createFinishTaskDto(popChildTask2.get().id(), CLIENT);
        taskService.finishTask(childFinishTask2);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier finishChild2Barrier = barrierRepository.getByTaskId(createdChild2).getLast();
        barrierService.release(finishChild2Barrier.getId(), instanceIdProvider.get());

        final Optional<Task> childTask1Completed = taskRepository.get(child1.id());
        assertThat(childTask1Completed.isEmpty()).isFalse();
        assertThat(childTask1Completed.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTask1Completed.get().getAbortedAt()).isNull();
        assertThat(childTask1Completed.get().getCompletedAt()).isNotNull();
        assertThat(childTask1Completed.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> childTask2Scheduled = taskRepository.get(createdChild2);
        assertThat(childTask2Scheduled.isEmpty()).isFalse();
        assertThat(childTask2Scheduled.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(childTask2Scheduled.get().getAbortedAt()).isNull();
        assertThat(childTask2Scheduled.get().getCompletedAt()).isNotNull();
        assertThat(childTask2Scheduled.get().getFinishedAt()).isNotNull();
        assertThat(childTask2Scheduled.get().getFailedAt()).isNull();
        assertThat(childTask2Scheduled.get().getFailedReason()).isNull();
        assertThat(childTask2Scheduled.get().getLock()).isEqualTo(Lock.free());

        finishedBarrier = barrierRepository.getByTaskId(taskId).getLast();
        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isFalse();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNull();
        assertThat(finishedBarrier.getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        barrierService.release(finishedBarrier.getId(), instanceIdProvider.get());

        finishedBarrier = barrierRepository.getByTaskId(taskId).getLast();
        assertThat(finishedBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        assertThat(finishedBarrier.getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));
        assertThat(finishedBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishedBarrier.isReleased()).isTrue();
        assertThat(finishedBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishedBarrier.getCreatedAt()).isNotNull();
        assertThat(finishedBarrier.getReleasedAt()).isNotNull();
        assertThat(finishedBarrier.getLock()).isEqualTo(Lock.free());

        final Optional<Task> taskCompleted = taskRepository.get(taskFinished.get().getId());
        assertThat(taskCompleted.isEmpty()).isFalse();
        assertThat(taskCompleted.get().getStatus()).isEqualTo(Task.Status.COMPLETED);
        assertThat(taskCompleted.get().getFailedReason()).isNull();
        assertThat(taskCompleted.get().getCompletedAt()).isNotNull();
        assertThat(taskCompleted.get().getAbortedAt()).isNull();
        assertThat(taskCompleted.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskFinished.get().getUpdatedAt().toEpochMilli());
        assertThat(taskCompleted.get().getLock()).isEqualTo(Lock.free());
    }
}