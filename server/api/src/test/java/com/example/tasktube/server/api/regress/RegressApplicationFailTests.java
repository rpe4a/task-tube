package com.example.tasktube.server.api.regress;

import com.example.tasktube.server.api.RestApiApplication;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.application.models.TaskSettingsDto;
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
class RegressApplicationFailTests extends AbstractRegressApplicationTests {

    @Test
    void shouldFailTask() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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
    void shouldFailTaskAndRetryTimeoutSeconds() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(3, 15, 10, 10);
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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
        assertThat(taskScheduled.get().getSettings()).isEqualTo(
                new TaskSettings(taskSettingsDto.maxFailures(), taskSettingsDto.failureRetryTimeoutSeconds(), taskSettingsDto.timeoutSeconds(), taskSettingsDto.heartbeatTimeoutSeconds()));
    }

    @Test
    void shouldFailTaskAndPostponeTask() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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
    void shouldFailTaskWaitOneSecondAndPopTask() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(3, 1, 10, 10);
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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
    void shouldFailTaskTwoTimes() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(2, 1, 10, 10);
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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
        assertThat(taskScheduled3.get().getSettings()).isEqualTo(new TaskSettings(taskSettingsDto.maxFailures(), taskSettingsDto.failureRetryTimeoutSeconds(),  taskSettingsDto.timeoutSeconds(), taskSettingsDto.heartbeatTimeoutSeconds()));
    }

    @Test
    void shouldAbortTask() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(1, 1, 10, 10);
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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
        assertThat(taskAborted.get().getSettings()).isEqualTo(new TaskSettings(taskSettingsDto.maxFailures(), taskSettingsDto.failureRetryTimeoutSeconds(),  taskSettingsDto.timeoutSeconds(), taskSettingsDto.heartbeatTimeoutSeconds()));
    }

    @Test
    void shouldAbortTaskStartFailed() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(1, 1, 10, 10);
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).getFirst();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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

        assertThatThrownBy(() -> taskService.startTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldAbortTaskProcessFailed() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(1, 1, 10, 10);
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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

        assertThatThrownBy(() -> taskService.processTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldAbortTaskFailFailed() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(1, 1, 10, 10);
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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

        assertThatThrownBy(() -> taskService.failTask(taskId, Instant.now(), "Fail reason", instanceIdProvider.get()));
    }

    @Test
    void shouldAbortTaskFinishFailed() {
        final TaskSettingsDto taskSettingsDto = new TaskSettingsDto(1, 1, 10, 10);
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto(taskSettingsDto);

        final UUID taskId = tubeService.push(pushTaskDto);

        jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        final Barrier barrier = barrierRepository.getByTaskId(taskId).stream().findFirst().orElseThrow();
        barrierService.release(barrier.getId(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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

        assertThatThrownBy(() -> taskService.finishTask(TestUtils.createFinishTaskDto(taskId, CLIENT)));
    }
}
