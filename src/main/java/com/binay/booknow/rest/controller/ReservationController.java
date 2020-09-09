package com.binay.booknow.rest.controller;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.rest.dto.AvailableSlotResponse;
import com.binay.booknow.rest.dto.BookingAvailability;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponse;
import com.binay.booknow.rest.dto.UpdateReservationRequest;
import com.binay.booknow.rest.exception.EtagMismatchException;
import com.binay.booknow.rest.exception.EtagNotFoundException;
import com.binay.booknow.rest.exception.ReservationNotFoundException;
import com.binay.booknow.service.ITableBookingService;

@RestController
public class ReservationController {

	@Autowired
	ITableBookingService tableBookingService;

	@GetMapping(path = "/v1/availableSlots/{date}")
	public DeferredResult<List<AvailableSlotResponse>> getAllAvailabilityOnDate(
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

		DeferredResult<List<AvailableSlotResponse>> deferredResult = new DeferredResult<>();

		List<Object[]> slotAndTable = tableBookingService.getSlotAndTable(date);

		List<AvailableSlotResponse> availability = slotAndTable.stream().map(entry -> {
			return AvailableSlotResponse.builder().availableTime((String) entry[1]).availableDate(date)
					.tableName((String) entry[0]).build();
		}).collect(Collectors.toList());

		// ResponseEntity.
		deferredResult.setResult(availability);
		return deferredResult;
	}

	@PostMapping(path = "/v1/reservations")
	public DeferredResult<ResponseEntity<CreateReservationResponse>> createReservation(
			@Valid @RequestBody CreateReservationRequest createReservation) throws ValidationException {

		DeferredResult<ResponseEntity<CreateReservationResponse>> deferredResult = new DeferredResult<>();
		
		RestaurantTable resturantTable = tableBookingService.getResturantTableByName(createReservation.getTableName());
		RestaurantSlot slotProvided = tableBookingService.getResturantSlotByTime(createReservation.getReservationTime());
		TableBooking tableBooking = TableBooking.builder().contact(createReservation.getContact())
				.personName(createReservation.getName()).reservationDate(createReservation.getReservationDate())
				.restaurantSlot(slotProvided).restaurantTable(resturantTable).build();
		

		Optional<TableBooking> tableBookingCompleted = tableBookingService.createReservation(tableBooking);
		
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
		
		 //Create resource location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                    .path("/{id}")
                                    .buildAndExpand(createReservationResponse.getId())
                                    .toUri();
        
        ResponseEntity<CreateReservationResponse> reservationResponseEntity 
        		= ResponseEntity.created(location).body(createReservationResponse);
        deferredResult.setResult(reservationResponseEntity);
		
		return deferredResult;
	}

	@GetMapping(path = "/v1/reservations/{id}")
	public DeferredResult<ResponseEntity<FetchReservationResponse>> getReservationById(@PathVariable Long id) {

		DeferredResult<ResponseEntity<FetchReservationResponse>> deferredResult = new DeferredResult<>();

		TableBooking tableBooking = tableBookingService.getReservationById(id);
		if (null == tableBooking) {
			throw new ReservationNotFoundException("No reservation found for id : " + id);
		}

		FetchReservationResponse fetchReservationResponse = FetchReservationResponse.builder()
				.id(String.valueOf(tableBooking.getId())).name(tableBooking.getPersonName())
				.contact(tableBooking.getContact()).reservationDate(tableBooking.getReservationDate())
				.reservationTime(tableBooking.getRestaurantSlot().getSlot())
				.tableName(tableBooking.getRestaurantTable().getTableName()).build();

		ResponseEntity<FetchReservationResponse> responseEntity = ResponseEntity.ok()
				.eTag(String.valueOf(tableBooking.getVersion())).body(fetchReservationResponse);
		deferredResult.setResult(responseEntity);

		return deferredResult;
	}

	@GetMapping(path = "/v1/reservation/{date}")
	public DeferredResult<ResponseEntity<List<FetchReservationResponse>>> getReservationsByDate(
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

		DeferredResult<ResponseEntity<List<FetchReservationResponse>>> deferredResult = new DeferredResult<>();

		Optional<List<TableBooking>> tableBookingOptional = tableBookingService.getReservationByDate(date);

		List<FetchReservationResponse> reservationList = Collections.EMPTY_LIST;
		if (tableBookingOptional.isPresent()) {
			reservationList = tableBookingOptional.get().stream().map(tableBooking -> {
				FetchReservationResponse fetchReservationResponse = FetchReservationResponse.builder()
						.id(String.valueOf(tableBooking.getId())).name(tableBooking.getPersonName())
						.contact(tableBooking.getContact()).reservationDate(tableBooking.getReservationDate())
						.reservationTime(tableBooking.getRestaurantSlot().getSlot())
						.tableName(tableBooking.getRestaurantTable().getTableName()).build();
				return fetchReservationResponse;
			}).collect(Collectors.toList());
		}

		deferredResult.setResult(new ResponseEntity(reservationList, HttpStatus.OK));

		return deferredResult;
	}
	
	
	@PutMapping(path = "/v1/reservations/{id}")
	public DeferredResult<ResponseEntity<CreateReservationResponse>> updateReservation(WebRequest webRequest,
			@PathVariable Long id,
			@RequestBody UpdateReservationRequest updateReservationRequest) throws ValidationException {

		DeferredResult<ResponseEntity<CreateReservationResponse>> deferredResult = new DeferredResult<>();

		TableBooking tableBooking = tableBookingService.getReservationById(id);
		if (null == tableBooking) {
			throw new ReservationNotFoundException("No reservation found for id : " + id);
		}

		String ifMatchValue = webRequest.getHeader("If-Match");
		if(StringUtils.isEmpty(ifMatchValue)) {
			throw new EtagNotFoundException("Etag not found");
		}
		
		if(!ifMatchValue.equals("\"" + tableBooking.getVersion() + "\"")) {
			throw new EtagMismatchException("Etag not found");
		}
		
		tableBooking.setContact(updateReservationRequest.getContact());
		tableBooking.setPersonName(updateReservationRequest.getName());
		
		
		boolean updateTableDateTime = false;
		if(tableBooking.getReservationDate().getDate() != updateReservationRequest.getReservationDate().getDate()
				|| tableBooking.getReservationDate().getMonth() != updateReservationRequest.getReservationDate().getMonth()
				|| tableBooking.getReservationDate().getYear() != updateReservationRequest.getReservationDate().getYear()) {
			tableBooking.setReservationDate(updateReservationRequest.getReservationDate());
			updateTableDateTime = true;
		}
		
		if (tableBooking.getRestaurantSlot().getSlot().compareTo(updateReservationRequest.getReservationTime()) !=0) {
			RestaurantSlot resturantSlot = tableBookingService.getResturantSlotByTime(updateReservationRequest.getReservationTime());
			tableBooking.setRestaurantSlot(resturantSlot);
			updateTableDateTime = true;
		}
		
		if (tableBooking.getRestaurantTable().getTableName().compareTo(updateReservationRequest.getTableName()) !=0) {
			RestaurantTable resturantTable = tableBookingService.getResturantTableByName(updateReservationRequest.getTableName());
			tableBooking.setRestaurantTable(resturantTable);
			updateTableDateTime = true;
		}

		TableBooking updateTableBooking = tableBookingService.updateTableBooking(tableBooking, updateTableDateTime);
		
		CreateReservationResponse reservationUpdateResponse = null;
		if(null != updateTableBooking) {
			reservationUpdateResponse = CreateReservationResponse.builder()
					.bookingAvailability(BookingAvailability.BOOKED)
					.id(String.valueOf(updateTableBooking.getId()))
					.build();
		} else {
			reservationUpdateResponse = CreateReservationResponse.builder()
					.bookingAvailability(BookingAvailability.UNAVAILABLE)
					.id("0")
					.build();
		}
		
		deferredResult.setResult(new ResponseEntity(reservationUpdateResponse, HttpStatus.OK));

		return deferredResult;
	}
	
	
	@DeleteMapping(path = "/v1/reservations/{id}")
	public DeferredResult<ResponseEntity<CreateReservationResponse>> deleteReservationById(@PathVariable Long id) {

		DeferredResult<ResponseEntity<CreateReservationResponse>> deferredResult = new DeferredResult<>();

		tableBookingService.deleteTableBooking(id);
		
		CreateReservationResponse deleteReservationResponse = CreateReservationResponse.builder()
											.id("0")
											.bookingAvailability(BookingAvailability.UNRESERVED)
											.build();

		deferredResult.setResult(new ResponseEntity(deleteReservationResponse,HttpStatus.OK));

		return deferredResult;
	}
	

}
