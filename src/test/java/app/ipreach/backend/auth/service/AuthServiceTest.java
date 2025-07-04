package app.ipreach.backend.auth.service;

import app.ipreach.backend.auth.db.repository.TokenRepository;
import app.ipreach.backend.auth.payload.dto.CredentialsDto;
import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.core.security.jwt.JwtUtils;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.payload.dto.UserDto;
import app.ipreach.backend.users.service.UserRepositoryService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepositoryService userRepositoryService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private SignedJWT signedJWT;

    @Mock
    private SignedJWT refreshToken;

    @InjectMocks
    private AuthService authService;

    private User user;
    private UserDto userDto;
    private CredentialsDto credentialsDto;
    private UserDetailsImpl userDetails;
    private String email;
    private String password;
    private String refreshTokenHeader;
    private String payloadTokenHeader;
    private String signatureTokenHeader;

    @BeforeEach
    void setUp() throws Exception {
        // Set up values for @Value annotations
        refreshTokenHeader = "refresh-token";
        payloadTokenHeader = "payload-token";
        signatureTokenHeader = "signature-token";
        ReflectionTestUtils.setField(authService, "refreshTokenHeader", refreshTokenHeader);
        ReflectionTestUtils.setField(authService, "payloadTokenHeader", payloadTokenHeader);
        ReflectionTestUtils.setField(authService, "signatureTokenHeader", signatureTokenHeader);
        ReflectionTestUtils.setField(authService, "securedCookies", true);
        ReflectionTestUtils.setField(authService, "sameSiteCookies", "None");

        // Set up test data
        email = "test@example.com";
        password = "Password1!";
        credentialsDto = new CredentialsDto(email, password);

        List<ERole> roles = new ArrayList<>();
        roles.add(ERole.ROLE_USER);

        user = User.builder()
            .id(1L)
            .email(email)
            .password("encodedPassword")
            .name("Test User")
            .roles(roles)
            .approved(true)
            .build();

        userDto = UserDto.builder()
            .id(1L)
            .email(email)
            .name("Test User")
            .roles(roles)
            .approved(true)
            .build();

        // Create a UserDetailsImpl
        org.springframework.security.core.userdetails.User userDetailsObj = new org.springframework.security.core.userdetails.User(
            email,
            password,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        userDetails = new UserDetailsImpl(userDetailsObj, 1L);

        // Mock JWT token generation - use lenient() to avoid UnnecessaryStubbingException
        lenient().when(jwtUtils.generateRegularToken(any(User.class))).thenReturn(signedJWT);
        lenient().when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn(refreshToken);

        // Mock JWT parts
        Base64URL[] parts = new Base64URL[]{
            new Base64URL("header"),
            new Base64URL("payload"),
            new Base64URL("signature")
        };
        lenient().doReturn(parts).when(signedJWT).getParsedParts();
        lenient().when(refreshToken.getParsedString()).thenReturn("refreshTokenString");

        // Mock JWT claims
        lenient().when(signedJWT.getJWTClaimsSet()).thenReturn(mock(com.nimbusds.jwt.JWTClaimsSet.class));
        lenient().when(refreshToken.getJWTClaimsSet()).thenReturn(mock(com.nimbusds.jwt.JWTClaimsSet.class));
        lenient().when(signedJWT.getJWTClaimsSet().getExpirationTime()).thenReturn(new Date());
        lenient().when(refreshToken.getJWTClaimsSet().getExpirationTime()).thenReturn(new Date());
    }

    @Test
    void loginUser_WithValidCredentials_ShouldReturnTokens() throws ParseException, JOSEException {
        // Arrange
        when(userRepositoryService.getUserByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // Act
        ResponseEntity<?> result = authService.loginUser(credentialsDto, response);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userRepositoryService).getUserByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateRegularToken(user);
        verify(jwtUtils).generateRefreshToken(user);
        verify(response, times(3)).addCookie(any(Cookie.class));
    }

    @Test
    void loginUser_WithInvalidPassword_ShouldThrowException() {
        // Arrange
        when(userRepositoryService.getUserByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // Act & Assert
        RequestException exception = assertThrows(RequestException.class, () -> 
            authService.loginUser(credentialsDto, response)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(Messages.ErrorClient.USER_PASSWORD_DONT_MATCH, exception.getMessage());
        verify(userRepositoryService).getUserByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void loginUser_WithDisabledUser_ShouldThrowException() {
        // Arrange
        user.setApproved(false);
        when(userRepositoryService.getUserByEmail(email)).thenReturn(user);

        // Act & Assert
        RequestException exception = assertThrows(RequestException.class, () -> 
            authService.loginUser(credentialsDto, response)
        );
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
        assertEquals(Messages.ErrorClient.USER_NOT_ENABLED, exception.getMessage());
        verify(userRepositoryService).getUserByEmail(email);
        verify(tokenRepository).deleteAllTokensFromUser(user.getId());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void logout_ShouldClearCookiesAndDeleteTokens() {
        // Act
        ResponseEntity<?> result = authService.logout(userDetails, response);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(response, times(3)).addCookie(any(Cookie.class));
        verify(tokenRepository).deleteAllTokensFromUser(userDetails.getId());
    }

    @Test
    void getUser_ShouldReturnUserDetails() {
        // Arrange
        when(authentication.getName()).thenReturn(email);
        when(userRepositoryService.getUserByEmail(email)).thenReturn(user);

        // Act
        ResponseEntity<?> result = authService.getUser(authentication);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userRepositoryService).getUserByEmail(email);
    }

    @Test
    void revokeAuthentication_ShouldDeleteAllTokens() {
        // Act
        authService.revokeAuthentication(user);

        // Assert
        verify(tokenRepository).deleteAllTokensFromUser(user.getId());
    }

    @Test
    void renewAuthentication_ShouldDeleteOldTokensAndCreateNewOnes() throws ParseException, JOSEException {
        // Act
        UserDto result = authService.renewAuthentication(user, response);

        // Assert
        assertNotNull(result);
        verify(tokenRepository).deleteAllTokensFromUser(user.getId());
        verify(jwtUtils).generateRegularToken(user);
        verify(jwtUtils).generateRefreshToken(user);
        verify(response, times(3)).addCookie(any(Cookie.class));
    }

    @Test
    void createToken_ShouldCreateTokensAndAddCookies() throws ParseException, JOSEException {
        // Act
        UserDto result = authService.createToken(user, response);

        // Assert
        assertNotNull(result);
        verify(jwtUtils).generateRegularToken(user);
        verify(jwtUtils).generateRefreshToken(user);
        verify(response, times(3)).addCookie(any(Cookie.class));
    }
}
