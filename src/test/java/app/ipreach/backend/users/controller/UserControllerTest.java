package app.ipreach.backend.users.controller;

import app.ipreach.backend.auth.payload.dto.CredentialsDto;
import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.users.payload.dto.UserDto;
import app.ipreach.backend.users.service.UserService;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.ParseException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserController userController;

    private UserDetailsImpl currentUser;
    private UserDto userDto;
    private CredentialsDto credentialsDto;
    private ResponseEntity<?> mockResponse;

    @BeforeEach
    void setUp() {
        // Create a UserDetails object
        UserDetails userDetails = new User(
            "test@example.com", 
            "password", 
            true, 
            true, 
            true, 
            true, 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Setup test data
        currentUser = new UserDetailsImpl(userDetails, 1L);

        userDto = UserDto.builder()
            .id(1L)
            .email("test@example.com")
            .name("Test User")
            .roles(Collections.singletonList(ERole.ROLE_USER))
            .build();

        credentialsDto = new CredentialsDto("test@example.com", "password");
        mockResponse = ResponseEntity.ok().build();
    }

    @Test
    void getMyUser_ShouldReturnUserDetails() {
        // Arrange
        doReturn(mockResponse).when(userService).getMyUser(any(UserDetailsImpl.class));

        // Act
        ResponseEntity<?> result = userController.getMyUser(currentUser);

        // Assert
        assertEquals(mockResponse, result);
        verify(userService).getMyUser(currentUser);
    }

    @Test
    void updateMyUser_ShouldUpdateUserAndReturnUpdatedDetails() throws ParseException, JOSEException {
        // Arrange
        doReturn(mockResponse).when(userService).updateMyUser(
            anyLong(), any(CredentialsDto.class), any(HttpServletResponse.class));

        // Act
        ResponseEntity<?> result = userController.updateMyUser(currentUser, credentialsDto, response);

        // Assert
        assertEquals(mockResponse, result);
        verify(userService).updateMyUser(currentUser.getId(), credentialsDto, response);
    }

    @Test
    void getUser_ShouldReturnUserDetails() {
        // Arrange
        long userId = 1L;
        doReturn(mockResponse).when(userService).getUser(userId);

        // Act
        ResponseEntity<?> result = userController.getUser(userId);

        // Assert
        assertEquals(mockResponse, result);
        verify(userService).getUser(userId);
    }

    @Test
    void createUser_ShouldCreateUserAndReturnCreatedUser() {
        // Arrange
        doReturn(mockResponse).when(userService).createUser(any(UserDto.class));

        // Act
        ResponseEntity<?> result = userController.createUser(userDto);

        // Assert
        assertEquals(mockResponse, result);
        verify(userService).createUser(userDto);
    }

    @Test
    void updateUser_ShouldUpdateUserAndReturnUpdatedDetails() throws ParseException, JOSEException {
        // Arrange
        long userId = 1L;
        doReturn(mockResponse).when(userService).updateUser(
            anyLong(), any(CredentialsDto.class), any(HttpServletResponse.class));

        // Act
        ResponseEntity<?> result = userController.updateUser(userId, credentialsDto, response);

        // Assert
        assertEquals(mockResponse, result);
        verify(userService).updateUser(userId, credentialsDto, response);
    }

    @Test
    void deleteUser_ShouldDeleteUserAndReturnSuccess() {
        // Arrange
        long userId = 2L; // Different from currentUser.getId()
        doReturn(mockResponse).when(userService).deleteUser(userId);

        // Act
        ResponseEntity<?> result = userController.deleteUser(currentUser, userId);

        // Assert
        assertEquals(mockResponse, result);
        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUser_WhenDeletingOwnAccount_ShouldThrowException() {
        // Arrange
        long userId = currentUser.getId(); // Same as currentUser.getId()

        // Act & Assert
        RequestException exception = assertThrows(RequestException.class, () -> 
            userController.deleteUser(currentUser, userId)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(Messages.ErrorClient.USER_ADMIN_CANNOT_DELETE_OWN_ACCOUNT, exception.getMessage());
        verify(userService, never()).deleteUser(anyLong());
    }
}
