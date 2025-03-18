package app.ipreach.backend.users.controller;

import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.core.security.user.CurrentUser;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.db.repository.UserRepository;
import app.ipreach.backend.users.payload.mapper.UserMapper;
import app.ipreach.backend.users.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "User CRUD")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMyUser(@CurrentUser UserDetailsImpl currentUser) {
        User userEntity = userRepository.findById(currentUser.getId()).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.ErrorClient.USER_NOT_FOUND));
        return buildResponse(HttpStatus.OK, UserMapper.MAPPER.toDTO(userEntity));
    }

}
