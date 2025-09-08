package app.ipreach.backend.users.service;

import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.db.repository.UserRepository;
import app.ipreach.backend.users.payload.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRepositoryService userRepositoryService;

    private User user;
    private UserDto userDto;
    private final Long userId = 1L;
    private final String email = "test@example.com";
    private final String phone = "1234567890";

    @BeforeEach
    void setUp() {
        List<ERole> roles = new ArrayList<>();
        roles.add(ERole.ROLE_USER);
        
        user = User.builder()
                .id(userId)
                .email(email)
                .password("encodedPassword")
                .name("Test User")
                .phone(phone)
                .roles(roles)
                .approved(true)
                .build();

        userDto = UserDto.builder()
                .id(userId)
                .email(email)
                .password("password")
                .name("Test User")
                .phone(phone)
                .roles(roles)
                .approved(true)
                .build();
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userRepositoryService.getUserById(userId);

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_WithNonExistingId_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RequestException exception = assertThrows(RequestException.class, () -> userRepositoryService.getUserById(999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(Messages.ErrorClient.USER_NOT_FOUND, exception.getMessage());

        verify(userRepository).findById(999L);
    }

    @Test
    void getUserByEmail_WithExistingEmail_ShouldReturnUser() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userRepositoryService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserByEmail_WithNonExistingEmail_ShouldThrowException() {
        String nonExistingEmail = "nonexisting@example.com";
        when(userRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        RequestException exception = assertThrows(RequestException.class, () -> userRepositoryService.getUserByEmail(nonExistingEmail));
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(Messages.ErrorClient.USER_NOT_FOUND, exception.getMessage());

        verify(userRepository).findByEmail(nonExistingEmail);
    }

    @Test
    void checkUserExistsByEmailOrPhone_WithExistingUser_ShouldThrowException() {
        when(userRepository.existsByEmailOrPhone(email, phone)).thenReturn(true);

        RequestException exception = assertThrows(RequestException.class, () -> userRepositoryService.checkUserExistsByEmailOrPhone(userDto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(Messages.ErrorClient.USER_WITH_THIS_EMAIL_OR_PHONE_EXISTS, exception.getMessage());

        verify(userRepository).existsByEmailOrPhone(email, phone);
    }

    @Test
    void checkUserExistsByEmailOrPhone_WithNonExistingUser_ShouldNotThrowException() {
        when(userRepository.existsByEmailOrPhone(email, phone)).thenReturn(false);

        assertDoesNotThrow(() -> userRepositoryService.checkUserExistsByEmailOrPhone(userDto));

        verify(userRepository).existsByEmailOrPhone(email, phone);
    }

    @Test
    void saveUser_ShouldReturnSavedUser() {
        when(userRepository.saveAndFlush(user)).thenReturn(user);

        User result = userRepositoryService.saveUser(user);

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepository).saveAndFlush(user);
    }

    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        userRepositoryService.deleteById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void existByEmail_WithExistingEmail_ShouldReturnTrue() {
        when(userRepository.existsByEmailOrPhone(email, null)).thenReturn(true);

        boolean result = userRepositoryService.existByEmail(email);

        assertTrue(result);

        verify(userRepository).existsByEmailOrPhone(email, null);
    }

    @Test
    void existByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        String nonExistingEmail = "nonexisting@example.com";
        when(userRepository.existsByEmailOrPhone(nonExistingEmail, null)).thenReturn(false);

        boolean result = userRepositoryService.existByEmail(nonExistingEmail);

        assertFalse(result);

        verify(userRepository).existsByEmailOrPhone(nonExistingEmail, null);
    }
}
