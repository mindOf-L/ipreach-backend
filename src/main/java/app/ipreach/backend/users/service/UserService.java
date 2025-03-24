package app.ipreach.backend.users.service;

import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.db.repository.UserRepository;
import app.ipreach.backend.users.payload.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<?> getMyUser(UserDetailsImpl currentUser) {
        return getUser(currentUser.getId());
    }

    public ResponseEntity<?> getUser(Long userId) {
        User userEntity = userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.ErrorClient.USER_NOT_FOUND));
        return buildResponse(HttpStatus.OK, UserMapper.MAPPER.toDTO(userEntity));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.ErrorClient.USER_NOT_FOUND));
    }

}
