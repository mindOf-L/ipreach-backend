package app.ipreach.backend.locations.controller;

import app.ipreach.backend.locations.service.LocationService;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.creation.FakeClass;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static app.ipreach.backend.shared.creation.Generator.getRandomList;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/locations")
@Tag(name = "Location", description = "Locations CRUD")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<?> getAllLocations() {
        var locations = getRandomList(FakeClass::giveMeLocation);
        return buildResponse(OK, locations, String.format(Messages.Info.LOCATIONS_LISTED, locations.size()));
    }

}
