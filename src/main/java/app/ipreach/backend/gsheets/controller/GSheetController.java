package app.ipreach.backend.gsheets.controller;

import app.ipreach.backend.gsheets.service.GSheetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authorization", description = "GSheet CRUD")
public class GSheetController {

    private final GSheetService gSheetService;

}
