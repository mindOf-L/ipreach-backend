package app.ipreach.backend.users.payload.dto;

import app.ipreach.backend.shared.constants.DateTimePatterns;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record ShiftDto(

    Long id,
    LocationDto location,
    Integer slotsOpened,
    Integer slotsAvailable,
    LocalDateTime dateTimeFrom,
    LocalDateTime dateTimeTo,
    ShiftAssignmentDto assignment

){
    public String details() {
        return "\n📍 *Location: [" + location.name() + "](" + location.url() + ")*" +
            "\n🗓️ *Day*: " + dateTimeFrom.format(DateTimePatterns.US_DATE_PATTERN_SHORT) +
            "\n⌚️ *Shift hours*: " + dateTimeFrom.format(DateTimePatterns.TIME_PATTERN_SHORT) + " - "
            + dateTimeTo.format(DateTimePatterns.TIME_PATTERN_SHORT);
    }
}
