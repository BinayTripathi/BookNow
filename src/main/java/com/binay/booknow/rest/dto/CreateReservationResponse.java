package com.binay.booknow.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationResponse {
	
	String id;
	BookingAvailability bookingAvailability;

}
