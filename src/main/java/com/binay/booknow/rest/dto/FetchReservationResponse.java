package com.binay.booknow.rest.dto;

import com.binay.booknow.rest.dto.AvailableSlotResponse.AvailableSlotResponseBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FetchReservationResponse {

	String id;
	String name;
	String contact;
	
	String reservationDate;
	
	String reservationTime;
	String tableName;
}
