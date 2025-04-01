package app.ipreach.backend.core.exception;

import app.ipreach.backend.core.exception.custom.ApiError;
import app.ipreach.backend.core.exception.custom.ApiSubError;
import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.creation.Constructor;
import app.ipreach.backend.shared.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static app.ipreach.backend.core.exception.ExceptionLogger.logErrorInConsole;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

        final String exceptionClassName = ex.getClass().getSimpleName();
        final String classFailing = ex.getStackTrace()[0].getFileName();
        final String methodFailing = ex.getStackTrace()[0].getMethodName();
        final int lineFailing = ex.getStackTrace()[0].getLineNumber();

        ApiError apiError = ApiError.builder()
            .id(UUID.fromString(MDC.get("petitionId")))
            .timestamp(LocalDateTime.now())
            .endpointPath(MDC.get("petitionEndpoint"))
            .exceptionClazzThrown(exceptionClassName)
            .clazz(classFailing)
            .method(methodFailing)
            .line(lineFailing)
            .build();

        var responseBody = ResponseDto.builder()
            .status(BAD_REQUEST.name())
            .httpStatus(BAD_REQUEST.getReasonPhrase())
            .code(BAD_REQUEST.value())
            .data(apiError)
            .message("Validation failed")
            .build();

        if(ex instanceof MethodArgumentNotValidException e) {
            var subErrors = e.getBindingResult().getAllErrors().stream()
                .map(error ->
                    ApiSubError.builder()
                        .field(((FieldError) error).getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(((FieldError) error).getRejectedValue())
                        .build())
                .toList();

            var completedApiError = apiError.toBuilder().errors(subErrors).build();
            var completedResponseBody = responseBody.toBuilder().data(completedApiError).build();

            return new ResponseEntity<>(completedResponseBody, new HttpHeaders(), responseBody.code());
        }

        return new ResponseEntity<>(responseBody, new HttpHeaders(), responseBody.code());
    }

    @ExceptionHandler({RequestException.class, RequestRejectedException.class})
    public final ResponseEntity<?> handleRequestExceptions(RequestException ex) {

        if (ex.getData() != null)
            return Constructor.buildResponse(ex.getHttpStatus(), ex.getData(), ex.getMessage());

        if (StringUtils.isNotEmpty(ex.getLocation())) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", ex.getLocation());
            return Constructor.buildResponseHeaders(ex.getHttpStatus(), ex.getData(), ex.getMessage(), headers);
        }

        logErrorInConsole("RequestException", ex, BAD_REQUEST, Messages.ErrorClient.RUNTIME_EXCEPTION);

        return Constructor.buildResponseHeaders(ex.getHttpStatus(), ex.getData(), ex.getMessage(), new HttpHeaders());
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public final ResponseEntity<?> handleRequestExceptions(Exception ex) {
        return Constructor.buildResponseHeaders(HttpStatus.UNAUTHORIZED, null, ex.getMessage(), new HttpHeaders());
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleAll(Exception ex) {
        logErrorInConsole(ex.getClass().getName(), ex, INTERNAL_SERVER_ERROR, Messages.ErrorDev.GENERIC_ERROR);
        return Constructor.buildResponse(INTERNAL_SERVER_ERROR, buildApiErrorFromStackTrace(ex), Messages.ErrorDev.GENERIC_ERROR);
    }

    public static ApiError buildApiErrorFromStackTrace(Exception ex) {
        // root cause
        var stackTraceBackend = Arrays.stream(ex.getStackTrace()).filter(stack -> stack.getClassName().contains("app.ipreach.backend")).toList().getLast();

        final String rootClassFailing = stackTraceBackend.getFileName();
        final String rootMethodFailing = stackTraceBackend.getMethodName();
        final int rootLineFailing = stackTraceBackend.getLineNumber();

        ApiError rootCause = ApiError.builder()
            .clazz(rootClassFailing)
            .method(rootMethodFailing)
            .line(rootLineFailing)
            .build();

        // final cause
        final String exceptionClassName = ex.getClass().getSimpleName();
        final String classFailing = ex.getStackTrace()[0].getFileName();
        final String methodFailing = ex.getStackTrace()[0].getMethodName();
        final int lineFailing = ex.getStackTrace()[0].getLineNumber();

        return ApiError.builder()
            .id(UUID.fromString(MDC.get("petitionId")))
            .timestamp(LocalDateTime.now())
            .endpointPath(MDC.get("petitionEndpoint"))
            .exceptionClazzThrown(exceptionClassName)
            .clazz(classFailing)
            .method(methodFailing)
            .line(lineFailing)
            .rootCause(rootCause)
            .build();
    }
}
