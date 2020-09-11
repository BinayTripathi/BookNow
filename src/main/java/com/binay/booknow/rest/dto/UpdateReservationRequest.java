package com.binay.booknow.rest.dto;

import java.util.Date;

import javax.validation.constraints.Future;

import org.springframework.format.annotation.DateTimeFormat;

import com.binay.booknow.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UpdateReservationRequest {
	
	Long id;
	String name;
	String contact;
	
	@JsonFormat(pattern=ApplicationConstants.ACCEPTED_DATE_FORMAT)
	@Future(message="Reservation date must be in future")
	@DateTimeFormat(pattern = ApplicationConstants.ACCEPTED_DATE_FORMAT)
	Date reservationDate;
	
	String reservationTime;
	String tableName;

}
