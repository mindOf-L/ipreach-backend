package app.ipreach.backend.auth.service;

import app.ipreach.backend.auth.db.repository.TokenRepository;
import app.ipreach.backend.auth.payload.dto.LoginDto;
import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.core.security.jwt.JwtUtils;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.creation.Constructor;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.payload.dto.UserDto;
import app.ipreach.backend.users.payload.mapper.UserMapper;
import app.ipreach.backend.users.service.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;

import static app.ipreach.backend.shared.conversion.Convert.dateToLocalDateTime;
import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    public static final String SAME_SITE_COOKIE_ATTRIBUTE = "SameSite";
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${refresh-token-header}")
    private String refreshTokenHeader;
    @Value("${payload-token-header}")
    private String payloadTokenHeader;
    @Value("${signature-token-header}")
    private String signatureTokenHeader;

    @Value("${server.servlet.session.cookie.secure}")
    private boolean securedCookies;
    @Value("${server.servlet.session.cookie.sameSite}")
    private String sameSiteValue;

    public ResponseEntity<?> loginUser(LoginDto loginDto, HttpServletResponse response) throws ParseException, JOSEException {

        User userEntity = userService.getUserByEmail(loginDto.email());

        validateUserEnabled(userEntity);

        if (!passwordEncoder.matches(loginDto.password(), userEntity.getPassword()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.ErrorClient.USER_PASSWORD_DONT_MATCH);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userEntity.getEmail(), loginDto.password(), userEntity.getRolesAuthorities());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return setToken(userEntity, response);
    }

    public ResponseEntity<?> logout(UserDetailsImpl user, HttpServletResponse response) {
        response.addCookie(deleteCookie(refreshTokenHeader));
        response.addCookie(deleteCookie(payloadTokenHeader));
        response.addCookie(deleteCookie(signatureTokenHeader));

        tokenRepository.deleteAllTokensFromUser(user.getId());

        return buildResponse(OK, Messages.Info.USER_LOGGED_OUT);
    }

    public ResponseEntity<?> getUser(Authentication authentication) {
        var user = userService.getUserByEmail(authentication.getName());
        return buildResponse(OK, UserMapper.MAPPER.toDTO(user));
    }

    private ResponseEntity<?> setToken(User user, HttpServletResponse response) throws ParseException, JOSEException {
        //HttpHeaders headers = new HttpHeaders();

        SignedJWT refreshToken = jwtUtils.generateRefreshToken(user);
        //headers.add(refreshTokenHeader, refreshToken.getParsedString());

        SignedJWT jwt = jwtUtils.generateRegularToken(user);
        //headers.add(payloadTokenHeader, String.format("%s.%s", jwt.getParsedParts()[0], jwt.getParsedParts()[1]));
        //headers.add(signatureTokenHeader, jwt.getParsedParts()[2].toString());

        // HttpServletRequest actualRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        response.addCookie(createCookie(refreshTokenHeader, refreshToken.getParsedString()));
        response.addCookie(createCookie(payloadTokenHeader, String.format("%s.%s", jwt.getParsedParts()[0], jwt.getParsedParts()[1])));
        response.addCookie(createCookie(signatureTokenHeader, jwt.getParsedParts()[2].toString()));

        UserDto userDto = UserMapper.MAPPER.toDTO(user).toBuilder()
            .tokenExpires(dateToLocalDateTime(jwt.getJWTClaimsSet().getExpirationTime()))
            .refreshExpires(dateToLocalDateTime(refreshToken.getJWTClaimsSet().getExpirationTime()))
            .build();

        return Constructor.buildResponse(HttpStatus.OK, userDto);
    }

    private void validateUserEnabled(User user) {
        if (Boolean.FALSE.equals(user.isApproved())) {
            tokenRepository.deleteAllTokensFromUser(user.getId());
            throw new RequestException(HttpStatus.UNAUTHORIZED, Messages.ErrorClient.USER_NOT_ENABLED);
        }
    }

    private Cookie createCookie(String key, String value) {
        var cookie = getRegularCookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        return cookie;
    }

    private Cookie deleteCookie(String key) {
        var cookie = getRegularCookie(key, null);
        cookie.setMaxAge(0);
        return cookie;
    }

    private Cookie getRegularCookie(String key, String value) {
        var cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setAttribute(SAME_SITE_COOKIE_ATTRIBUTE, sameSiteValue);
        cookie.setSecure(securedCookies);
        cookie.setPath("/api/v1");
        return cookie;
    }
}
