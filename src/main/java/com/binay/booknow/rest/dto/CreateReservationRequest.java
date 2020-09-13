package com.binay.booknow.rest.dto;

import java.util.Date;

import javax.validation.constraints.Future;

import org.springframework.format.annotation.DateTimeFormat;

import com.binay.booknow.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
	
	Long id;
	String name;
	String contact;
	
	//@JsonFormat(pattern=ApplicationConstants.ACCEPTED_DATE_FORMAT)
	@Future(message="Reservation date must be in future")
	@DateTimeFormat(pattern = ApplicationConstants.ACCEPTED_DATE_FORMAT)
	Date reservationDate;
	
	String reservationTime;
	String tableName;

}
