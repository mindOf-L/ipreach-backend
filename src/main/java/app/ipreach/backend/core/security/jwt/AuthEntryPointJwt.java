package app.ipreach.backend.core.security.jwt;

import app.ipreach.backend.shared.constants.Messages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import static app.ipreach.backend.core.exception.ExceptionLogger.logErrorInConsole;

@Slf4j
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {

        MDC.put("petitionId", UUID.randomUUID());

        logErrorInConsole("RequestException", ex, HttpStatus.BAD_REQUEST, Messages.ErrorClient.ERROR_CALLING_API);

        response.sendError(HttpServletResponse.SC_BAD_REQUEST, Messages.ErrorClient.ERROR_CALLING_API);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(ex.getMessage());

        //throw new RequestException(BAD_REQUEST, ex.getMessage(), ex);

        //logErrorInConsole(ex.getClass().getName(), ex, HttpStatus.INTERNAL_SERVER_ERROR, Messages.ErrorDev.GENERIC_ERROR);
        //return Constructor.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, buildApiErrorFromStackTrace(ex), Messages.ErrorDev.GENERIC_ERROR);
    }
}
