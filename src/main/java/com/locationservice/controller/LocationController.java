package com.locationservice.controller;

import java.util.List;

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

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(
            @RequestBody SaveDriverLocationRequestDto saveDriverLocationRequestDto) {
        try {
            System.out.println("saveDriverLocation || RequestBody: " + saveDriverLocationRequestDto);
            Boolean saveDriverLocation = locationService.saveDriverLocation(
                    saveDriverLocationRequestDto.getDriverId(),
                    saveDriverLocationRequestDto.getLongitude(),
                    saveDriverLocationRequestDto.getLatitude()
            );

            if (saveDriverLocation) {
                return new ResponseEntity<>(true, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }


    @GetMapping("/nearby/drivers")
    public ResponseEntity<List<DriverLocationDto>> getNearbyDrivers(
            @RequestBody NearbyDriversRequestDto nearbyDriversRequestDto) {
        try {
            List<DriverLocationDto> nearbyDrivers = locationService.getNearbyDrivers(
            		nearbyDriversRequestDto.getLongitude(), 
            		nearbyDriversRequestDto.getLatitude());
            return new ResponseEntity<>(nearbyDrivers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
}
