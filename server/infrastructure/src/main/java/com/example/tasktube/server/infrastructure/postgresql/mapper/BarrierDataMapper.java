package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.domain.enties.Barrier;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
public class BarrierDataMapper {

    public Map<String, Object> getDataDto(final Barrier barrier, final JdbcOperations ps) {
        final Map<String, Object> map = new HashMap<>();
        map.put("id", barrier.getId());
        map.put("task_id", barrier.getTaskId());
        map.put("wait_for", ps.execute((ConnectionCallback<Object>) con -> con.createArrayOf("uuid", barrier.getWaitFor().toArray())));
        map.put("type", barrier.getType().name());
        map.put("released", barrier.isReleased());
        map.put("updated_at", barrier.getUpdatedAt() != null ? Timestamp.from(barrier.getUpdatedAt()) : null);
        map.put("created_at", barrier.getCreatedAt() != null ? Timestamp.from(barrier.getCreatedAt()) : null);
        map.put("released_at", barrier.getReleasedAt() != null ? Timestamp.from(barrier.getReleasedAt()) : null);
        if (barrier.getLock() != null) {
            map.put("locked_at", barrier.getLock().lockedAt() != null ? Timestamp.from(barrier.getLock().lockedAt()) : null);
            map.put("locked", barrier.getLock().locked());
            map.put("locked_by", barrier.getLock().lockedBy());
        } else {
            map.put("locked_at", null);
            map.put("locked", false);
            map.put("locked_by", null);
        }
        return map;

    }
}
