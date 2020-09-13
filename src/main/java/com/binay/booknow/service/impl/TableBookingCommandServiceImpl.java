package com.binay.booknow.service.impl;

import java.util.Date;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.binay.booknow.ApplicationConstants;
import com.binay.booknow.configuration.properties.BookNowProperties;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.persistence.repository.RestaurantSlotRepository;
import com.binay.booknow.persistence.repository.RestaurantTableRepository;
import com.binay.booknow.persistence.repository.TableBookingRepository;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.exception.ReservationNotFoundException;
import com.binay.booknow.service.ITableBookingCommandService;


@Service
public class TableBookingCommandServiceImpl implements ITableBookingCommandService {
	
	
	
	@Autowired
	private TableBookingRepository tableBookingRepository;
	

	/**
	 * {@inheritDoc}
	 */
	// Pessimistic locking for concurrency control
	// Table lock on criteria since inserts cannot be restricted by Unique contraint
	// in the design
	@Transactional(isolation = Isolation.SERIALIZABLE, timeout = ApplicationConstants.TRANSACTION_TIMEOUT_DURATION)
	public Optional<TableBooking> createReservation(TableBooking tableBookingToBeDone) {

		Optional<TableBooking> tableBookingDone = Optional.empty();

		String tableName = tableBookingToBeDone.getRestaurantTable().getTableName();
		Date reservationDate = tableBookingToBeDone.getReservationDate();
		String reservationTime = tableBookingToBeDone.getRestaurantSlot().getSlot();

		// Check if a booking in same date,time and table exists -
		// This locks all the rows for update
		// So an update request cannot go on to update old entry to another entry that
		// conflicts with this one
		// If update reaches here first, then offcourse below query will tell that
		// request time, time, slot is not available
		Optional<TableBooking> reservationByDateTableAndSlot = tableBookingRepository
				.getReservationByDateTableAndSlot(reservationDate, tableName, reservationTime);

		if (reservationByDateTableAndSlot.isPresent()) {
			return tableBookingDone;   // return Optional.empty()
		} else {
			TableBooking savedBooking = tableBookingRepository.save(tableBookingToBeDone);
			tableBookingDone = Optional.of(savedBooking);
		}

		return tableBookingDone;
	}
	
	
	
	
	

	@Override
	public TableBooking updateTableBooking(TableBooking tableBookingUpdateToBeDone)	throws ObjectOptimisticLockingFailureException {
		
		TableBooking updatedBooking = tableBookingRepository.save(tableBookingUpdateToBeDone);
		return updatedBooking;
	}

	@Override
	public boolean deleteTableBooking(Long id) {

		boolean isUnReserved = true;

		if (tableBookingRepository.existsById(id))
			tableBookingRepository.deleteById(id);
		else
			throw new ReservationNotFoundException("No reservation found with id :" + id);

		return isUnReserved;
	}


}
