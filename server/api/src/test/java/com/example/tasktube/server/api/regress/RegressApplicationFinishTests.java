package com.example.tasktube.server.api.regress;

import com.example.tasktube.server.api.RestApiApplication;
import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.PushTaskDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest(
        classes = RestApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class RegressApplicationFinishTests extends AbstractRegressApplicationTests {

    @Test
    void shouldFinishedTaskProcess() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

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

        assertThatThrownBy(() -> taskService.processTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldFinishedTaskFail() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

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

        assertThatThrownBy(() -> taskService.failTask(taskId, Instant.now(), "Fail reason", instanceIdProvider.get()));
    }

    @Test
    void shouldFinishedTaskTwoTimes() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

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

        assertThatThrownBy(() -> taskService.finishTask(TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT)));
    }

    @Test
    void shouldFinishedTaskWithoutChildren() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
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
        assertThat(taskFinished.get().getStatus()).isEqualTo(Task.Status.FINISHED);
        assertThat(taskFinished.get().getFinishedAt().toEpochMilli()).isEqualTo(finishTask.finishedAt().toEpochMilli());
        assertThat(taskFinished.get().getHeartbeatAt().toEpochMilli()).isEqualTo(taskHeartbeat.get().getHeartbeatAt().toEpochMilli());
        assertThat(taskFinished.get().getUpdatedAt().toEpochMilli()).isGreaterThan(taskHeartbeat.get().getUpdatedAt().toEpochMilli());
        assertThat(taskFinished.get().getLock()).isEqualTo(Lock.free());

        final List<Barrier> barriers = barrierRepository.getByTaskId(taskFinished.get().getId());
        assertThat(barriers).hasSize(2);
        assertThat(barriers.getFirst().getStatus()).isEqualTo(Barrier.Status.COMPLETED);
        assertThat(barriers.getLast().getStatus()).isEqualTo(Barrier.Status.WAITING);
    }

    @Test
    void shouldFinishedTaskWithOneChildrenWithoutWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final PushTaskDto child = TestUtils.createPushTaskDto();
        final List<PushTaskDto> children = List.of(child);
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(
                taskHeartbeat.get().getId(),
                CLIENT,
                children
        );
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());

        final Barrier finishBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).get(1);

        assertThat(finishBarrier.getTaskId()).isEqualTo(taskFinished.get().getId());
        final List<UUID> childrenIdList = children.stream().map(x -> x.id()).toList();
        assertThat(finishBarrier.getWaitFor()).isEqualTo(childrenIdList);
        assertThat(finishBarrier.getType()).isEqualTo(Barrier.Type.FINISH);
        assertThat(finishBarrier.isReleased()).isFalse();
        assertThat(finishBarrier.getUpdatedAt()).isNotNull();
        assertThat(finishBarrier.getCreatedAt()).isNotNull();
        assertThat(finishBarrier.getReleasedAt()).isNull();
        assertThat(finishBarrier.getLock()).isEqualTo(Lock.free());

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
    void shouldFinishedTaskWithOneChildrenWithWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final UUID waitTaskId = UUID.randomUUID();
        final PushTaskDto child = TestUtils.createPushTaskDto(List.of(waitTaskId));
        final List<PushTaskDto> children = List.of(child);
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(
                taskHeartbeat.get().getId(),
                CLIENT,
                children
        );
        taskService.finishTask(finishTask);

        final Optional<Task> taskChild = taskRepository.get(child.id());

        final Barrier startChildBarrier = barrierRepository.getByTaskId(taskChild.get().getId()).getFirst();
        assertThat(startChildBarrier.getTaskId()).isEqualTo(taskChild.get().getId());
        assertThat(startChildBarrier.getWaitFor()).isEqualTo(List.of(waitTaskId));
        assertThat(startChildBarrier.getType()).isEqualTo(Barrier.Type.START);
        assertThat(startChildBarrier.isReleased()).isFalse();
        assertThat(startChildBarrier.getUpdatedAt()).isNotNull();
        assertThat(startChildBarrier.getCreatedAt()).isNotNull();
        assertThat(startChildBarrier.getReleasedAt()).isNull();
        assertThat(startChildBarrier.getLock()).isEqualTo(Lock.free());
    }

    @Test
    void shouldFinishedTaskWithTwoChildrenWithWaitingTasks() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier startBarrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(startBarrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());

        taskService.startTask(taskPopped.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskProcessing = taskRepository.get(popTask.get().id());

        taskService.processTask(taskProcessing.get().getId(), Instant.now(), CLIENT);

        final Optional<Task> taskHeartbeat = taskRepository.get(taskProcessing.get().getId());

        final UUID waitTaskId1 = UUID.randomUUID();
        final UUID waitTaskId2 = UUID.randomUUID();
        final PushTaskDto child1 = TestUtils.createPushTaskDto(List.of(waitTaskId1));
        final PushTaskDto child2 = TestUtils.createPushTaskDto(List.of(waitTaskId2));
        final List<PushTaskDto> children = List.of(child1, child2);
        final FinishTaskDto finishTask = TestUtils.createFinishTaskDto(
                taskHeartbeat.get().getId(),
                CLIENT,
                children
        );
        taskService.finishTask(finishTask);

        final Optional<Task> taskFinished = taskRepository.get(finishTask.taskId());

        final Barrier finishBarrier = barrierRepository.getByTaskId(taskFinished.get().getId()).getLast();
        assertThat(finishBarrier.getWaitFor()).isEqualTo(List.of(child1.id(), child2.id()));

        final Optional<Task> taskChild1 = taskRepository.get(child1.id());

        final Barrier startChildBarrier1 = barrierRepository.getByTaskId(taskChild1.get().getId()).get(0);
        assertThat(startChildBarrier1.getTaskId()).isEqualTo(taskChild1.get().getId());
        assertThat(startChildBarrier1.getWaitFor()).isEqualTo(List.of(waitTaskId1));
        assertThat(startChildBarrier1.getType()).isEqualTo(Barrier.Type.START);
        assertThat(startChildBarrier1.isReleased()).isFalse();
        assertThat(startChildBarrier1.getUpdatedAt()).isNotNull();
        assertThat(startChildBarrier1.getCreatedAt()).isNotNull();
        assertThat(startChildBarrier1.getReleasedAt()).isNull();
        assertThat(startChildBarrier1.getLock()).isEqualTo(Lock.free());

        final Optional<Task> taskChild2 = taskRepository.get(child2.id());

        final Barrier startChildBarrier2 = barrierRepository.getByTaskId(taskChild2.get().getId()).get(0);
        assertThat(startChildBarrier2.getTaskId()).isEqualTo(taskChild2.get().getId());
        assertThat(startChildBarrier2.getWaitFor()).isEqualTo(List.of(waitTaskId2));
        assertThat(startChildBarrier2.getType()).isEqualTo(Barrier.Type.START);
        assertThat(startChildBarrier2.isReleased()).isFalse();
        assertThat(startChildBarrier2.getUpdatedAt()).isNotNull();
        assertThat(startChildBarrier2.getCreatedAt()).isNotNull();
        assertThat(startChildBarrier2.getReleasedAt()).isNull();
        assertThat(startChildBarrier2.getLock()).isEqualTo(Lock.free());
    }

}
