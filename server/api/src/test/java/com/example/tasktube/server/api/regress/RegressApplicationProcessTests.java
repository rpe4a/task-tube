package com.example.tasktube.server.api.regress;

import com.example.tasktube.server.api.RestApiApplication;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.domain.enties.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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
class RegressApplicationProcessTests extends AbstractRegressApplicationTests {

    @Test
    void shouldProcessTask() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        final List<UUID> taskIdList = jobService.getTaskIdList(Task.Status.CREATED, 10, instanceIdProvider.get());

        final UUID createdTaskId = taskIdList.get(0);
        taskService.scheduleTask(createdTaskId, Instant.now(), instanceIdProvider.get());

        final Optional<PopTaskDto> popTask = tubeService.pop(pushTaskDto.tube(), CLIENT);

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

}
