package com.binay.booknow.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FetchReservationResponseWrapper {
	
	FetchReservationResponse fetchReservationResponse;
	String eTag;

}
