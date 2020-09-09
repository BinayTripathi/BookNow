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

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.persistence.repository.RestaurantSlotRepository;
import com.binay.booknow.persistence.repository.RestaurantTableRepository;
import com.binay.booknow.persistence.repository.TableBookingRepository;
import com.binay.booknow.rest.dto.BookingAvailability;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;
import com.binay.booknow.rest.exception.ReservationNotFoundException;
import com.binay.booknow.service.ITableBookingService;

import lombok.extern.slf4j.Slf4j;



/**
 * 
 * @author Binay
 * 
 * DAO for handling all database related operations
 *
 */

@Service
@Slf4j
public class TableBookingServiceImpl implements ITableBookingService{

	@Autowired
	TableBookingRepository tableBookingRepository;

	@Autowired
	RestaurantTableRepository restaurantTableRepository;

	@Autowired
	RestaurantSlotRepository restaurantSlotRepository;

	

	public List<Object[]> getSlotAndTable(Date reservationDate) {

		List<Object[]> allFreeTableAndSlotForDate = tableBookingRepository
				.getAllFreeTableAndSlotForDate(reservationDate);
		return allFreeTableAndSlotForDate;

	}
	
	
	
	

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public CreateReservationResponse createReservation(CreateReservationRequest createReservation)
			throws ValidationException {

		String tableName = createReservation.getTableName();
		Date reservationDate = createReservation.getReservationDate();
		String reservationTime = createReservation.getReservationTime();

		Optional<TableBooking> reservationByDateTableAndSlot = tableBookingRepository
				.getReservationByDateTableAndSlot(reservationDate, tableName, reservationTime);

		CreateReservationResponse createReservationResponse = null;
		if (reservationByDateTableAndSlot.isPresent()) {
			createReservationResponse = CreateReservationResponse.builder().id("0")
					.bookingAvailability(BookingAvailability.UNAVAILABLE).build();
		} else {

			RestaurantTable resturantTable = restaurantTableRepository.findByTableName(tableName)
					.orElseThrow(() -> new ValidationException("Incorrect table name provided : " + tableName));

			RestaurantSlot slotProvided = restaurantSlotRepository.findBySlot(reservationTime).orElseThrow(
					() -> new ValidationException("Incorrect reservation time provided : " + reservationTime));

			TableBooking tableBooking = TableBooking.builder().contact(createReservation.getContact())
					.personName(createReservation.getName()).reservationDate(reservationDate)
					.restaurantSlot(slotProvided).restaurantTable(resturantTable).build();

			TableBooking savedBooking = tableBookingRepository.save(tableBooking);
			createReservationResponse = CreateReservationResponse.builder().id(String.valueOf(savedBooking.getId()))
					.bookingAvailability(BookingAvailability.BOOKED).build();
		}

		return createReservationResponse;
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

	
	
	public TableBooking updateTableBooking(TableBooking tableBooking, boolean updateTableDateTime) {

		TableBooking updatedTableBooking = null;

		// If there is change in table, date, time - check for availablility
		if (updateTableDateTime) {

			Optional<TableBooking> dateTimeTableNotAvailableOptional = tableBookingRepository
					.getReservationByDateTableAndSlot(tableBooking.getReservationDate(),
							tableBooking.getRestaurantTable().getTableName(),
							tableBooking.getRestaurantSlot().getSlot());

			if (dateTimeTableNotAvailableOptional.isPresent())
				return updatedTableBooking;
		}
		try {
			updatedTableBooking = tableBookingRepository.save(tableBooking);
		} catch (ObjectOptimisticLockingFailureException ex) {
			log.error("Optimistic lock exception - Update failed");
		}

		return updatedTableBooking;
	}

	
	
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public boolean deleteTableBooking(Long id) {

		boolean isUnReserved = true;

		if (tableBookingRepository.existsById(id))
			tableBookingRepository.deleteById(id);
		else
			throw new ReservationNotFoundException("No reservation found with id :" + id);

		return isUnReserved;
	}

}
