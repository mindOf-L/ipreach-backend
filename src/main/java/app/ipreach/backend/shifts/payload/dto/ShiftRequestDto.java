package app.ipreach.backend.shifts.payload.dto;

import app.ipreach.backend.locations.payload.dto.LocationDto;
import app.ipreach.backend.shared.constants.DateTimePatterns;
import app.ipreach.backend.shared.enums.EStatus;
import app.ipreach.backend.users.payload.dto.UserDto;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static app.ipreach.backend.shared.constants.Messages.Params.GOOGLE_CALENDAR_TEMPLATE;
import static app.ipreach.backend.shared.constants.Messages.Params.OUTLOOK_CALENDAR_TEMPLATE;
import static java.util.stream.Collectors.joining;

@Builder(toBuilder = true)
public record ShiftRequestDto(
    Long id,
    Long userId,
    UserDto user,
    Long shiftId,
    ShiftDto shift,
    Long locationId,
    LocationDto location,
    List<Long> partnerIds,
    List<UserDto> partners,
    int slotsRequested,
    EStatus status

){
    public String detailsOfApproved() {
        final String partners = CollectionUtils.isEmpty(partners())
            ? ""
            : "\n👥 *Other participants in this shift:*\n" + partners().stream()
            .map(p -> String.format("  👉 *%s* - *%s*", p.name(), p.phone()))
            .collect(joining("\n"));

        if (status().equals(EStatus.APPROVED))
            return "\n*Approved shift for [" + shift().location().name() + "](" + shift().location().url() + ")*" +
                "\n🗓️ *Day*: " + shift().dateTimeFrom().format(DateTimePatterns.US_DATE_PATTERN_LETTER) +
                "\n🕣 *Shift hours*: " + shift().dateTimeFrom().format(DateTimePatterns.AM_PM_TIME_PATTERN) + " - "
                + shift().dateTimeTo().format(DateTimePatterns.AM_PM_TIME_PATTERN) +
                partners + // could be empty
                "\n✏️ Add to calendar:\n" +
                "*[Google](" + calendarEvent(shift().location(), GOOGLE_CALENDAR_TEMPLATE) +
                ")* - *[Outlook](" + calendarEvent(shift().location(), OUTLOOK_CALENDAR_TEMPLATE) +
                ")* - *Apple \\(not yet available\\)*\n"
                //")* - *[Apple](" + calendarEvent(shiftLocation, APPLE_CALENDAR_TEMPLATE) + ")*"
                ;

        return "";
    }

    private String calendarEvent(LocationDto locationDto, String template) {
        return String.format(template,
            "Shift to preach on " + locationDto.name(),
            shift().dateTimeFrom().format(DateTimePatterns.DATE_TIME_CALENDAR_PATTERN),
            shift().dateTimeTo().format(DateTimePatterns.DATE_TIME_CALENDAR_PATTERN),
            locationDto.address(),
            locationDto.details());
    }
}
