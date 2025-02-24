package app.ipreach.backend.mapper.dto.user;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ShiftAssignmentDto(

    Long id,
    Long shiftId,
    UserDto user,
    List<UserDto> partners

){ }
