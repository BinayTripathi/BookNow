package com.binay.booknow.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.rest.dto.AvailableSlotResponse;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponseWrapper;
import com.binay.booknow.rest.dto.CreateReservationRequest;


/**
 * 
 * @author Binay
 * 
 * @Service class for holding business logic between controller and repository for Table Booking
 *
 */
public interface ITableBookingService {
	
	
	/**
	 * Get all free slot and table for a given date
	 * @param reservationDate :  Date on which to check for all available slot and table
	 * @return List of Objects array. 0th entry is the slot and 1st entry is the table name
	 */
	public List<AvailableSlotResponse> getFreeSlotAndTable(Date reservationDate);
	
	
	
	public FetchReservationResponseWrapper getReservation(Long id);
	
	
	
	public List<FetchReservationResponse> getReservation(Date reservationDate);	
	
	
	/**
	 * Book table based on the details provided in TableBooking object 
	 * @param tableBooking {@link TableBooking}
	 * @return returns an optional of TableBooking object incase booking is successful
	 */
	public CreateReservationResponse createReservation(CreateReservationRequest createReservation) throws Throwable;	
	
	
	public CreateReservationResponse updateTableBooking(Long id, CreateReservationRequest updateReservationRequest, String eTag)
			throws ValidationException;
	
	
	public CreateReservationResponse deleteTableBooking(Long id);	
	

}
