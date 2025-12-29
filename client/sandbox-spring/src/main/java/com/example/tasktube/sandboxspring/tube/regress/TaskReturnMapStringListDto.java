package com.example.tasktube.sandboxspring.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.example.tasktube.sandboxspring.tube.regress.dto.RegressDto;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Map;

public class TaskReturnMapStringListDto extends Task0<Map<String, List<RegressDto>>> {

    @Nonnull
    @Override
    public Value<Map<String, List<RegressDto>>> run() throws Exception {
        return constant(Map.of("1", List.of(new RegressDto("hello", 1, true))), new TypeReference<>() {});
    }
}
