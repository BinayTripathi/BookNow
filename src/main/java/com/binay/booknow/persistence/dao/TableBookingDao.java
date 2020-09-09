package com.binay.booknow.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.binay.booknow.persistence.entity.DailySlot;
import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.persistence.repository.RestaurantSlotRepository;
import com.binay.booknow.persistence.repository.RestaurantTableRepository;
import com.binay.booknow.persistence.repository.TableBookingRepository;
import com.binay.booknow.rest.dto.BookingAvailability;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TableBookingDao {

	@Autowired
	TableBookingRepository tableBookingRepository;

	@Autowired
	RestaurantTableRepository restaurantTableRepository;

	@Autowired
	RestaurantSlotRepository restaurantSlotRepository;

	/*
	 * public void getSlotAndTable1() {
	 * 
	 * Query query1 = entityManager
	 * .createQuery("SELECT t.tableName,s.dailySlot FROM RestaurantTable t join  t.restaurantSlots s"
	 * );
	 * 
	 * List<Object[]> resultSet = query1.getResultList();
	 * log.info("Result size : {} ", resultSet.size()); for (Object[] result :
	 * resultSet) // Result represents a row of 2 arrays
	 * log.info("RestaurantTable : {} , RestaurantSlot  :{}", result[0], result[1]);
	 * 
	 * String query =
	 * "SELECT table,slot FROM TABLE_SLOT st, RestaurantTable t, RestaurantSlot s  minus select Restaurant_table_id, restaurant_slot_id from table_booking"
	 * ; Query createNativeQuery = entityManager.createNativeQuery(query); List
	 * resultList = createNativeQuery.getResultList();
	 * System.out.println(resultList);
	 * 
	 * String query2 = "SELECT t,s FROM RestaurantSlot s join RestaurantTable t";
	 * Query createNativeQuery2 = entityManager.createNativeQuery(query,
	 * RestaurantTable.class); List resultList2 = createNativeQuery.getResultList();
	 * System.out.println(resultList2);
	 * 
	 * }
	 */

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

		return tableBookingRepository.getOne(id);
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
		RestaurantSlot slotProvided = restaurantSlotRepository.findBySlot(reservationTime).orElseThrow(
				() -> new ValidationException("Incorrect reservation time provided : " + reservationTime));
		
		return slotProvided;
	}
	
	public TableBooking updateTableBooking(TableBooking tableBooking, boolean updateTableDateTime) {
		
		TableBooking updatedTableBooking = null;
		
		//If there is change in table, date, time - check for availablility
		if(updateTableDateTime) {
			
		}
		try {
		updatedTableBooking = tableBookingRepository.save(tableBooking);
		} catch(ObjectOptimisticLockingFailureException ex) {
			log.error("Optimistic lock exception - Update failed");
		}
		
		return updatedTableBooking;
	}

}
