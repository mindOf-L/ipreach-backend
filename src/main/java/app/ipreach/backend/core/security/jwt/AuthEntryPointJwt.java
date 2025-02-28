package app.ipreach.backend.core.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {

        MDC.put("petitionId", UUID.randomUUID());
        handlerExceptionResolver.resolveException(request, response, null, ex);

    }
}
