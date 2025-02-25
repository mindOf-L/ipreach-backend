package app.ipreach.backend.users.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/users")
@Tag(name = "Users", description = "Users methods to refresh users from GSheet")
public class UserController {



}
