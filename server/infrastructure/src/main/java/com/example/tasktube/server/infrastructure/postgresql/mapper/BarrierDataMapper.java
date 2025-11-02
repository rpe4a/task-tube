package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.domain.enties.Barrier;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
public class BarrierDataMapper {

    public void fillByDataDto(final Barrier barrier, final PreparedStatement ps) throws SQLException {
//        final Map<String, Object> map = new HashMap<>();
//        map.put("id", barrier.getId());
//        map.put("task_id", barrier.getTaskId());
//        map.put("wait_for", barrier.getWaitFor().toArray());
//        map.put("type", barrier.getType().name());
//        map.put("released", barrier.isReleased());
//        map.put("updated_at", barrier.getUpdatedAt() != null ? Timestamp.from(barrier.getUpdatedAt()) : null);
//        map.put("created_at", barrier.getCreatedAt() != null ? Timestamp.from(barrier.getCreatedAt()) : null);
//        map.put("released_at", barrier.getReleasedAt() != null ? Timestamp.from(barrier.getReleasedAt()) : null);
//        if(barrier.getLock() != null){
//            map.put("locked_at", barrier.getLock().lockedAt() != null ? Timestamp.from(barrier.getLock().lockedAt()) : null);
//            map.put("locked", barrier.getLock().locked());
//            map.put("locked_by", barrier.getLock().lockedBy());
//        }else{
//            map.put("locked_at", null);
//            map.put("locked", false);
//            map.put("locked_by", null);
//        }
//
//        return map;
        ps.setObject(1, barrier.getId());
        ps.setObject(2, barrier.getTaskId());
        ps.setArray(3, ps.getConnection().createArrayOf("uuid", barrier.getWaitFor().toArray()));
        ps.setString(4, barrier.getType().name());
        ps.setBoolean(5, barrier.isReleased());
        ps.setTimestamp(6, barrier.getUpdatedAt() != null ? Timestamp.from(barrier.getUpdatedAt()) : null);
        ps.setTimestamp(7, barrier.getCreatedAt() != null ? Timestamp.from(barrier.getCreatedAt()) : null);
        ps.setTimestamp(8, barrier.getReleasedAt() != null ? Timestamp.from(barrier.getReleasedAt()) : null);
        if (barrier.getLock() != null) {
            ps.setTimestamp(9, barrier.getLock().lockedAt() != null ? Timestamp.from(barrier.getLock().lockedAt()) : null);
            ps.setBoolean(10, barrier.getLock().locked());
            ps.setString(11, barrier.getLock().lockedBy());
        } else {
            ps.setTimestamp(9, null);
            ps.setBoolean(10, false);
            ps.setString(11, null);
        }
    }
}
