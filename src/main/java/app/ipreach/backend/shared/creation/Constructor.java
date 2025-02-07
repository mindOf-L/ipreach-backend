package app.ipreach.backend.shared.creation;

import app.ipreach.backend.dto.shared.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class Constructor {

    public static ResponseEntity<?> buildResponse(HttpStatus httpStatus, Object body) {
        return ResponseEntity.status(httpStatus).body(buildResponseDto(httpStatus, body, null));
    }

    public static ResponseEntity<?> buildResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus).body(buildResponseDto(httpStatus, null, message));
    }

    public static ResponseEntity<?> buildResponse(HttpStatus httpStatus, Object body, String message) {
        return ResponseEntity.status(httpStatus).body(buildResponseDto(httpStatus, body, message));
    }

    public static ResponseEntity<?> buildResponseHeaders(HttpStatus httpStatus, Object body, HttpHeaders headers) {
        return ResponseEntity.status(httpStatus).headers(headers).body(body);
    }

    private static ResponseDto buildResponseDto(HttpStatus httpStatus, Object body, String message) {
        return ResponseDto.builder()
            .status(HttpStatus.Series.valueOf(httpStatus.value()).name())
            .httpStatus(httpStatus.getReasonPhrase())
            .code(httpStatus.value())
            .data(body)
            .message(message)
            .build();
    }

}
