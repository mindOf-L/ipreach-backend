package app.ipreach.backend.shifts.payload.dto;

import app.ipreach.backend.users.payload.dto.UserDto;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ShiftAssignmentDto(

    Long id,
    Long shiftId,
    List<UserDto> participants

){ }
