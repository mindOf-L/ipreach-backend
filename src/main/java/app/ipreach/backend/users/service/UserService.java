package app.ipreach.backend.users.service;

import app.ipreach.backend.auth.payload.dto.CredentialsDto;
import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.db.repository.UserRepository;
import app.ipreach.backend.users.payload.dto.UserDto;
import app.ipreach.backend.users.payload.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> getMyUser(UserDetailsImpl currentUser) {
        return getUser(currentUser.getId());
    }

    public ResponseEntity<?> getUser(Long userId) {
        User userEntity = userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.ErrorClient.USER_NOT_FOUND));
        return buildResponse(OK, UserMapper.MAPPER.toDTO(userEntity));
    }

    public ResponseEntity<?> createUser(UserDto userDto) {
        if(userRepository.existsByEmailOrPhone(userDto.email(), userDto.phone()))
            throw new RequestException(BAD_REQUEST,
                Messages.ErrorClient.USER_WITH_THIS_EMAIL_OR_PHONE_EXISTS,
                userDto.toBuilder().password(null).build());

        var user = UserMapper.MAPPER.toEntity(userDto);
        user = userRepository.saveAndFlush(user);

        return buildResponse(CREATED, UserMapper.MAPPER.toDTO(user), Messages.Info.USER_CREATED);
    }

    public ResponseEntity<?> updateUser(long userId, CredentialsDto credentialsDto) {
        var userEntity = getUserById(userId);

        userEntity.setEmail(credentialsDto.email());
        userEntity.setPassword(passwordEncoder.encode(credentialsDto.password()));

        userRepository.save(userEntity);

        return buildResponse(CREATED, UserMapper.MAPPER.toDTO(userEntity), Messages.Info.USER_UPDATED);
    }

    public ResponseEntity<?> deleteUser(long userId) {
        userRepository.deleteById(userId);
        return buildResponse(OK, Messages.Info.USER_DELETED);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.ErrorClient.USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.ErrorClient.USER_NOT_FOUND));
    }

}
