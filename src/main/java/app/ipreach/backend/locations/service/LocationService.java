package app.ipreach.backend.locations.service;

import app.ipreach.backend.locations.db.repository.LocationRepository;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.creation.FakeClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static app.ipreach.backend.shared.creation.Generator.getRandomList;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public ResponseEntity<?> getAllLocations() {
        var locations = getRandomList(FakeClass::giveMeLocation);
        return buildResponse(OK, locations, String.format(Messages.Info.LOCATIONS_LISTED, locations.size()));
    }
}
