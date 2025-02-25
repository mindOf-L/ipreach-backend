package app.ipreach.backend.core.exception;

import app.ipreach.backend.core.exception.custom.ApiError;
import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.creation.Constructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({RequestException.class, RequestRejectedException.class})
    public final ResponseEntity<?> handleRequestExceptions(RequestException ex) {

        if (ex.getData() != null)
            return Constructor.buildResponse(ex.getHttpStatus(), ex.getData(), ex.getMessage());

        if (StringUtils.isNotEmpty(ex.getLocation())) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", ex.getLocation());
            return Constructor.buildResponseHeaders(ex.getHttpStatus(), ex.getData(), ex.getMessage(), headers);
        }

        logErrorInConsole("RequestException", ex, HttpStatus.BAD_REQUEST, Messages.ErrorClient.RUNTIME_EXCEPTION);

        return Constructor.buildResponseHeaders(ex.getHttpStatus(), ex.getData(), ex.getMessage(), new HttpHeaders());
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleAll(Exception ex) {
        logErrorInConsole(ex.getClass().getName(), ex, HttpStatus.INTERNAL_SERVER_ERROR, Messages.ErrorDev.GENERIC_ERROR);
        return Constructor.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, buildApiErrorFromStackTrace(ex), Messages.ErrorDev.GENERIC_ERROR);
    }

    private void logErrorInConsole(String exceptionParent, Exception ex, HttpStatus status, String errorMessage) {
        String causedBy = String.format("%s -> %s", ex.getStackTrace()[0].getFileName(), ex.getStackTrace()[0].getMethodName());

        String exceptionClass = ex.getClass().getSimpleName();

        log.error("[{}]::[{}]:: {} -> {}: {}. Error message: {}", MDC.get("petitionId"), status.value(), exceptionParent, exceptionClass, ex.getLocalizedMessage(), errorMessage);
        log.error("[{}]::[{}]:: Caused by: {}", MDC.get("petitionId"), HttpStatus.INTERNAL_SERVER_ERROR.value(), causedBy);
    }

    private ApiError buildApiErrorFromStackTrace(Exception ex) {
        // root cause
        var stackTraceBackend = Arrays.stream(ex.getStackTrace()).filter(stack -> stack.getClassName().contains("app.ipreach.backend")).toList().getLast();

        final String rootClassFailing = stackTraceBackend.getFileName();
        final String rootMethodFailing = stackTraceBackend.getMethodName();
        final int rootLineFailing = stackTraceBackend.getLineNumber();

        final ApiError rootCause = ApiError.builder()
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
