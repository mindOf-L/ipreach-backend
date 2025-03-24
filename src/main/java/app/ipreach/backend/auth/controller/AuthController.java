package app.ipreach.backend.auth.controller;

import app.ipreach.backend.auth.payload.dto.LoginDto;
import app.ipreach.backend.auth.service.AuthService;
import app.ipreach.backend.core.security.user.CurrentUser;
import app.ipreach.backend.core.security.user.UserDetailsImpl;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authorization", description = "Authorization methods to login, logout, and password management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto, HttpServletResponse response) throws ParseException, JOSEException {
        return authService.loginUser(loginDto, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CurrentUser UserDetailsImpl user, HttpServletResponse response) {
        return authService.logout(user, response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(Authentication authentication) {
        return authService.getUser(authentication);
    }

}
