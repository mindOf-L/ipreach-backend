package app.ipreach.backend.users.controller;

import app.ipreach.backend.auth.payload.dto.CredentialsDto;
import app.ipreach.backend.core.security.user.CurrentUser;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.users.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @Profile("!pro")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
        @PathVariable long userId, @Valid @RequestBody CredentialsDto credentialsDto) {
        return userService.updateUser(userId, credentialsDto);
    }

}
