package com.binay.booknow.rest.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.binay.booknow.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonPropertyOrder({"tableName", "availableDate", "availableTime" })
@AllArgsConstructor
@NoArgsConstructor
public class AvailableSlotResponse {
	
	String tableName;
	
	//Date availableDate;
	String dateAvailable;
	
	String availableTime;

}
