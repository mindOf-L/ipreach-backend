package app.ipreach.backend.users.controller;

import app.ipreach.backend.auth.payload.dto.CredentialsDto;
import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.core.security.user.CurrentUser;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.users.payload.dto.UserDto;
import app.ipreach.backend.users.service.UserService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;

import static app.ipreach.backend.shared.constants.Authorities.Role.ADMIN_LEVEL;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "User CRUD")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyUser(@CurrentUser UserDetailsImpl currentUser) {
        return userService.getMyUser(currentUser);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyUser(@CurrentUser UserDetailsImpl currentUser, @Valid @RequestBody CredentialsDto credentialsDto, HttpServletResponse response)
        throws ParseException, JOSEException {
        return userService.updateMyUser(currentUser.getId(), credentialsDto, response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @Profile("!pro")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable long userId, @Valid @RequestBody CredentialsDto credentialsDto, HttpServletResponse response) throws ParseException, JOSEException {
        return userService.updateUser(userId, credentialsDto, response);
    }

    @Profile("!pro")
    @DeleteMapping("/{userId}")
    @PreAuthorize(ADMIN_LEVEL)
    public ResponseEntity<?> deleteUser(@CurrentUser UserDetailsImpl currentUser, @PathVariable long userId) {
        if (currentUser.getId() == userId)
            throw new RequestException(BAD_REQUEST, Messages.ErrorClient.USER_ADMIN_CANNOT_DELETE_OWN_ACCOUNT);

        return userService.deleteUser(userId);
    }

}
