package com.binay.booknow.rest.dto;

import java.util.Date;

import com.binay.booknow.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({"tableName", "availableDate", "availableTime" })
public class AvailableSlotResponse {
	
	String tableName;
	
	@JsonFormat(pattern=ApplicationConstants.ACCEPTED_DATE_FORMAT)
	Date availableDate;
	
	String availableTime;

}
