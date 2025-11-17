package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.application.port.in.ITubeService;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.values.Lock;
import com.example.tasktube.server.domain.values.TaskSettings;
import com.example.tasktube.server.infrastructure.configuration.InstanceIdProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(
        classes = RestApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class InfrastructureTests {

    @Autowired
    ITubeService tubeService;

    @Autowired
    ITaskRepository taskRepository;

    @Autowired
    IBarrierRepository barrierRepository;

    @Autowired
    InstanceIdProvider instanceIdProvider;

    @Test
    void shouldPushTaskWithoutStartBarrierSuccessfully() {
        final TaskDto taskDto = new TaskDto(
                UUID.randomUUID(),
                "task" + UUID.randomUUID(),
                "tube" + UUID.randomUUID(),
                Map.of("key", "value"),
                null,
                Instant.now()
        );

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
        final TaskDto taskDto = new TaskDto(
                UUID.randomUUID(),
                "task" + UUID.randomUUID(),
                "tube" + UUID.randomUUID(),
                Map.of("key", "value"),
                List.of(UUID.randomUUID()),
                Instant.now()
        );

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
    void shouldPopTask() {
        final TaskDto taskDto = new TaskDto(
                UUID.randomUUID(),
                "task" + UUID.randomUUID(),
                "tube",
                Map.of("key", "value"),
                List.of(UUID.randomUUID()),
                Instant.now()
        );

        final UUID taskId = tubeService.push(taskDto);

        final Optional<Task> task = taskRepository.get(taskId);
        assertThat(task.isPresent()).isTrue();
    }
}