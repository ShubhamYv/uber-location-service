package com.locationservice.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.locationservice.dtos.DriverLocationDto;
import com.locationservice.service.LocationService;
import com.locationservice.utils.LogMessage;

@Service
public class LocationServiceImpl implements LocationService {

    private static final Logger LOGGER = LogManager.getLogger(LocationServiceImpl.class);

    private final StringRedisTemplate stringRedisTemplate;
    private static final String DRIVER_GEO_OPS_KEY = "drivers";
    private static final Double SEARCH_RADIUS = 5.0; // in kilometers

    public LocationServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Boolean saveDriverLocation(String driverId, Double longitude, Double latitude) {
        LogMessage.setLogMessagePrefix("/SAVE_DRIVER_LOCATION");

        LogMessage.info(LOGGER, String.format("Attempting to save driver location: "
        		+ "driverId=%s, longitude=%f, latitude=%f", driverId, longitude, latitude));

        try {
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            Long addedCount = geoOps.add(
                    DRIVER_GEO_OPS_KEY, 
                    new Point(longitude, latitude), 
                    driverId);

            if (addedCount != null && addedCount > 0) {
                LogMessage.info(LOGGER, String.format("Driver location saved successfully in Redis: "
                		+ "driverId=%s, longitude=%f, latitude=%f", driverId, longitude, latitude));
                return true;
            } else {
                LogMessage.warn(LOGGER, String.format("Failed to save driver location in Redis:"
                		+ " driverId=%s", driverId));
                return false;
            }
        } catch (Exception e) {
            LogMessage.logException(LOGGER, e);
            return false;
        }
    }

    @Override
    public List<DriverLocationDto> getNearbyDrivers(Double longitude, Double latitude) {
        LogMessage.setLogMessagePrefix("/GET_NEARBY_DRIVERS");
        
        LogMessage.info(LOGGER, String.format("Fetching nearby drivers for location: "
        		+ "longitude=%f, latitude=%f", longitude, latitude));
        try {
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            Distance radius = new Distance(SEARCH_RADIUS, Metrics.KILOMETERS);
            Circle within = new Circle(
                    new Point(longitude, latitude),
                    radius);

            GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = geoOps.radius(DRIVER_GEO_OPS_KEY, within);

            if (geoResults == null || geoResults.getContent().isEmpty()) {
                LogMessage.warn(LOGGER, String.format("No nearby drivers found for location: "
                		+ "longitude=%f, latitude=%f", longitude, latitude));
                return new ArrayList<>();
            }

            List<DriverLocationDto> driverLocations = geoResults.getContent().stream().map(result -> {
                RedisGeoCommands.GeoLocation<String> location = result.getContent();
                Point point = location.getPoint();
                return DriverLocationDto.builder()
                        .driverId(location.getName())
                        .latitude(point.getY())
                        .longitude(point.getX())
                        .build();
            }).toList();

            LogMessage.info(LOGGER, String.format("Found %d nearby drivers for location: "
            		+ "longitude=%f, latitude=%f", driverLocations.size(), longitude, latitude));
            return driverLocations;
        } catch (Exception e) {
            LogMessage.logException(LOGGER, e);
            return new ArrayList<>();
        }
    }
}
