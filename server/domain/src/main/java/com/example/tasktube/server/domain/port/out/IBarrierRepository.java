package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.enties.Barrier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBarrierRepository {
    Optional<Barrier> get(UUID barrierId);

    void save(List<Barrier> barriers);

    void save(Barrier barrier);

    void update(Barrier barrier);
}
