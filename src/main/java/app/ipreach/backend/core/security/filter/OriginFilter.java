package app.ipreach.backend.core.security.filter;

import app.ipreach.backend.core.exception.custom.RequestException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
@RequiredArgsConstructor
public class OriginFilter implements Filter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final String nullRequestOriginHeaderError = "Requests without Origin or Referer are not allowed";

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String origin = ((HttpServletRequest) request).getHeader("Origin");
        String referer = ((HttpServletRequest) request).getHeader("Referer");

        try {
            if (ObjectUtils.allNull(origin, referer))
                throw new RequestException(BAD_REQUEST, nullRequestOriginHeaderError);
        } catch (RequestException ex) {
            handlerExceptionResolver.resolveException((HttpServletRequest) request, (HttpServletResponse) response, null, ex);
            return;
        }

        chain.doFilter(request, response);
    }
}
