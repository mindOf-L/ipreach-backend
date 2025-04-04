package app.ipreach.backend.users.service;

import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.db.repository.UserRepository;
import app.ipreach.backend.users.payload.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRepositoryService {

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.ErrorClient.USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.ErrorClient.USER_NOT_FOUND));
    }

    public void checkUserExistsByEmailOrPhone(UserDto userDto) {
        if (userRepository.existsByEmailOrPhone(userDto.email(), userDto.phone()))
            throw new RequestException(BAD_REQUEST,
                Messages.ErrorClient.USER_WITH_THIS_EMAIL_OR_PHONE_EXISTS,
                userDto.toBuilder().password(null).build());
    }

    public User saveUser(User user) {
        return userRepository.saveAndFlush(user);
    }

    public void deleteById(long userId) {
        userRepository.deleteById(userId);
    }

    public boolean existByEmail(String email) {
        return userRepository.existsByEmailOrPhone(email, null);
    }
}
