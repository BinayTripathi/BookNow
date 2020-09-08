package com.binay.booknow.rest.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FetchReservationResponse {

	String id;
	String name;
	String contact;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	Date reservationDate;
	
	String reservationTime;
	String tableName;
}
