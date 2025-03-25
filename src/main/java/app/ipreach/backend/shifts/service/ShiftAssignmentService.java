package app.ipreach.backend.shifts.service;

import app.ipreach.backend.shifts.db.repository.ShiftAssigmentRepository;
import app.ipreach.backend.shifts.payload.dto.ShiftAssignmentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static app.ipreach.backend.shared.creation.FakeClass.giveMeAssignment;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftAssignmentService {

    private final ShiftAssigmentRepository shiftAssigmentRepository;

    public ResponseEntity<?> listShifts(long shiftAssignmentId) {
        return buildResponse(OK, giveMeAssignment(shiftAssignmentId));
    }

    public ResponseEntity<?> listShifts(ShiftAssignmentDto shiftAssignmentDto) {
        return buildResponse(OK, giveMeAssignment(shiftAssignmentDto.shiftId()));
    }
}
