package app.ipreach.backend.core.exception.custom;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiSubError(String object, String field, Object rejectedValue, String message) { }
