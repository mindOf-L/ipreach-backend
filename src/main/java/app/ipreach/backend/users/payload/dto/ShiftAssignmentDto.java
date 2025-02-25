package app.ipreach.backend.users.payload.dto;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ShiftAssignmentDto(

    Long id,
    Long shiftId,
    UserDto user,
    List<UserDto> partners

){ }
