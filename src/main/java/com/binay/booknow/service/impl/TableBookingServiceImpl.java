package com.binay.booknow.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.binay.booknow.ApplicationConstants;
import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.persistence.repository.RestaurantSlotRepository;
import com.binay.booknow.persistence.repository.RestaurantTableRepository;
import com.binay.booknow.persistence.repository.TableBookingRepository;
import com.binay.booknow.rest.exception.ReservationNotFoundException;
import com.binay.booknow.service.ITableBookingService;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class TableBookingServiceImpl implements ITableBookingService{

	@Autowired
	private TableBookingRepository tableBookingRepository;

	@Autowired
	private RestaurantTableRepository restaurantTableRepository;

	@Autowired
	private RestaurantSlotRepository restaurantSlotRepository;
	

	/**
	 * {@inheritDoc}
	 */
	public List<String[]> getFreeSlotAndTable(Date reservationDate) {

		List<String[]> allFreeTableAndSlotForDate = tableBookingRepository
				.getAllFreeTableAndSlotForDate(reservationDate);
		return allFreeTableAndSlotForDate;

	}
	
	
	/**
	 * {@inheritDoc}
	 */
	//Pessimistic locking for concurrency control
	//Table lock on criteria since inserts cannot be restricted by Unique contraint in the design
	@Transactional(isolation = Isolation.SERIALIZABLE, timeout=ApplicationConstants.TRANSACTION_TIMEOUT_DURATION)   
	public Optional<TableBooking> createReservation(TableBooking tableBookingToBeDone) {

		Optional<TableBooking> tableBookingDone = Optional.empty();
		
		String tableName = tableBookingToBeDone.getRestaurantTable().getTableName();
		Date reservationDate = tableBookingToBeDone.getReservationDate();
		String reservationTime = tableBookingToBeDone.getRestaurantSlot().getSlot();

		
		//Check if a booking in same date,time and table exists -  
		//This locks all the rows for update
		//So an update request cannot go on to update old entry to another entry that conflicts with this one
		//If update reaches here first, then offcourse below query will tell that request time, time, slot is not available
		Optional<TableBooking> reservationByDateTableAndSlot = tableBookingRepository
				.getReservationByDateTableAndSlot(reservationDate, tableName, reservationTime);

		if (reservationByDateTableAndSlot.isPresent()) {
			return tableBookingDone;
		} else {
			TableBooking savedBooking = tableBookingRepository.save(tableBookingToBeDone);
			tableBookingDone = Optional.of(savedBooking);
		}

		return tableBookingDone;
	}
	
	
	
	

	public TableBooking getReservationById(Long id) {

		return tableBookingRepository.findById(id)
				.orElseThrow(() -> new ReservationNotFoundException("No reservation found with id :" + id));
	}
	
	
	

	public Optional<List<TableBooking>> getReservationByDate(Date reservationDate) {

		return tableBookingRepository.getByReservationDate(reservationDate);
	}
	
	
	

	public RestaurantTable getResturantTableByName(String tableName) throws ValidationException {

		RestaurantTable resturantTable = restaurantTableRepository.findByTableName(tableName)
				.orElseThrow(() -> new ValidationException("Incorrect table name provided : " + tableName));

		return resturantTable;
	}

	
	
	
	public RestaurantSlot getResturantSlotByTime(String reservationTime) throws ValidationException {
		RestaurantSlot slotProvided = restaurantSlotRepository.findBySlot(reservationTime)
				.orElseThrow(() -> new ValidationException("Incorrect reservation time provided : " + reservationTime));

		return slotProvided;
	}

	//Optimistic locking for concurrency control 
	public TableBooking updateTableBooking(TableBooking tableBooking, boolean updateTableDateTime) {

		TableBooking updatedTableBooking = null;
		
		String tableName = tableBooking.getRestaurantTable().getTableName();
		Date reservationDate = tableBooking.getReservationDate();
		String reservationTime = tableBooking.getRestaurantSlot().getSlot();

		// If there is change in table, date, time - check for availablility
		if (updateTableDateTime) {

			Optional<TableBooking> dateTimeTableOccupiedOptional = tableBookingRepository
					.getReservationByDateTableAndSlot(reservationDate, tableName, reservationTime);

			//Proceed only if no booking available
			if (dateTimeTableOccupiedOptional.isPresent()) {
				log.info("Seat is unavailable for update: " + dateTimeTableOccupiedOptional.get());
				return updatedTableBooking;  // null returned
			}
		}
		try {
			updatedTableBooking = tableBookingRepository.save(tableBooking);
		} catch (ObjectOptimisticLockingFailureException ex) {
			log.error("Optimistic lock exception - Update failed ");
			throw ex;
		}

		return updatedTableBooking;
	}

	
	
	//Row is read and locked so that a transaction does not try to update after its deleted.
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public boolean deleteTableBooking(Long id) {

		boolean isUnReserved = true;

		if (tableBookingRepository.existsById(id))
			tableBookingRepository.deleteById(id);
		else
			throw new ReservationNotFoundException("No reservation found with id :" + id);

		return isUnReserved;
	}

}
