package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sandbox.tube.regress.dto.IRegressInterface;
import com.example.tasktube.client.sandbox.tube.regress.dto.RegressDto;
import com.example.tasktube.client.sandbox.tube.regress.dto.RegressStatus;
import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.TaskResult;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StartPointTaskReturnNothing extends Task0<Void> {

    @Nonnull
    @Override
    public Value<Void> run() throws Exception {
        // Tasks can return different type
        final TaskResult<Integer> integerTaskResult = pushIn(new TaskReturnInteger());
        final TaskResult<Integer> integerNullTaskResult = pushIn(new TaskReturnIntegerNull());
        final TaskResult<Long> longTaskResult = pushIn(new TaskReturnLong());
        final TaskResult<Float> floatTaskResult = pushIn(new TaskReturnFloat());
        final TaskResult<Double> doubleTaskResult = pushIn(new TaskReturnDouble());
        final TaskResult<Boolean> booleanFalseTaskResult = pushIn(new TaskReturnBooleanFalse());
        final TaskResult<Boolean> booleanTrueTaskResult = pushIn(new TaskReturnBooleanTrue());
        final TaskResult<Character> characterTaskResult = pushIn(new TaskReturnChar());
        final TaskResult<String> stringTaskResult = pushIn(new TaskReturnString());
        final TaskResult<String> stringNullTaskResult = pushIn(new TaskReturnStringNull());
        final TaskResult<RegressDto> dtoTaskResult = pushIn(new TaskReturnDto());
        final TaskResult<RegressDto> dtoNullTaskResult = pushIn(new TaskReturnDtoNull());
        final TaskResult<IRegressInterface> iRegressInterfaceTaskResult = pushIn(new TaskReturnInterface());
        final TaskResult<Map<String, RegressDto>> mapStringDtoTaskResult = pushIn(new TaskReturnMapStringDto());
        final TaskResult<Map<String, List<RegressDto>>> mapStringListDtoTaskResult = pushIn(new TaskReturnMapStringListDto());
        final TaskResult<Map<String, Object>> mapStringObjectTaskResult = pushIn(new TaskReturnMapStringObject());
        final TaskResult<List<RegressDto>> listDtoTaskResult = pushIn(new TaskReturnListDto());
        final TaskResult<List<String>> listStringTaskResult = pushIn(new TaskReturnListString());
        final TaskResult<List<IRegressInterface>> listInterfaceTaskResult = pushIn(new TaskReturnListInterface());
        final TaskResult<List<Map<String, Object>>> listMapTaskResult = pushIn(new TaskReturnListMap());
        final TaskResult<RegressDto[]> arrayDtoTaskResult = pushIn(new TaskReturnArrayDto());
        final TaskResult<String[]> arrayStringTaskResult = pushIn(new TaskReturnArrayString());
        final TaskResult<IRegressInterface[]> arrayInterfaceTaskResult = pushIn(new TaskReturnArrayInterface());
        final TaskResult<RegressStatus> enumTaskResult = pushIn(new TaskReturnEnum());
        final TaskResult<Instant> instantTaskResult = pushIn(new TaskReturnInstant());
        final TaskResult<LocalDate> locaDateTaskResult = pushIn(new TaskReturnLocalDate());
        final TaskResult<LocalDateTime> locaDateTimeTaskResult = pushIn(new TaskReturnLocalDateTime());
        final TaskResult<Set<RegressDto>> setDtoTaskResult = pushIn(new TaskReturnSetDto());
        final TaskResult<Set<IRegressInterface>> setInterfaceTaskResult = pushIn(new TaskReturnSetInterface());
        final TaskResult<Set<String>> setStringTaskResult = pushIn(new TaskReturnSetString());
        final TaskResult<UUID> uuidTaskResult = pushIn(new TaskReturnUUID());

        return nothing();
    }
}
