package com.example.tasktube.server.api.regress;

import com.example.tasktube.server.application.port.in.IBarrierService;
import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.application.port.in.ITubeService;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.infrastructure.configuration.InstanceIdProvider;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.JdbcTemplate;

@TestComponent
public abstract class AbstractRegressApplicationTests {
    static final String CLIENT = "client";

    @Autowired
    ITubeService tubeService;

    @Autowired
    IJobService jobService;

    @Autowired
    ITaskService taskService;

    @Autowired
    IBarrierService barrierService;

    @Autowired
    ITaskRepository taskRepository;

    @Autowired
    IBarrierRepository barrierRepository;

    @Autowired
    InstanceIdProvider instanceIdProvider;

    @Autowired
    JdbcTemplate db;

    @BeforeEach
    void setup() {
        final String deleteTasks = """
                    delete from tasks
                """;

        final String deleteBarriers = """
                    delete from barriers
                """;

        db.execute(deleteTasks);
        db.execute(deleteBarriers);
    }
}
