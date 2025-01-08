package com.locationservice.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locationservice.dtos.DriverLocationDto;
import com.locationservice.dtos.NearbyDriversRequestDto;
import com.locationservice.dtos.SaveDriverLocationRequestDto;
import com.locationservice.service.LocationService;
import com.locationservice.utils.LogMessage;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private static final Logger LOGGER = LogManager.getLogger(LocationController.class);

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(@RequestBody SaveDriverLocationRequestDto requestDto) {
        LogMessage.setLogMessagePrefix("/SAVE_DRIVER_LOCATION");

        if (requestDto == null) {
            LogMessage.warn(LOGGER, "Received null save driver location request");
            return ResponseEntity.badRequest().build();
        }

        LogMessage.info(LOGGER, "Processing save driver location request: " + requestDto);

        try {
            Boolean isSaved = locationService.saveDriverLocation(
                    requestDto.getDriverId(), 
                    requestDto.getLongitude(),
                    requestDto.getLatitude());

            if (isSaved) {
                LogMessage.info(LOGGER, "Driver location saved successfully: " + requestDto.getDriverId());
                return ResponseEntity.status(HttpStatus.CREATED).body(true);
            } else {
                LogMessage.warn(LOGGER, "Failed to save driver location: " + requestDto.getDriverId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
            }
        } catch (Exception e) {
            LogMessage.logException(LOGGER, e);
            throw new RuntimeException("Error saving driver location", e);
        }
    }

    @GetMapping("/nearby/drivers")
    public ResponseEntity<List<DriverLocationDto>> getNearbyDrivers(
            @RequestBody NearbyDriversRequestDto requestDto) {
        LogMessage.setLogMessagePrefix("/GET_NEARBY_DRIVERS");

        if (requestDto == null) {
            LogMessage.warn(LOGGER, "Received null nearby drivers request");
            return ResponseEntity.badRequest().build();
        }

        LogMessage.info(LOGGER, "Processing nearby drivers request: " + requestDto);

        try {
            List<DriverLocationDto> nearbyDrivers = locationService.getNearbyDrivers(
                    requestDto.getLongitude(),
                    requestDto.getLatitude());

            LogMessage.info(LOGGER, "Found nearby drivers: " + nearbyDrivers.size());
            return ResponseEntity.ok(nearbyDrivers);
        } catch (Exception e) {
            LogMessage.logException(LOGGER, e);
            throw new RuntimeException("Error fetching nearby drivers", e);
        }
    }
}
