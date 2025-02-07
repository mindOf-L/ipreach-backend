package app.ipreach.backend.controller.shifts;

import app.ipreach.backend.dto.user.ShiftDto;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.creation.FakeClass;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static app.ipreach.backend.shared.creation.FakeClass.giveMeLocation;
import static app.ipreach.backend.shared.creation.FakeClass.giveMeShift;
import static app.ipreach.backend.shared.creation.Generator.getRandomIntegerFromRange;
import static app.ipreach.backend.shared.creation.Generator.getRandomList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequestMapping("/shifts")
@Tag(name = "Shifts", description = "Shift methods to create, list and filter")
public class ShiftController {

    @GetMapping
    public ResponseEntity<?> listShifts(
        @RequestParam long locationId,
        @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM") YearMonth month,
        @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date,
        @RequestParam(required = false) boolean detailed
    ) {

        var location = giveMeLocation().toBuilder().id(locationId).build();
        var shifts = getRandomList(FakeClass::giveMeShift).stream()
            .map(shift -> {
                LocalDate shiftThisDate = null;
                if (date != null) shiftThisDate = date;
                else if (month != null) shiftThisDate = month.atDay(getRandomIntegerFromRange(1, month.atEndOfMonth().getDayOfMonth()));

                return shift.toBuilder()
                    .location(location)
                    .dateTimeFrom(shiftThisDate != null ? shiftThisDate.atStartOfDay() : shift.dateTimeFrom())
                    .dateTimeTo(shiftThisDate != null ? shiftThisDate.atStartOfDay().plusHours(2) : shift.dateTimeTo())
                    .build();
            })
            .toList();

        return buildResponse(OK, shifts, String.format(Messages.Info.SHIFTS_LISTED, shifts.size()));
    }

    @GetMapping("/{shiftId}")
    public ResponseEntity<?> getShift(@PathVariable Integer shiftId) {
        return buildResponse(OK, giveMeShift(shiftId));
    }

    @PostMapping()
    public ResponseEntity<?> createShiftList(@RequestBody List<ShiftDto> shiftsDto) {
        if(CollectionUtils.isEmpty(shiftsDto))
            return buildResponse(BAD_REQUEST, Messages.ErrorDev.SHIFT_LIST_NULL_OR_EMPTY, Messages.ErrorClient.ERROR_PROCESSING_DATA);

        if(shiftsDto.size() == 1)
            return buildResponse(OK, shiftsDto.getFirst().toBuilder().id(RandomUtils.secure().randomLong()).build(),
                Messages.Info.SHIFT_CREATED);

        var shifts = shiftsDto.stream()
            .map(shift -> shift.toBuilder().id(RandomUtils.secure().randomLong()).build())
            .toList();
        return buildResponse(OK, shifts, String.format(Messages.Info.SHIFTS_CREATED, shifts.size()));
    }
}
