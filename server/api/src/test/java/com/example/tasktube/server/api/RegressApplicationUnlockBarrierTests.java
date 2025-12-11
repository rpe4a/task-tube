package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.TaskDto;
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

@ActiveProfiles("test")
@SpringBootTest(
        classes = RestApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class RegressApplicationUnlockBarrierTests extends AbstractRegressApplicationTests {

    @Test
    void shouldNotGetLockedBarriers() {
        final TaskDto taskDto = TestUtils.createTaskDto(List.of(UUID.randomUUID(), UUID.randomUUID()));

        final UUID taskId = tubeService.push(taskDto);

        final Task task = taskRepository.get(taskId).orElseThrow();
        assertThat(task.getStartBarrier()).isNotNull();

        final Barrier barrier = barrierRepository.get(task.getStartBarrier()).orElseThrow();

        final Lock lock = new Lock(Instant.now(), true, instanceIdProvider.get());
        barrier.setLock(lock);

        barrierRepository.update(barrier);

        final List<UUID> lockedBarriers = jobService.getLockedBarrierIdList(10, 600);
        assertThat(lockedBarriers).isEmpty();
    }

    @Test
    void shouldGetLockedBarriers() {
        final TaskDto taskDto = TestUtils.createTaskDto(List.of(UUID.randomUUID(), UUID.randomUUID()));

        final UUID taskId = tubeService.push(taskDto);

        final Task task = taskRepository.get(taskId).orElseThrow();
        assertThat(task.getStartBarrier()).isNotNull();

        final Barrier barrier = barrierRepository.get(task.getStartBarrier()).orElseThrow();

        final Lock lock = new Lock(Instant.now().minusSeconds(600), true, instanceIdProvider.get());
        barrier.setLock(lock);

        barrierRepository.update(barrier);

        final List<UUID> lockedBarriers = jobService.getLockedBarrierIdList(10, 600);
        assertThat(lockedBarriers).hasSize(1);
    }

    @Test
    void shouldUnlockedCreatedTasks() {
        final TaskDto taskDto = TestUtils.createTaskDto(List.of(UUID.randomUUID(), UUID.randomUUID()));

        final UUID taskId = tubeService.push(taskDto);

        final Task task = taskRepository.get(taskId).orElseThrow();
        assertThat(task.getStartBarrier()).isNotNull();

        final Barrier barrier = barrierRepository.get(task.getStartBarrier()).orElseThrow();

        final Lock lock = new Lock(Instant.now().minusSeconds(600), true, instanceIdProvider.get());
        barrier.setLock(lock);

        barrierRepository.update(barrier);

        final List<UUID> lockedBarriers = jobService.getLockedBarrierIdList(10, 600);
        assertThat(lockedBarriers).hasSize(1);

        final Barrier lockedBarrier = barrierRepository.get(task.getStartBarrier()).orElseThrow();
        assertThat(lockedBarrier.getCreatedAt().toEpochMilli()).isEqualTo(barrier.getCreatedAt().toEpochMilli());
        assertThat(lockedBarrier.getLock().lockedBy()).isEqualTo(lock.lockedBy());
        assertThat(lockedBarrier.getLock().lockedAt().toEpochMilli()).isEqualTo(lock.lockedAt().toEpochMilli());
        assertThat(lockedBarrier.getLock().locked()).isEqualTo(lock.locked());

        barrierService.unlockBarrier(barrier.getId(), 600);

        final Barrier unlockedBarrier = barrierRepository.get(barrier.getId()).orElseThrow();
        assertThat(unlockedBarrier.getCreatedAt().toEpochMilli()).isEqualTo(barrier.getCreatedAt().toEpochMilli());
        assertThat(unlockedBarrier.getUpdatedAt().toEpochMilli()).isGreaterThan(barrier.getUpdatedAt().toEpochMilli());
        assertThat(unlockedBarrier.getLock()).isEqualTo(Lock.free());
    }

}
