package app.ipreach.backend.shifts.service;

import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shifts.db.repository.ShiftRepository;
import app.ipreach.backend.shifts.payload.dto.ShiftAssignmentDto;
import app.ipreach.backend.shifts.payload.mapper.ShiftMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static app.ipreach.backend.shared.creation.FakeClass.giveMeAssignment;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftAssignmentService {

    private final ShiftRepository shiftRepository;

    @Transactional
    public ResponseEntity<?> getShiftAssignments(long shiftAssignmentId) {
        var shift = shiftRepository.findById(shiftAssignmentId).orElseThrow(
            () -> new RequestException(HttpStatus.BAD_REQUEST, Messages.ErrorClient.SHIFT_NOT_FOUND));
        return buildResponse(OK, ShiftMapper.MAPPER.toDtoWithAssignments(shift));
    }

    public ResponseEntity<?> createShiftAssignment(ShiftAssignmentDto shiftAssignmentDto) {
        return buildResponse(NOT_IMPLEMENTED, giveMeAssignment(shiftAssignmentDto.shiftId()));
    }
}
