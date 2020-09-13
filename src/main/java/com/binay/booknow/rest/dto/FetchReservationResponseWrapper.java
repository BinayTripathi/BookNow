package com.binay.booknow.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FetchReservationResponseWrapper {
	
	FetchReservationResponse fetchReservationResponse;
	String eTag;

}
