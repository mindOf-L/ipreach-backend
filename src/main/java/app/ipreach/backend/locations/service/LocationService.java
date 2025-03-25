package app.ipreach.backend.locations.service;

import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.locations.db.model.Location;
import app.ipreach.backend.locations.db.repository.LocationRepository;
import app.ipreach.backend.locations.payload.mapper.LocationMapper;
import app.ipreach.backend.shared.constants.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static app.ipreach.backend.shared.creation.Constructor.buildResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public ResponseEntity<?> getAllLocations() {
        var locations = locationRepository.findAll().stream()
            .map(LocationMapper.MAPPER::toDto)
            .toList();

        return buildResponse(OK, locations, String.format(Messages.Info.LOCATIONS_LISTED, locations.size()));
    }

    public Location getLocationById(long id) {
        return locationRepository.findById(id).orElseThrow(
            () -> new RequestException(BAD_REQUEST, Messages.ErrorClient.LOCATION_NOT_FOUND));
    }

    public boolean locationExists(long id) {
        return locationRepository.existsById(id);
    }
}
