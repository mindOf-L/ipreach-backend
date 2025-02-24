package app.ipreach.backend.app.security.jwt;

import app.ipreach.backend.app.exception.custom.RequestException;
import app.ipreach.backend.app.security.user.UserDetailsImpl;
import app.ipreach.backend.app.security.user.UserDetailsServiceImpl;
import app.ipreach.backend.shared.constants.Messages;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;

import static app.ipreach.backend.shared.validation.Endpoint.isNotReplaceTokenEndpoint;
import static app.ipreach.backend.shared.validation.Endpoint.isTokenEndpoint;
import static app.ipreach.backend.shared.validation.Endpoint.nonTokenEndpoint;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${app.auth.jwtExpiration:86400}")
    private int jwtExpiration;

    @Value("${refresh-token-header}")
    private String refreshTokenHeader;

    @Value("${payload-token-header}")
    private String payloadTokenHeader;

    @Value("${signature-token-header}")
    private String signatureTokenHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        final String refreshJwt = request.getHeader(refreshTokenHeader);
        final String jwtHeaderPayload = request.getHeader(payloadTokenHeader);
        final String jwtSignature = request.getHeader(signatureTokenHeader);

        final boolean authIsNotPresent = StringUtils.isBlank(jwtHeaderPayload) || StringUtils.isBlank(jwtSignature);

        if (authIsNotPresent || nonTokenEndpoint(request.getMethod(), request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = String.format("Bearer %s.%s", jwtHeaderPayload, jwtSignature);

        SignedJWT signedJWT = jwtUtils.getDecodedJwt(jwt);

        if(!jwtUtils.verifyJwt(signedJWT))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.ErrorClient.TOKEN_INVALID);

        String userEmail = jwtUtils.getEmailFromJwtToken(signedJWT);

        UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(userEmail);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
            null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String authorization = "Authorization";
        request.setAttribute(authorization, jwt);
        if (isTokenEndpoint(request.getMethod(), request.getRequestURI())) {
            response.setHeader(payloadTokenHeader, jwtHeaderPayload);
            response.setHeader(signatureTokenHeader, jwtSignature);
        }

        if (isTokenEndpoint(request.getMethod(), request.getRequestURI()) &&
            isNotReplaceTokenEndpoint(request.getMethod(), request.getRequestURI())) {
            response.setHeader(refreshTokenHeader, refreshJwt);
            response.setHeader(payloadTokenHeader, jwtHeaderPayload);
            response.setHeader(signatureTokenHeader, jwtSignature);
            final String expires = "Expires";
            try {
                response.setHeader(expires, signedJWT.getJWTClaimsSet().getExpirationTime().toString());
            } catch (ParseException _) {
                response.setHeader(expires, LocalDateTime.now().plusSeconds(jwtExpiration).toString());
            }
        }
        filterChain.doFilter(request, response);

    }

}
