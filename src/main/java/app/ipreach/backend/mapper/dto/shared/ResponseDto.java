package app.ipreach.backend.mapper.dto.shared;

import lombok.Builder;

@Builder
public record ResponseDto(String status, String httpStatus, int code, Object data, String message) { }
