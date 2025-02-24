package app.ipreach.backend.controller.auth;

import app.ipreach.backend.app.exception.custom.RequestException;
import app.ipreach.backend.app.security.jwt.JwtUtils;
import app.ipreach.backend.db.model.User;
import app.ipreach.backend.db.repository.TokenRepository;
import app.ipreach.backend.db.repository.UserRepository;
import app.ipreach.backend.mapper.dto.auth.LoginDto;
import app.ipreach.backend.mapper.dto.user.UserDto;
import app.ipreach.backend.mapper.payload.user.UserMapper;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.creation.Constructor;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;

import static app.ipreach.backend.shared.conversion.Convert.dateToLocalDateTime;
import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequestMapping("/auth")
@Tag(name = "Authorization", description = "Authorization methods to login, logout, and password management")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${refresh-token-header}")
    private String refreshTokenHeader;

    @Value("${payload-token-header}")
    private String payloadTokenHeader;

    @Value("${signature-token-header}")
    private String signatureTokenHeader;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) throws ParseException, JOSEException {

        User userEntity = userRepository.findByEmail(loginDto.email()).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.ErrorClient.USER_NOT_FOUND));

        validateUserEnabled(userEntity);

        if (!passwordEncoder.matches(loginDto.password(), userEntity.getPassword()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.ErrorClient.USER_PASSWORD_DONT_MATCH);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userEntity.getEmail(), loginDto.password(), userEntity.getRolesAuthorities());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return setToken(userEntity);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return buildResponse(OK, Messages.Info.USER_LOGGED_OUT);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(Authentication authentication) {
        var user = userRepository.findByEmail(authentication.getName()).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.ErrorClient.USER_NOT_FOUND));
        return buildResponse(OK, UserMapper.MAPPER.toDTO(user));
    }

    private ResponseEntity<?> setToken(User user) throws ParseException, JOSEException {
        HttpHeaders headers = new HttpHeaders();

        SignedJWT refreshToken = jwtUtils.generateRefreshToken(user);
        headers.add(refreshTokenHeader, refreshToken.getParsedString());

        SignedJWT jwt = jwtUtils.generateRegularToken(user);
        headers.add(payloadTokenHeader, String.format("%s.%s", jwt.getParsedParts()[0], jwt.getParsedParts()[1]));
        headers.add(signatureTokenHeader, jwt.getParsedParts()[2].toString());

        // HttpServletRequest actualRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        UserDto userDto = UserMapper.MAPPER.toDTO(user).toBuilder()
            .tokenExpires(dateToLocalDateTime(jwt.getJWTClaimsSet().getExpirationTime()))
            .refreshExpires(dateToLocalDateTime(refreshToken.getJWTClaimsSet().getExpirationTime()))
            .build();

        return Constructor.buildResponseHeaders(HttpStatus.OK, userDto, headers);
    }

    private void validateUserEnabled(User user) {
        if (Boolean.FALSE.equals(user.isEnabled())) {
            tokenRepository.deleteAllTokensFromUser(user.getId());
            throw new RequestException(HttpStatus.UNAUTHORIZED, Messages.ErrorClient.USER_NOT_ENABLED);
        }
    }

}
