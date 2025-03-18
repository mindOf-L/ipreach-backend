package app.ipreach.backend.shifts.payload.dto;

import app.ipreach.backend.locations.payload.dto.LocationDto;
import app.ipreach.backend.shared.constants.DateTimePatterns;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record ShiftDto(

    Long id,
    Long locationId,
    LocationDto location,
    String locationName,
    LocalDateTime dateTimeFrom,
    LocalDateTime dateTimeTo,
    Integer slotsOpened,
    Integer slotsAvailable,
    ShiftAssignmentDto assignments

){
    public String details() {
        return "\n📍 *Location: [" + location.name() + "](" + location.url() + ")*" +
            "\n🗓️ *Day*: " + dateTimeFrom.format(DateTimePatterns.US_DATE_PATTERN_SHORT) +
            "\n⌚️ *Shift hours*: " + dateTimeFrom.format(DateTimePatterns.TIME_PATTERN_SHORT) + " - "
            + dateTimeTo.format(DateTimePatterns.TIME_PATTERN_SHORT);
    }
}
