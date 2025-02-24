package app.ipreach.backend.app.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RequestException extends RuntimeException {

    private HttpStatus httpStatus;
    private Object data;
    private String location;

    public RequestException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public RequestException(HttpStatus _httpStatus, String message) {
        super(message);
        httpStatus = _httpStatus;
    }

    public RequestException(HttpStatus _httpStatus, String message, String _location) {
        super(message);
        httpStatus = _httpStatus;
        location = _location;
    }

    public RequestException(HttpStatus _httpStatus, String message, Object _data) {
        super(message);
        httpStatus = _httpStatus;
        data = _data;
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }


}
