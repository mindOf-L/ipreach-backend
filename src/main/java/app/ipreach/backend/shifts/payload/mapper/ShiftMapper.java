package app.ipreach.backend.shifts.payload.mapper;

import app.ipreach.backend.shifts.db.model.Shift;
import app.ipreach.backend.shifts.payload.dto.ShiftAssignmentDto;
import app.ipreach.backend.shifts.payload.dto.ShiftDto;
import app.ipreach.backend.users.payload.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShiftMapper {

    ShiftMapper MAPPER = Mappers.getMapper(ShiftMapper.class);

    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "location.id", source = "locationId")
    Shift toMo(ShiftDto shift);

    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "locationId", source = "location.id")
    ShiftDto toDto(Shift shift);

    @Mapping(target = "assignments", source = "shift", qualifiedByName = "mapAssignmentsToDto")
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "locationId", source = "location.id")
    ShiftDto toDtoWithAssignments(Shift shift);

    @Named("mapAssignmentsToDto")
    static ShiftAssignmentDto mapAssignmentsToDto(Shift shift) {
        var participants = shift.getAssignments()
            .stream()
            .map(shiftAssignment ->
                UserMapper.MAPPER.toDtoForAssignment(shiftAssignment.getUser())
                    .toBuilder()
                    .shiftUserRole(shiftAssignment.getShiftUserRole())
                    .build()
            )
            .toList();

        return ShiftAssignmentDto.builder()
            .shiftId(shift.getId())
            .participants(participants)
            .build();
    }

}
