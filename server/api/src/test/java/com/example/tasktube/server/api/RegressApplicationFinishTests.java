package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.TaskDto;
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
    void shouldFinishedTaskSchedule() {
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

        assertThatThrownBy(() -> taskService.scheduleTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldFinishedTaskProcess() {
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

        assertThatThrownBy(() -> taskService.processTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldFinishedTaskFail() {
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

        assertThatThrownBy(() -> taskService.failTask(taskId, Instant.now(), "Fail reason", instanceIdProvider.get()));
    }

    @Test
    void shouldFinishedTaskTwoTimes() {
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

        assertThatThrownBy(() -> taskService.finishTask(TestUtils.createFinishTaskDto(taskHeartbeat.get().getId(), CLIENT)));
    }

    @Test
    void shouldFinishedTaskWithoutChildren() {
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
    void shouldFinishedTaskWithOneChildrenWithoutWaitingTasks() {
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
    void shouldFinishedTaskWithOneChildrenWithWaitingTasks() {
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
    void shouldFinishedTaskWithTwoChildrenWithWaitingTasks() {
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

}
