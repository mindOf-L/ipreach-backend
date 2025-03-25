package app.ipreach.backend.shifts.service;

import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.locations.service.LocationService;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shifts.db.model.Shift;
import app.ipreach.backend.shifts.db.repository.ShiftRepository;
import app.ipreach.backend.shifts.payload.dto.ShiftDto;
import app.ipreach.backend.shifts.payload.mapper.ShiftMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final LocationService locationService;

    @Transactional(readOnly = true)
    public ResponseEntity<?> listShifts(long locationId, String yearMonth, LocalDate date, boolean detailed) {
        if (!locationService.locationExists(locationId))
            throw new RequestException(BAD_REQUEST, Messages.ErrorClient.LOCATION_NOT_FOUND);

        List<Shift> shifts = new ArrayList<>();

        if (yearMonth == null && date == null)
            shifts.addAll(shiftRepository.findByLocationId(locationId));
        else
            shifts.addAll(shiftRepository.findFiltered(locationId, yearMonth, date));

        var shiftDtos = shifts.stream()
            .map(detailed ? ShiftMapper.MAPPER::toDtoWithAssignments : ShiftMapper.MAPPER::toDto)
            .toList();

        return buildResponse(OK, shiftDtos, String.format(Messages.Info.SHIFTS_LISTED, shifts.size()));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getShift(Long shiftId) {
        var shift = shiftRepository.findById(shiftId).orElseThrow(
            () -> new RequestException(HttpStatus.BAD_REQUEST, Messages.ErrorClient.SHIFT_NOT_FOUND));
        return buildResponse(OK, ShiftMapper.MAPPER.toDto(shift));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getShiftsSummary(String year, String month) {
        //var shifts = shiftRepository.findSummarized(year, month);

        return null;
    }

    public ResponseEntity<?> createShiftList(List<ShiftDto> shiftsDto) {
        if (CollectionUtils.isEmpty(shiftsDto))
            return buildResponse(BAD_REQUEST, Messages.ErrorDev.SHIFT_LIST_NULL_OR_EMPTY, Messages.ErrorClient.ERROR_PROCESSING_DATA);

        if (shiftsDto.size() == 1)
            return buildResponse(NOT_IMPLEMENTED, shiftsDto.getFirst().toBuilder().id(RandomUtils.secure().randomLong()).build(),
                Messages.Info.SHIFT_CREATED);

        var shifts = shiftsDto.stream()
            .map(shift -> shift.toBuilder().id(RandomUtils.secure().randomLong()).build())
            .toList();
        return buildResponse(NOT_IMPLEMENTED, shifts, String.format(Messages.Info.SHIFTS_CREATED, shifts.size()));
    }

}
