package app.ipreach.backend.test;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/test/cookie")
@Tag(name = "Testing", description = "Testing methods to validate app functionality")
public class TestCookieController {

    @GetMapping
    public ResponseEntity<?> getAlive(HttpServletResponse response) {
        Cookie cookie = new Cookie("test-cookie", "test-value");
        response.setHeader("Set-Cookie",
            String.format("%s=%s;", cookie.getName(), cookie.getValue()));

        return ResponseEntity.status(200).body("Everything OK! (with cookies)");
    }

}
