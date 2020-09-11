package com.binay.booknow.rest.dto;

import java.util.Date;

import javax.validation.constraints.Future;

import com.binay.booknow.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CreateReservationRequest {
	
	String name;
	String contact;
	
	@JsonFormat(pattern=ApplicationConstants.ACCEPTED_DATE_FORMAT)
	@Future(message="Reservation date must be in future")
	Date reservationDate;
	
	String reservationTime;
	String tableName;

}
