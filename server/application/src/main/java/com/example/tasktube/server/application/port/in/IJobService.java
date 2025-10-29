package com.example.tasktube.server.application.port.in;

import com.example.tasktube.server.application.models.SchedulingDto;

public interface IJobService {
    void scheduleTask(SchedulingDto schedulingDto);
}
