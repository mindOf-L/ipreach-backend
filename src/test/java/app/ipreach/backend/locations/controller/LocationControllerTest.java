package app.ipreach.backend.locations.controller;

import app.ipreach.backend.locations.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private ResponseEntity<?> mockResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        mockResponse = ResponseEntity.ok().build();
    }

    @Test
    void getAllLocations_ShouldReturnAllLocations() {
        // Arrange
        doReturn(mockResponse).when(locationService).getAllLocations();

        // Act
        ResponseEntity<?> result = locationController.getAllLocations();

        // Assert
        assertEquals(mockResponse, result);
        verify(locationService).getAllLocations();
    }
}
