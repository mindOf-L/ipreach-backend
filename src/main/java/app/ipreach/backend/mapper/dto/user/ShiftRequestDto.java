package app.ipreach.backend.mapper.dto.user;

import app.ipreach.backend.shared.constants.DateTimePatterns;
import app.ipreach.backend.shared.enums.EStatus;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static app.ipreach.backend.shared.constants.Messages.Params.GOOGLE_CALENDAR_TEMPLATE;
import static app.ipreach.backend.shared.constants.Messages.Params.OUTLOOK_CALENDAR_TEMPLATE;
import static java.util.stream.Collectors.joining;

@Builder
public record ShiftRequestDto(

    Long id,
    LocationDto location,
    UserDto user,
    ShiftDto shift,
    List<UserDto> partners,
    int slotsRequested,
    EStatus status

) {

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
