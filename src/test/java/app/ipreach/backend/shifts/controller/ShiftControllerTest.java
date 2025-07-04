package app.ipreach.backend.shifts.controller;

import app.ipreach.backend.shifts.payload.dto.ShiftDto;
import app.ipreach.backend.shifts.service.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static app.ipreach.backend.shared.creation.FakeClass.giveMeShift;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ShiftControllerTest {

    @Mock
    private ShiftService shiftService;

    @InjectMocks
    private ShiftController shiftController;

    private ResponseEntity<?> mockResponse;
    private ShiftDto shiftDto;
    private List<ShiftDto> shiftDtoList;
    private long locationId;
    private long shiftId;

    @BeforeEach
    void setUp() {
        // Setup test data
        locationId = 1L;
        shiftId = 1L;
        shiftDto = giveMeShift(shiftId);
        shiftDtoList = new ArrayList<>();
        shiftDtoList.add(shiftDto);
        shiftDtoList.add(giveMeShift(2L));
        
        mockResponse = ResponseEntity.ok().build();
    }

    @Test
    void listShifts_WithLocationIdOnly_ShouldReturnShifts() {
        // Arrange
        doReturn(mockResponse).when(shiftService).listShifts(anyLong(), isNull(), isNull(), anyBoolean());

        // Act
        ResponseEntity<?> result = shiftController.listShifts(locationId, null, null, false);

        // Assert
        assertEquals(mockResponse, result);
        verify(shiftService).listShifts(locationId, null, null, false);
    }

    @Test
    void listShifts_WithYearMonth_ShouldReturnShifts() {
        // Arrange
        YearMonth yearMonth = YearMonth.now();
        doReturn(mockResponse).when(shiftService).listShifts(anyLong(), any(YearMonth.class), isNull(), anyBoolean());

        // Act
        ResponseEntity<?> result = shiftController.listShifts(locationId, yearMonth, null, false);

        // Assert
        assertEquals(mockResponse, result);
        verify(shiftService).listShifts(locationId, yearMonth, null, false);
    }

    @Test
    void listShifts_WithDate_ShouldReturnShifts() {
        // Arrange
        LocalDate date = LocalDate.now();
        doReturn(mockResponse).when(shiftService).listShifts(anyLong(), isNull(), any(LocalDate.class), anyBoolean());

        // Act
        ResponseEntity<?> result = shiftController.listShifts(locationId, null, date, false);

        // Assert
        assertEquals(mockResponse, result);
        verify(shiftService).listShifts(locationId, null, date, false);
    }

    @Test
    void listShifts_WithYearMonthAndDate_ShouldIgnoreYearMonth() {
        // Arrange
        YearMonth yearMonth = YearMonth.now();
        LocalDate date = LocalDate.now();
        doReturn(mockResponse).when(shiftService).listShifts(anyLong(), isNull(), any(LocalDate.class), anyBoolean());

        // Act
        ResponseEntity<?> result = shiftController.listShifts(locationId, yearMonth, date, false);

        // Assert
        assertEquals(mockResponse, result);
        // Verify that yearMonth is set to null when both yearMonth and date are provided
        verify(shiftService).listShifts(locationId, null, date, false);
    }

    @Test
    void listShifts_WithDetailed_ShouldReturnDetailedShifts() {
        // Arrange
        doReturn(mockResponse).when(shiftService).listShifts(anyLong(), isNull(), isNull(), eq(true));

        // Act
        ResponseEntity<?> result = shiftController.listShifts(locationId, null, null, true);

        // Assert
        assertEquals(mockResponse, result);
        verify(shiftService).listShifts(locationId, null, null, true);
    }

    @Test
    void getShiftsSummary_ShouldReturnShiftsSummary() {
        // Arrange
        String yearMonth = "2023-01";
        doReturn(mockResponse).when(shiftService).getShiftsSummary(anyString(), anyLong());

        // Act
        ResponseEntity<?> result = shiftController.getShiftsSummary(yearMonth, locationId);

        // Assert
        assertEquals(mockResponse, result);
        verify(shiftService).getShiftsSummary(yearMonth, locationId);
    }

    @Test
    void getShift_ShouldReturnShift() {
        // Arrange
        doReturn(mockResponse).when(shiftService).getShift(anyLong());

        // Act
        ResponseEntity<?> result = shiftController.getShift(shiftId);

        // Assert
        assertEquals(mockResponse, result);
        verify(shiftService).getShift(shiftId);
    }

    @Test
    void createShiftList_ShouldCreateShiftsAndReturnCreatedShifts() {
        // Arrange
        doReturn(mockResponse).when(shiftService).createShiftList(anyList());

        // Act
        ResponseEntity<?> result = shiftController.createShiftList(shiftDtoList);

        // Assert
        assertEquals(mockResponse, result);
        verify(shiftService).createShiftList(shiftDtoList);
    }
}
