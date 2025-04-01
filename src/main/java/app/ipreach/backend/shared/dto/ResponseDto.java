package app.ipreach.backend.shared.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record ResponseDto(String status, String httpStatus, int code, Object data, String message) { }
