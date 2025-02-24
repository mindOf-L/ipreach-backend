package app.ipreach.backend.app.exception.custom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private UUID id;

    private LocalDateTime timestamp;

    private Integer status;

    private HttpStatusCode httpStatus;

    private String endpointPath;

    @JsonProperty("exceptionClassThrown")
    private String exceptionClazzThrown;

    @JsonProperty("class")
    private String clazz;

    private String method;

    private Integer line;

    private String message;

    private ApiError rootCause;

    @Singular("error")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ApiSubError> errors;

}
