package com.binay.booknow.service.impl;

import static com.binay.booknow.ApplicationConstants.DATE_FORMATTER;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import javax.xml.bind.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.binay.booknow.ApplicationConstants;
import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.rest.dto.AvailableSlotResponse;
import com.binay.booknow.rest.dto.BookingAvailability;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponseWrapper;
import com.binay.booknow.rest.exception.EtagMismatchException;
import com.binay.booknow.service.ITableBookingCommandService;
import com.binay.booknow.service.ITableBookingQueryService;
import com.binay.booknow.service.ITableBookingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TableBookingServiceImpl implements ITableBookingService {
	
	@Autowired
	ITableBookingQueryService tableBookingQueryService;
	
	@Autowired
	ITableBookingCommandService tableBookingCommandService;


	/**
	 * {@inheritDoc}
	 */
	public List<AvailableSlotResponse> getFreeSlotAndTable(Date reservationDate) {

		List<String[]> freeSlotAndTable = tableBookingQueryService.getFreeSlotAndTable(reservationDate);
		
		
		List<AvailableSlotResponse> allFreeTableAndSlotForDate = freeSlotAndTable.stream().map(record -> {
			
			String tableName = record[0];
			String availableSlot =  record[1];
			return AvailableSlotResponse.builder()
					.dateAvailable(DATE_FORMATTER.format(reservationDate))
					.availableTime(availableSlot)
					.tableName(tableName)
					.build();
		}).collect(Collectors.toList());
		
		
		return allFreeTableAndSlotForDate;

	}
	
		
	
	public FetchReservationResponseWrapper getReservation(Long id) {

		TableBooking tableBooking = tableBookingQueryService.getReservationById(id);
		
		FetchReservationResponse fetchReservationResponse = FetchReservationResponse.builder()
				.id(String.valueOf(tableBooking.getId())).name(tableBooking.getPersonName())
				.contact(tableBooking.getContact()).reservationDate(DATE_FORMATTER.format(tableBooking.getReservationDate()))
				.reservationTime(tableBooking.getRestaurantSlot().getSlot())
				.tableName(tableBooking.getRestaurantTable().getTableName()).build();
		
		String eTag = "\"" + tableBooking.getVersion() + "\"";
		
		return FetchReservationResponseWrapper.builder()
				.fetchReservationResponse(fetchReservationResponse)
				.eTag(eTag)
				.build();
	}
	
	

	public List<FetchReservationResponse> getReservation(Date reservationDate) {

		Optional<List<TableBooking>> tableBookingOptional = tableBookingQueryService.getReservationByDate(reservationDate);
		
		List<FetchReservationResponse> reservationList = Collections.EMPTY_LIST;
		if (tableBookingOptional.isPresent()) {
			reservationList = tableBookingOptional.get().stream().map(tableBooking -> {
				FetchReservationResponse fetchReservationResponse = FetchReservationResponse.builder()
						.id(String.valueOf(tableBooking.getId())).name(tableBooking.getPersonName())
						.contact(tableBooking.getContact()).reservationDate(DATE_FORMATTER.format(tableBooking.getReservationDate()))
						.reservationTime(tableBooking.getRestaurantSlot().getSlot())
						.tableName(tableBooking.getRestaurantTable().getTableName()).build();
				return fetchReservationResponse;
			}).collect(Collectors.toList());
		}
		
		return reservationList;
	}
	
	

	/**
	 * {@inheritDoc}
	 * @throws Throwable 
	 */
	public CreateReservationResponse createReservation(CreateReservationRequest createReservation) throws Throwable {

		CompletableFuture<RestaurantTable> resturantTableFuture = CompletableFuture.supplyAsync( () -> {
			try {
				return tableBookingQueryService.getResturantTableByName(createReservation.getTableName());
			} catch (ValidationException e) {
				throw new CompletionException(e); 
			}
		}) ;
				
				
		CompletableFuture<RestaurantSlot> restaurantSlotFuture = CompletableFuture.supplyAsync(() -> {
			try {
				return tableBookingQueryService.getResturantSlotByTime(createReservation.getReservationTime());
			} catch (ValidationException e) {
				throw new CompletionException(e); 
			}
		});
		
		
		RestaurantTable resturantTable = null;
		RestaurantSlot slotProvided = null;
	    try {
	    	resturantTable = resturantTableFuture.join();
	    	slotProvided = restaurantSlotFuture.join();
	    }
	    catch(CompletionException ex) {	       
	            throw ex.getCause();
	    }	        		
		
		TableBooking tableBooking = TableBooking.builder().contact(createReservation.getContact())
				.personName(createReservation.getName()).reservationDate(createReservation.getReservationDate())
				.restaurantSlot(slotProvided).restaurantTable(resturantTable).build();
		
		
		Optional<TableBooking> tableBookingCompleted = tableBookingCommandService.createReservation(tableBooking);
		
		CreateReservationResponse createReservationResponse = null;
		if (!tableBookingCompleted.isPresent()) {
			createReservationResponse = CreateReservationResponse.builder()
					.id("0")
					.bookingAvailability(BookingAvailability.UNAVAILABLE)
					.build();
		} else {
			createReservationResponse = CreateReservationResponse.builder()
					.id(String.valueOf(tableBookingCompleted.get().getId()))
					.bookingAvailability(BookingAvailability.BOOKED)
					.build();
		}
		
		return createReservationResponse;
	}

	

	// Optimistic locking for concurrency control
	public CreateReservationResponse updateTableBooking(Long id, CreateReservationRequest updateReservationRequest, String eTag)
			throws ValidationException {
		
		TableBooking tableBooking = tableBookingQueryService.getReservationById(id);
				
		if (!eTag.equals("\"" + tableBooking.getVersion() + "\"")) {
			throw new EtagMismatchException("Expired etag provided");
		}

		
		TableBooking tableBookingToUpdate = new TableBooking();
		BeanUtils.copyProperties(tableBooking, tableBookingToUpdate);
		
		boolean updateTableDateTime = false;
		if (tableBookingToUpdate.getReservationDate().getDate() != updateReservationRequest.getReservationDate().getDate()
				|| tableBookingToUpdate.getReservationDate().getMonth() != updateReservationRequest.getReservationDate()
						.getMonth()
				|| tableBookingToUpdate.getReservationDate().getYear() != updateReservationRequest.getReservationDate()
						.getYear()) {
			tableBookingToUpdate.setReservationDate(updateReservationRequest.getReservationDate());
			updateTableDateTime = true;
		}

		if (tableBooking.getRestaurantSlot().getSlot().compareTo(updateReservationRequest.getReservationTime()) != 0) {
			RestaurantSlot resturantSlot = tableBookingQueryService.getResturantSlotByTime(updateReservationRequest.getReservationTime());
			tableBookingToUpdate.setRestaurantSlot(resturantSlot);
			updateTableDateTime = true;
		}

		if (tableBooking.getRestaurantTable().getTableName().compareTo(updateReservationRequest.getTableName()) != 0) {
			RestaurantTable resturantTable = tableBookingQueryService.getResturantTableByName(updateReservationRequest.getTableName());
			tableBookingToUpdate.setRestaurantTable(resturantTable);
			updateTableDateTime = true;
		}


		TableBooking updatedTableBooking = null;

		// If there is change in table, date, time - should be transactional create reservation
		if (updateTableDateTime) {
			
			Optional<TableBooking> tableBookingCompleted = tableBookingCommandService.createReservation(tableBookingToUpdate);

			// Proceed only if no booking available
			if (!tableBookingCompleted.isPresent()) {
				log.info("Seat is unavailable for update on request date and slot");
			} else {
				updatedTableBooking = tableBookingCompleted.get();
			}
		} else {
			try {
				updatedTableBooking = tableBookingCommandService.updateTableBooking(tableBookingToUpdate);
			} catch (ObjectOptimisticLockingFailureException ex) {
				log.error("Optimistic lock exception - Update failed ");
				throw ex;
			}
		}
		
		
		CreateReservationResponse reservationUpdateResponse = null;
		if(null != updatedTableBooking) {
			reservationUpdateResponse = CreateReservationResponse.builder()
					.bookingAvailability(BookingAvailability.BOOKED)
					.id(String.valueOf(updatedTableBooking.getId()))
					.build();
		} else {
			reservationUpdateResponse = CreateReservationResponse.builder()
					.bookingAvailability(BookingAvailability.UNAVAILABLE)
					.id("0")
					.build();
		}
		
		return reservationUpdateResponse;
		
	}

	
	//If an update is called after delete - ObjectOptimisticLockingFailureException happens
	public CreateReservationResponse deleteTableBooking(Long id) {

		tableBookingCommandService.deleteTableBooking(id);
		
		CreateReservationResponse deleteReservationResponse = CreateReservationResponse.builder()
				.id("0")
				.bookingAvailability(BookingAvailability.UNRESERVED)
				.build();
		
		
		return deleteReservationResponse;
	}



	

}
