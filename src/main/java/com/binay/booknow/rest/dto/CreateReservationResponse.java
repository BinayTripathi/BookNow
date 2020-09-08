package com.binay.booknow.rest.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateReservationResponse {
	
	String id;
	BookingAvailability bookingAvailability;

}
