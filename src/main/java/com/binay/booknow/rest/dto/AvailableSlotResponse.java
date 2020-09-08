package com.binay.booknow.rest.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.binay.booknow.persistence.entity.DailySlot;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({"tableName", "availableDate", "availableTime" })
public class AvailableSlotResponse {
	
	String tableName;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	Date availableDate;
	
	String availableTime;

}
