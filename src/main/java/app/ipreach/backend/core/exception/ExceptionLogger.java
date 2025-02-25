package app.ipreach.backend.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

@Slf4j
public class ExceptionLogger {

    public static void logErrorInConsole(String exceptionParent, Exception ex, HttpStatus status, String errorMessage) {
        String causedBy = String.format("%s -> %s", ex.getStackTrace()[0].getFileName(), ex.getStackTrace()[0].getMethodName());
        String exceptionClass = ex.getClass().getSimpleName();
        String petitionId = MDC.get("petitionId");

        log.error("[INIT]::[{}]:: {} -> {}: {}. Petition with id {}. Error message: {}", status.value(), exceptionParent, exceptionClass, ex.getLocalizedMessage(), petitionId, errorMessage);
        log.error("[INIT]::[{}]:: Petition with id {} Caused by: {}", HttpStatus.INTERNAL_SERVER_ERROR.value(), petitionId, causedBy);
    }
}
