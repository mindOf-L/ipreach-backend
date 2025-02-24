package app.ipreach.backend.app.security.jwt;

import app.ipreach.backend.app.exception.custom.RequestException;
import app.ipreach.backend.shared.constants.Messages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static app.ipreach.backend.shared.constants.Messages.ErrorClient.ERROR_TOKEN_NOT_PROVIDED;
import static app.ipreach.backend.shared.constants.Messages.ErrorClient.USER_PARAMETERS_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Unauthorized error: ", authException);
        log.error("Responding with unauthorized error. Message - {}", authException.getMessage());

        Object data = ERROR_TOKEN_NOT_PROVIDED;
        RequestException exception = new RequestException(BAD_REQUEST, USER_PARAMETERS_ERROR, data);

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(exception.getMessage());
    }
}
