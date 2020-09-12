package com.binay.booknow.service;

import java.util.Optional;

import javax.xml.bind.ValidationException;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.rest.dto.CreateReservationRequest;


public interface ITableBookingCommandService {
	
	
	
	/**
	 * Book table based on the details provided in TableBooking object 
	 * @param tableBooking {@link TableBooking}
	 * @return returns an optional of TableBooking object incase booking is successful
	 */
	public Optional<TableBooking> createReservation(TableBooking tableBooking);
	
		
	public TableBooking updateTableBooking(TableBooking tableBookingUpdateToBeDone)	throws ObjectOptimisticLockingFailureException;
	
	
	public boolean deleteTableBooking(Long id);

}
