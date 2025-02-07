package app.ipreach.backend.controller.auth;

import app.ipreach.backend.shared.constants.Messages;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequestMapping("/auth")
@Tag(name = "Authorization", description = "Authorization methods to login, logout, and password management")
public class AuthController {

    @GetMapping("/login")
    public ResponseEntity<?> login() {
        return buildResponse(HttpStatus.OK, Messages.App.LOGIN_STATUS);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(Authentication authentication) {
        return buildResponse(OK, authentication.getPrincipal(), Messages.Info.USER_LOGGED);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return buildResponse(OK, Messages.Info.USER_LOGGED_OUT);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(Authentication authentication) {
        return buildResponse(OK, authentication.getPrincipal());
    }

}
