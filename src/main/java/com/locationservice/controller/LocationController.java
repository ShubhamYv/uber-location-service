package com.locationservice.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locationservice.dtos.NearbyDriversRequestDto;
import com.locationservice.dtos.SaveDriverLocationRequestDto;

@RestController
@RequestMapping("/api/location")
public class LocationController {

	private StringRedisTemplate stringRedisTemplate;
	private static final String DRIVER_GEO_OPS_KEY = "drivers";
	private static final Double SEARCH_RADIUS= 5.0;
	
	public LocationController(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@PostMapping("/drivers")
	public ResponseEntity<Boolean> saveDriverLocation(
			@RequestBody SaveDriverLocationRequestDto saveDriverLocationRequestDto) {
		try {
			System.out.println("saveDriverLocation|| RequestBody::" + saveDriverLocationRequestDto);
			GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
			
	        RedisGeoCommands.GeoLocation<String> geoLocation = new RedisGeoCommands.GeoLocation<>(
	                saveDriverLocationRequestDto.getDriverId(),
	                new Point(
	                		saveDriverLocationRequestDto.getLongitude(), 
	                		saveDriverLocationRequestDto.getLatitude()
	                )
	        );
	        
	        geoOps.add(DRIVER_GEO_OPS_KEY, geoLocation);
	        
			return new ResponseEntity<>(true, HttpStatus.CREATED);
		} catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
	}

	@GetMapping("/nearby/drivers")
	public ResponseEntity<List<String>> getNearbyDrivers(@RequestBody NearbyDriversRequestDto nearbyDriversRequestDto) {
		try {
			GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
			Distance radius = new Distance(SEARCH_RADIUS, Metrics.KILOMETERS);

			Circle within = new Circle(
					new Point(nearbyDriversRequestDto.getLongitude(), nearbyDriversRequestDto.getLatitude()),
					radius);
			
			GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = geoOps.radius(DRIVER_GEO_OPS_KEY, within);

			if (geoResults == null) {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
			}
			
			List<String> drivers = geoResults.getContent().stream()
					.map(result -> result.getContent().getName())
					.toList();

			return new ResponseEntity<>(drivers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
		}
	}

}
