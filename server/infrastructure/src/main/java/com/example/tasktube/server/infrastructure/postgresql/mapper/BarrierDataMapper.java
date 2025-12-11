package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.Lock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public Barrier getBarrier(final ResultSet rs) throws SQLException {
        final Barrier barrier = new Barrier();
        barrier.setId(rs.getObject("id", UUID.class));
        barrier.setTaskId(rs.getObject("task_Id", UUID.class));
        barrier.setWaitFor(Arrays.asList((UUID[])rs.getArray("wait_For").getArray()));
        barrier.setType(Barrier.Type.valueOf(rs.getString("type")));
        barrier.setReleased(rs.getBoolean("released"));
        barrier.setUpdatedAt(Instant.ofEpochMilli(rs.getTimestamp("updated_at").getTime()));
        barrier.setCreatedAt(Instant.ofEpochMilli(rs.getTimestamp("created_at").getTime()));
        barrier.setReleasedAt(rs.getTimestamp("released_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("released_at").getTime())
                : null);
        barrier.setLock(
                new Lock(
                        rs.getTimestamp("locked_at") != null
                                ? Instant.ofEpochMilli(rs.getTimestamp("locked_at").getTime())
                                : null,
                        rs.getBoolean("locked"),
                        rs.getString("locked_by")
                )
        );
        return barrier;
    }
}
