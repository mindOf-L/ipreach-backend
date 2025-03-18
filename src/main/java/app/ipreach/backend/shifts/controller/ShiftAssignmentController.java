package app.ipreach.backend.shifts.controller;

import app.ipreach.backend.shifts.payload.dto.ShiftAssignmentDto;
import app.ipreach.backend.shifts.service.ShiftAssignmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static app.ipreach.backend.shared.creation.FakeClass.giveMeAssignment;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequestMapping("/shifts/assignments")
@RequiredArgsConstructor
@Tag(name = "Shift assignments", description = "Shift assignment methods to create, list and filter")
public class ShiftAssignmentController {

    private final ShiftAssignmentService shiftAssignmentService;

    @GetMapping("/{shiftAssignmentId}")
    public ResponseEntity<?> listShifts(@PathVariable long shiftAssignmentId) {

        return buildResponse(OK, giveMeAssignment(shiftAssignmentId));
    }

    @PostMapping
    public ResponseEntity<?> listShifts(@RequestBody ShiftAssignmentDto shiftAssignmentDto) {

        return buildResponse(OK, giveMeAssignment(shiftAssignmentDto.shiftId()));
    }

}
