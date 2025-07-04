package app.ipreach.backend.shifts.controller;

import app.ipreach.backend.shifts.payload.dto.ShiftAssignmentDto;
import app.ipreach.backend.shifts.service.ShiftAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static app.ipreach.backend.shared.creation.FakeClass.giveMeAssignment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ShiftAssignmentControllerTest {

    @Mock
    private ShiftAssignmentService shiftAssignmentService;

    @InjectMocks
    private ShiftAssignmentController shiftAssignmentController;

    private ResponseEntity<?> mockResponse;
    private ShiftAssignmentDto shiftAssignmentDto;
    private long shiftAssignmentId;

    @BeforeEach
    void setUp() {
        // Setup test data
        shiftAssignmentId = 1L;
        shiftAssignmentDto = giveMeAssignment(shiftAssignmentId);
        mockResponse = ResponseEntity.ok().build();
    }

    @Test
    void getShiftAssignments_ShouldReturnShiftAssignments() {
        // Arrange
        doReturn(mockResponse).when(shiftAssignmentService).getShiftAssignments(anyLong());

        // Act
        ResponseEntity<?> result = shiftAssignmentController.getShiftAssignments(shiftAssignmentId);

        // Assert
        assertEquals(mockResponse, result);
        verify(shiftAssignmentService).getShiftAssignments(shiftAssignmentId);
    }

    @Test
    void createShiftAssignment_ShouldCreateShiftAssignmentAndReturnCreatedShiftAssignment() {
        // Arrange
        doReturn(mockResponse).when(shiftAssignmentService).createShiftAssignment(any(ShiftAssignmentDto.class));

        // Act
        ResponseEntity<?> result = shiftAssignmentController.createShiftAssignment(shiftAssignmentDto);

        // Assert
        assertEquals(mockResponse, result);
        verify(shiftAssignmentService).createShiftAssignment(shiftAssignmentDto);
    }
}
