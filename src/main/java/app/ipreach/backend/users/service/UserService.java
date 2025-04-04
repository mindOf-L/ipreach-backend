package app.ipreach.backend.users.service;

import app.ipreach.backend.auth.payload.dto.CredentialsDto;
import app.ipreach.backend.auth.service.AuthService;
import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.payload.dto.UserDto;
import app.ipreach.backend.users.payload.mapper.UserMapper;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final UserRepositoryService userRepositoryService;

    public ResponseEntity<?> getMyUser(UserDetailsImpl currentUser) {
        return getUser(currentUser.getId());
    }

    public ResponseEntity<?> updateMyUser(long userId, CredentialsDto credentialsDto, HttpServletResponse response) throws ParseException, JOSEException {
        var userEntity = updateUserCredentials(userId, credentialsDto);

        var userDtoWithCookies = authService.renewAuthentication(userEntity, response);

        return buildResponse(CREATED, userDtoWithCookies, Messages.Info.USER_UPDATED);
    }

    public ResponseEntity<?> getUser(Long userId) {
        User userEntity = userRepositoryService.getUserById(userId);
        return buildResponse(OK, UserMapper.MAPPER.toDTO(userEntity));
    }

    public ResponseEntity<?> createUser(UserDto userDto) {
        userRepositoryService.checkUserExistsByEmailOrPhone(userDto);

        var userEntity = UserMapper.MAPPER.toEntity(userDto);
        userEntity = userRepositoryService.saveUser(userEntity);

        return buildResponse(CREATED, UserMapper.MAPPER.toDTO(userEntity), Messages.Info.USER_CREATED);
    }

    public ResponseEntity<?> updateUser(long userId, CredentialsDto credentialsDto, HttpServletResponse response) throws ParseException, JOSEException {
        var userEntity = updateUserCredentials(userId, credentialsDto);

        authService.revokeAuthentication(userEntity);

        if(getCurrentUser().getId() == userId) {
            var userDtoWithCookies = authService.renewAuthentication(userEntity, response);
            return buildResponse(CREATED, userDtoWithCookies, Messages.Info.USER_UPDATED);
        }

        return buildResponse(CREATED, UserMapper.MAPPER.toDTO(userEntity), Messages.Info.USER_UPDATED);
    }

    public ResponseEntity<?> deleteUser(long userId) {
        userRepositoryService.deleteById(userId);
        return buildResponse(OK, Messages.Info.USER_DELETED);
    }

    private User updateUserCredentials(long userId, CredentialsDto credentialsDto) {
        if (userRepositoryService.existByEmail(credentialsDto.email()) &&
            userRepositoryService.getUserByEmail(credentialsDto.email()).getId() != userId)
            throw new RequestException(BAD_REQUEST, Messages.ErrorClient.USER_WITH_THIS_EMAIL_TAKEN, credentialsDto.email());

        var userEntity = userRepositoryService.getUserById(userId);

        userEntity.setEmail(credentialsDto.email());
        userEntity.setPassword(passwordEncoder.encode(credentialsDto.password()));

        return userRepositoryService.saveUser(userEntity);
    }

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }
        throw new RequestException(BAD_REQUEST, Messages.ErrorClient.USER_NOT_AUTHENTICATED);
    }
}
