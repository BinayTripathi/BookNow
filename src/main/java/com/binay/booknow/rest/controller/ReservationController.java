package com.binay.booknow.rest.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.binay.booknow.persistence.dao.TableBookingDao;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.rest.dto.AvailableSlotResponse;
import com.binay.booknow.rest.dto.BookingAvailability;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponse;
import com.binay.booknow.rest.exception.ReservationNotFoundException;

@RestController
public class ReservationController {

	@Autowired
	TableBookingDao tableBookingDao;

	@GetMapping(path = "/v1/availableSlots/{date}")
	public DeferredResult<List<AvailableSlotResponse>> getAllAvailabilityOnDate(
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

		DeferredResult<List<AvailableSlotResponse>> deferredResult = new DeferredResult<>();

		List<Object[]> slotAndTable = tableBookingDao.getSlotAndTable(date);

		List<AvailableSlotResponse> availability = slotAndTable.stream().map(entry -> {
			return AvailableSlotResponse.builder().availableTime((String) entry[1]).availableDate(date)
					.tableName((String) entry[0]).build();
		}).collect(Collectors.toList());

		// ResponseEntity.
		deferredResult.setResult(availability);
		return deferredResult;
	}

	@PostMapping(path = "/v1/reservations")
	public DeferredResult<CreateReservationResponse> createReservation(
			@Valid @RequestBody CreateReservationRequest createReservation) throws ValidationException {

		DeferredResult<CreateReservationResponse> deferredResult = new DeferredResult<>();

		CreateReservationResponse createReservationResponse = tableBookingDao.createReservation(createReservation);
		deferredResult.setResult(createReservationResponse);

		return deferredResult;
	}

	@GetMapping(path = "/v1/reservation/{id}")
	public DeferredResult<FetchReservationResponse> getReservationById(@PathVariable Long id) {

		DeferredResult<FetchReservationResponse> deferredResult = new DeferredResult<>();

		TableBooking tableBooking = tableBookingDao.getReservationById(id);
		if (null == tableBooking) {
			throw new ReservationNotFoundException("No reservation found for id : " + id);
		}

		FetchReservationResponse fetchReservationResponse = FetchReservationResponse.builder()
				.id(String.valueOf(tableBooking.getId())).name(tableBooking.getPersonName())
				.contact(tableBooking.getContact()).reservationDate(tableBooking.getReservationDate())
				.reservationTime(tableBooking.getRestaurantSlot().getSlot())
				.tableName(tableBooking.getRestaurantTable().getTableName()).build();

		deferredResult.setResult(fetchReservationResponse);

		return deferredResult;
	}

	@GetMapping(path = "/v1/reservations/{date}")
	public DeferredResult<List<FetchReservationResponse>> getReservationsByDate(
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

		DeferredResult<List<FetchReservationResponse>> deferredResult = new DeferredResult<>();

		Optional<List<TableBooking>> tableBookingOptional = tableBookingDao.getReservationByDate(date);

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

		deferredResult.setResult(reservationList);

		return deferredResult;
	}

}
