package com.locationservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SaveDriverLocationRequestDto {

	private String driverId;

	private Double latitude;

	private Double longitude;

}
