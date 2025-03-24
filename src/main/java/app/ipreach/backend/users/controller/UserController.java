package app.ipreach.backend.users.controller;

import app.ipreach.backend.core.security.user.CurrentUser;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import app.ipreach.backend.users.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

}
