package com.binay.booknow.rest.dto;

import java.util.Date;

import javax.validation.constraints.Future;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CreateReservationRequest {
	
	String name;
	String contact;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	@Future(message="Reservation date must be in future")
	Date reservationDate;
	
	String reservationTime;
	String tableName;

}
