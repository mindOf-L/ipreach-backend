package app.ipreach.backend.core.exception.custom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError (
    UUID id,
    LocalDateTime timestamp,
    Integer status,
    HttpStatusCode httpStatus,
    String endpointPath,
    @JsonProperty("exceptionClassThrown") String exceptionClazzThrown,
    @JsonProperty("class") String clazz,
    String method,
    Integer line,
    String message,
    ApiError rootCause,
    @Singular("error") @JsonInclude(JsonInclude.Include.NON_EMPTY) List<ApiSubError> errors)
{ }
