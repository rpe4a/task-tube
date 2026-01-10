package com.example.tasktube.server.api.regress;

import com.example.tasktube.server.api.RestApiApplication;
import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.Lock;
import com.example.tasktube.server.domain.values.slot.ConstantSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest(
        classes = RestApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class RegressApplicationScheduleTests extends AbstractRegressApplicationTests {

    private static Stream<List<UUID>> provideWaitFor() {
        return Stream.of(
                List.of(UUID.randomUUID()),
                List.of(),
                null
        );
    }

    @Test
    void shouldCreatedTaskStartFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        assertThatThrownBy(() -> taskService.startTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldCreatedTaskProcessFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        assertThatThrownBy(() -> taskService.processTask(taskId, Instant.now(), instanceIdProvider.get()));
    }

    @Test
    void shouldCreatedTaskFinishFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        assertThatThrownBy(() -> taskService.finishTask(new FinishTaskDto(taskId, null, new ConstantSlot(), CLIENT, Instant.now())));
    }

    @Test
    void shouldCreatedTaskFailFailed() {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto();

        final UUID taskId = tubeService.push(pushTaskDto);

        assertThatThrownBy(() -> taskService.failTask(taskId, Instant.now(), "fail reason", instanceIdProvider.get()));
    }

    @ParameterizedTest
    @MethodSource("provideWaitFor")
    void shouldScheduledTaskWithBarrier(final List<UUID> waitFor) {
        final PushTaskDto pushTaskDto = TestUtils.createPushTaskDto(waitFor);

        final UUID taskId = tubeService.push(pushTaskDto);

        final Optional<Task> pushTask = taskRepository.get(taskId);
        assertThat(pushTask.isPresent()).isTrue();
        assertThat(pushTask.get().getStatus()).isEqualTo(Task.Status.CREATED);

        final List<UUID> barrierIdList = jobService.getBarrierIdList(Barrier.Status.WAITING, 10, instanceIdProvider.get());
        assertThat(barrierIdList).isNotEmpty();
        assertThat(barrierIdList.size()).isEqualTo(1);

        final UUID startBarrierId = barrierIdList.get(0);
        final Optional<Barrier> startBarrier = barrierRepository.getById(startBarrierId);
        assertThat(startBarrier.isPresent()).isTrue();
        assertThat(startBarrier.get().getType()).isEqualTo(Barrier.Type.START);
        assertThat(startBarrier.get().getStatus()).isEqualTo(Barrier.Status.WAITING);
        assertThat(startBarrier.get().getTaskId()).isEqualTo(pushTask.get().getId());
        assertThat(startBarrier.get().isNotReleased()).isTrue();
        assertThat(startBarrier.get().getLock().isLockedBy(instanceIdProvider.get())).isTrue();

        barrierService.release(startBarrierId, instanceIdProvider.get());

        final Optional<Barrier> startReleasedBarrier = barrierRepository.getById(startBarrierId);
        assertThat(startReleasedBarrier.isPresent()).isTrue();
        assertThat(startReleasedBarrier.get().isReleased()).isTrue();
        assertThat(startReleasedBarrier.get().getReleasedAt()).isNotNull();
        assertThat(startReleasedBarrier.get().getUpdatedAt().toEpochMilli()).isGreaterThan(startBarrier.get().getUpdatedAt().toEpochMilli());
        assertThat(startReleasedBarrier.get().getLock()).isEqualTo(Lock.free());

        final Optional<Task> taskScheduled = taskRepository.get(taskId);
        assertThat(taskScheduled.isPresent()).isTrue();
        assertThat(taskScheduled.get().getStatus()).isEqualTo(Task.Status.SCHEDULED);
        assertThat(taskScheduled.get().getScheduledAt()).isNotNull();
        assertThat(taskScheduled.get().getCanceledAt()).isNull();
        assertThat(taskScheduled.get().getUpdatedAt().toEpochMilli()).isGreaterThan(pushTask.get().getUpdatedAt().toEpochMilli());
        assertThat(taskScheduled.get().getLock()).isEqualTo(Lock.free());
    }
}
