package com.locationservice.service;

import java.util.List;

import com.locationservice.dtos.DriverLocationDto;

public interface LocationService {

	Boolean saveDriverLocation(String driverId, Double longitude, Double latitude);

	List<DriverLocationDto> getNearbyDrivers(Double longitude, Double latitude);
}
