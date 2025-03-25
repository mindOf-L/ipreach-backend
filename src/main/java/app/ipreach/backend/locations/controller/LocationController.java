package app.ipreach.backend.locations.controller;

import app.ipreach.backend.locations.service.LocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/locations")
@Tag(name = "Location", description = "Locations CRUD")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<?> getAllLocations() {
        return locationService.getAllLocations();
    }

}
