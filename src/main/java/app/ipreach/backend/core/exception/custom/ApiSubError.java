package app.ipreach.backend.core.exception.custom;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiSubError {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String object;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String field;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Object rejectedValue;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

}
