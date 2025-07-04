package app.ipreach.backend.users.service;

import app.ipreach.backend.auth.payload.dto.CredentialsDto;
import app.ipreach.backend.auth.service.AuthService;
import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.dto.ResponseDto;
import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.payload.dto.UserDto;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepositoryService userRepositoryService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetailsImpl userDetails;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;
    private CredentialsDto credentialsDto;
    private final Long userId = 1L;
    private final String email = "test@example.com";
    private final String password = "Password1!";
    private final String encodedPassword = "encodedPassword";

    @BeforeEach
    void setUp() {
        // Create a sample user
        List<ERole> roles = new ArrayList<>();
        roles.add(ERole.ROLE_USER);

        user = User.builder()
                .id(userId)
                .email(email)
                .password(encodedPassword)
                .name("Test User")
                .roles(roles)
                .approved(true)
                .build();

        // Create a sample user DTO
        userDto = UserDto.builder()
                .id(userId)
                .email(email)
                .password(password)
                .name("Test User")
                .roles(roles)
                .approved(true)
                .build();

        // Create a sample credentials DTO
        credentialsDto = new CredentialsDto(email, password);

        // Mock UserDetailsImpl
        lenient().when(userDetails.getId()).thenReturn(userId);
    }

    @Test
    void updateMyUser_ShouldUpdateUserAndRenewAuthentication() throws ParseException, JOSEException {
        // Arrange
        when(userRepositoryService.getUserById(userId)).thenReturn(user);
        when(userRepositoryService.existByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepositoryService.saveUser(user)).thenReturn(user);
        when(authService.renewAuthentication(user, response)).thenReturn(userDto);

        // Act
        ResponseEntity<?> result = userService.updateMyUser(userId, credentialsDto, response);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        ResponseDto responseDto = (ResponseDto) result.getBody();
        assertNotNull(responseDto);
        assertEquals(userDto, responseDto.data());
        assertEquals(Messages.Info.USER_UPDATED, responseDto.message());

        // Verify
        verify(userRepositoryService).getUserById(userId);
        verify(userRepositoryService).existByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepositoryService).saveUser(user);
        verify(authService).renewAuthentication(user, response);
    }

    @Test
    void updateMyUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        User existingUser = User.builder().id(2L).email(email).build();
        when(userRepositoryService.existByEmail(email)).thenReturn(true);
        when(userRepositoryService.getUserByEmail(email)).thenReturn(existingUser);

        // Act & Assert
        RequestException exception = assertThrows(RequestException.class, () -> 
            userService.updateMyUser(userId, credentialsDto, response)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(Messages.ErrorClient.USER_WITH_THIS_EMAIL_TAKEN, exception.getMessage());

        // Verify
        verify(userRepositoryService).existByEmail(email);
        verify(userRepositoryService).getUserByEmail(email);
        verify(userRepositoryService, never()).saveUser(any(User.class));
        try {
            verify(authService, never()).renewAuthentication(any(User.class), any(HttpServletResponse.class));
        } catch (ParseException | JOSEException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void updateUser_ForCurrentUser_ShouldUpdateAndRenewAuthentication() throws ParseException, JOSEException {
        // Arrange
        when(userRepositoryService.getUserById(userId)).thenReturn(user);
        when(userRepositoryService.existByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepositoryService.saveUser(user)).thenReturn(user);
        when(authService.renewAuthentication(user, response)).thenReturn(userDto);

        // Mock SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(userId);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<?> result = userService.updateUser(userId, credentialsDto, response);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        ResponseDto responseDto = (ResponseDto) result.getBody();
        assertNotNull(responseDto);
        assertEquals(userDto, responseDto.data());
        assertEquals(Messages.Info.USER_UPDATED, responseDto.message());

        // Verify
        verify(userRepositoryService).getUserById(userId);
        verify(userRepositoryService).existByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepositoryService).saveUser(user);
        verify(authService).revokeAuthentication(user);
        verify(authService).renewAuthentication(user, response);
    }

    @Test
    void deleteUser_ShouldDeleteUserAndReturnSuccessMessage() {
        // Act
        ResponseEntity<?> response = userService.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseDto responseDto = (ResponseDto) response.getBody();
        assertNotNull(responseDto);
        assertEquals(Messages.Info.USER_DELETED, responseDto.message());

        // Verify
        verify(userRepositoryService).deleteById(userId);
    }
}
