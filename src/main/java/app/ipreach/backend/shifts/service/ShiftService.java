package app.ipreach.backend.shifts.service;

import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.creation.FakeClass;
import app.ipreach.backend.shifts.db.repository.ShiftRepository;
import app.ipreach.backend.shifts.payload.dto.ShiftDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static app.ipreach.backend.shared.creation.FakeClass.giveMeLocation;
import static app.ipreach.backend.shared.creation.FakeClass.giveMeShiftWithAssignment;
import static app.ipreach.backend.shared.creation.Generator.getRandomIntegerFromRange;
import static app.ipreach.backend.shared.creation.Generator.getRandomList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;

    public ResponseEntity<?> listShifts(long locationId, YearMonth month, LocalDate date, boolean detailed) {
        var location = giveMeLocation().toBuilder().id(locationId).build();
        var shifts = getRandomList(detailed
            ? FakeClass::giveMeShiftWithAssignment
            : FakeClass::giveMeShift)
            .stream()
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
    public ResponseEntity<?> getShift(Integer shiftId) {
        return buildResponse(OK, giveMeShiftWithAssignment(shiftId));
    }
    public ResponseEntity<?> createShiftList(List< ShiftDto > shiftsDto) {
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
