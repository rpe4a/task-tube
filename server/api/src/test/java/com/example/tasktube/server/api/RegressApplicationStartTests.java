package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.domain.enties.Task;
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
class RegressApplicationStartTests extends AbstractRegressApplicationTests {

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
    void shouldStartTaskOneTimeSuccessfully() {
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

        assertThatThrownBy(() -> taskService.startTask(taskId, Instant.now(), CLIENT));
    }

    @Test
    void shouldStartTaskDifferentClientFailed() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());
        assertThat(taskPopped.isEmpty()).isFalse();

        assertThatThrownBy(() -> taskService.startTask(taskId, Instant.now(), "CLIENT_2"));
    }

    @Test
    void shouldStartTaskScheduleFailed() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());
        assertThat(taskPopped.isEmpty()).isFalse();

        assertThatThrownBy(() -> taskService.scheduleTask(taskId, Instant.now(), CLIENT));
    }

    @Test
    void shouldStartTaskCompleteFailed() {
        final TaskDto taskDto = TestUtils.createTaskDto();

        final UUID taskId = tubeService.push(taskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(taskDto.tube(), CLIENT);

        final Optional<Task> taskPopped = taskRepository.get(popTask.get().id());
        assertThat(taskPopped.isEmpty()).isFalse();

        assertThatThrownBy(() -> taskService.completeTask(taskId, Instant.now(), CLIENT));
    }
}
