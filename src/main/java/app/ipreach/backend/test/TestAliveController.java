package app.ipreach.backend.test;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/test/alive")
@Tag(name = "Testing", description = "Testing methods to validate app functionality")
public class TestAliveController {

    // TODO implement health check
    //  -> check https://www.baeldung.com/spring-boot-health-indicators
    @GetMapping
    public ResponseEntity<?> getAlive() {
        return ResponseEntity.status(200).body("Everything OK!");
    }

}
