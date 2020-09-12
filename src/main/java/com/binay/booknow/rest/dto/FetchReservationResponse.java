package com.binay.booknow.rest.dto;

import java.util.Date;

import com.binay.booknow.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FetchReservationResponse {

	String id;
	String name;
	String contact;
	
	String reservationDate;
	
	String reservationTime;
	String tableName;
}
