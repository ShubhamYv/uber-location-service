package com.locationservice.service.impl;

import java.util.ArrayList;
import java.util.List;

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

@Service
public class LocationServiceImpl implements LocationService {

	private final StringRedisTemplate stringRedisTemplate;
	private static final String DRIVER_GEO_OPS_KEY = "drivers";
	private static final Double SEARCH_RADIUS = 5.0; // in kilometers

	public LocationServiceImpl(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Override
	public Boolean saveDriverLocation(String driverId, Double longitude, Double latitude) {
		try {
			GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
			Long addedCount = geoOps.add(DRIVER_GEO_OPS_KEY, new Point(longitude, latitude), driverId);

			if (addedCount != null && addedCount > 0) {
				System.out.println(
						"Driver location saved in Redis: " + driverId + " (" + longitude + ", " + latitude + ")");
				return true;
			} else {
				System.out.println("Failed to save driver location in Redis: " + driverId);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<DriverLocationDto> getNearbyDrivers(Double longitude, Double latitude) {
		GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
		Distance radius = new Distance(SEARCH_RADIUS, Metrics.KILOMETERS);
		Circle within = new Circle(new Point(longitude, latitude), radius);

		GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = geoOps.radius(DRIVER_GEO_OPS_KEY, within);

		if (geoResults == null || geoResults.getContent().isEmpty()) {
			System.out.println("No nearby drivers found for location: (" + longitude + ", " + latitude + ")");
			return new ArrayList<>();
		}

		return geoResults.getContent().stream().map(result -> {
			RedisGeoCommands.GeoLocation<String> location = result.getContent();
			Point point = location.getPoint();
			return DriverLocationDto.builder()
					.driverId(location.getName())
					.latitude(point.getY())
					.longitude(point.getX())
					.build();
		}).toList();
	}
}
