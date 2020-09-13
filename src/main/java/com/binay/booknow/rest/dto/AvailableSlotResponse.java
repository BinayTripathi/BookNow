package com.binay.booknow.rest.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"tableName", "availableDate", "availableTime" })
public class AvailableSlotResponse {
	
	String tableName;
	
	//Date availableDate;
	String dateAvailable;
	
	String availableTime;

}
