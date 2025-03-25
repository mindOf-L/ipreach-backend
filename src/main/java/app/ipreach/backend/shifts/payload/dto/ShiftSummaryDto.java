package app.ipreach.backend.shifts.payload.dto;

import java.util.Date;

public record ShiftSummaryDto(

    Date shiftsDate,
    Integer shiftsRegistered,
    Integer shiftsAvailable

){ }
