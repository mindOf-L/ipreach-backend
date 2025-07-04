package app.ipreach.backend.auth.controller;

import app.ipreach.backend.auth.payload.dto.CredentialsDto;
import app.ipreach.backend.auth.service.AuthService;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.ParseException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private UserDetailsImpl userDetails;
    private CredentialsDto credentialsDto;
    private ResponseEntity<?> mockResponse;

    @BeforeEach
    void setUp() {
        // Create a UserDetails object
        UserDetails userDetailsObj = new User(
            "test@example.com", 
            "Password1!", 
            true, 
            true, 
            true, 
            true, 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Setup test data
        userDetails = new UserDetailsImpl(userDetailsObj, 1L);
        credentialsDto = new CredentialsDto("test@example.com", "Password1!");
        mockResponse = ResponseEntity.ok().build();
    }

    @Test
    void loginUser_ShouldAuthenticateAndReturnTokens() throws ParseException, JOSEException {
        // Arrange
        doReturn(mockResponse).when(authService).loginUser(any(CredentialsDto.class), any(HttpServletResponse.class));

        // Act
        ResponseEntity<?> result = authController.loginUser(credentialsDto, response);

        // Assert
        assertEquals(mockResponse, result);
        verify(authService).loginUser(credentialsDto, response);
    }

    @Test
    void logout_ShouldLogoutUserAndClearCookies() {
        // Arrange
        doReturn(mockResponse).when(authService).logout(any(UserDetailsImpl.class), any(HttpServletResponse.class));

        // Act
        ResponseEntity<?> result = authController.logout(userDetails, response);

        // Assert
        assertEquals(mockResponse, result);
        verify(authService).logout(userDetails, response);
    }

    @Test
    void getUser_ShouldReturnUserDetails() {
        // Arrange
        doReturn(mockResponse).when(authService).getUser(any(Authentication.class));

        // Act
        ResponseEntity<?> result = authController.getUser(authentication);

        // Assert
        assertEquals(mockResponse, result);
        verify(authService).getUser(authentication);
    }
}
