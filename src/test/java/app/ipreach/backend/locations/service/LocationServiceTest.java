package app.ipreach.backend.locations.service;

import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.locations.db.model.Location;
import app.ipreach.backend.locations.db.repository.LocationRepository;
import app.ipreach.backend.locations.payload.dto.LocationDto;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.dto.ResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location location;
    private LocationDto locationDto;

    @BeforeEach
    void setUp() {
        // Create a sample location
        location = Location.builder()
                .id(1L)
                .name("Test Location")
                .address("123 Test Street")
                .url("https://test.com")
                .details("Test details")
                .build();

        // Create a sample location DTO
        locationDto = LocationDto.builder()
                .id(1L)
                .name("Test Location")
                .address("123 Test Street")
                .url("https://test.com")
                .details("Test details")
                .build();
    }

    @Test
    void getAllLocations_WithLocations_ShouldReturnLocations() {
        // Arrange
        List<Location> locations = List.of(location);
        when(locationRepository.findAll()).thenReturn(locations);

        // Act
        ResponseEntity<?> response = locationService.getAllLocations();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseDto responseDto = (ResponseDto) response.getBody();
        assertNotNull(responseDto);
        assertEquals("SUCCESSFUL", responseDto.status());
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDto.httpStatus());
        assertEquals(HttpStatus.OK.value(), responseDto.code());
        assertEquals(String.format(Messages.Info.LOCATIONS_LISTED, 1), responseDto.message());

        @SuppressWarnings("unchecked")
        List<LocationDto> resultLocations = (List<LocationDto>) responseDto.data();
        assertEquals(1, resultLocations.size());

        // Verify
        verify(locationRepository).findAll();
    }

    @Test
    void getAllLocations_WithNoLocations_ShouldReturnEmptyList() {
        // Arrange
        when(locationRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<?> response = locationService.getAllLocations();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseDto responseDto = (ResponseDto) response.getBody();
        assertNotNull(responseDto);
        assertEquals("SUCCESSFUL", responseDto.status());
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseDto.httpStatus());
        assertEquals(HttpStatus.OK.value(), responseDto.code());
        assertEquals(String.format(Messages.Info.LOCATIONS_LISTED, 0), responseDto.message());

        @SuppressWarnings("unchecked")
        List<LocationDto> resultLocations = (List<LocationDto>) responseDto.data();
        assertTrue(resultLocations.isEmpty());

        // Verify
        verify(locationRepository).findAll();
    }

    @Test
    void getLocationById_WithExistingId_ShouldReturnLocation() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        // Act
        Location result = locationService.getLocationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(location, result);

        // Verify
        verify(locationRepository).findById(1L);
    }

    @Test
    void getLocationById_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RequestException exception = assertThrows(RequestException.class, () -> locationService.getLocationById(999L));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(Messages.ErrorClient.LOCATION_NOT_FOUND, exception.getMessage());

        // Verify
        verify(locationRepository).findById(999L);
    }

    @Test
    void locationExists_WithExistingId_ShouldReturnTrue() {
        // Arrange
        when(locationRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = locationService.locationExists(1L);

        // Assert
        assertTrue(result);

        // Verify
        verify(locationRepository).existsById(1L);
    }

    @Test
    void locationExists_WithNonExistingId_ShouldReturnFalse() {
        // Arrange
        when(locationRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = locationService.locationExists(999L);

        // Assert
        assertFalse(result);

        // Verify
        verify(locationRepository).existsById(999L);
    }
}
