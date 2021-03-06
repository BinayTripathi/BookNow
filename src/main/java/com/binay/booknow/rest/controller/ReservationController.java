package com.binay.booknow.rest.controller;

import static com.binay.booknow.ApplicationConstants.URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE;
import static com.binay.booknow.ApplicationConstants.URI_VI_CREATE_RESERVATION;
import static com.binay.booknow.ApplicationConstants.URI_VI_DELETE_RESERVATIONS_BY_ID;
import static com.binay.booknow.ApplicationConstants.URI_VI_GET_RESERVATIONS_BY_DATE;
import static com.binay.booknow.ApplicationConstants.URI_VI_GET_RESERVATIONS_BY_ID;
import static com.binay.booknow.ApplicationConstants.URI_VI_UPDATE_RESERVATIONS_BY_ID;

import java.net.URI;
import java.util.Date;
import java.util.List;

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

import com.binay.booknow.rest.dto.AvailableSlotResponse;
import com.binay.booknow.rest.dto.BookingAvailability;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponseWrapper;
import com.binay.booknow.rest.exception.EtagNotFoundException;
import com.binay.booknow.service.ITableBookingService;

@RestController
public class ReservationController {

	@Autowired
	ITableBookingService tableBookingService;
	
	

	@GetMapping(path = URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE)
	public DeferredResult<ResponseEntity<List<AvailableSlotResponse>>> getAllAvailabilityOnDate(
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

		List<AvailableSlotResponse> freeSlotAndTable = tableBookingService.getFreeSlotAndTable(date);		

		DeferredResult<ResponseEntity<List<AvailableSlotResponse>>> deferredResult = new DeferredResult<>();
		deferredResult.setResult(ResponseEntity.ok(freeSlotAndTable));
		return deferredResult;
	}
	
	
	
	

	@PostMapping(path = URI_VI_CREATE_RESERVATION)
	public DeferredResult<ResponseEntity<CreateReservationResponse>> createReservation(
			@Valid @RequestBody CreateReservationRequest createReservation) throws Throwable {

					
		CreateReservationResponse createReservationResponse = tableBookingService.createReservation(createReservation);
		
		
		ResponseEntity<CreateReservationResponse> reservationResponseEntity ;
		if(createReservationResponse.getBookingAvailability() == BookingAvailability.BOOKED) {
			 //Create resource location
			URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createReservationResponse.getId())
                    .toUri();

			reservationResponseEntity = ResponseEntity.created(location)
														.body(createReservationResponse);
		} else {
			reservationResponseEntity = new ResponseEntity(createReservationResponse,HttpStatus.CONFLICT);
		}
        
        
        DeferredResult<ResponseEntity<CreateReservationResponse>> deferredResult = new DeferredResult<>();
        deferredResult.setResult(reservationResponseEntity);		
		return deferredResult;
	}
	
	
	
	
	

	@GetMapping(path = URI_VI_GET_RESERVATIONS_BY_ID)
	public DeferredResult<ResponseEntity<FetchReservationResponse>> getReservationById(@PathVariable Long id) {

		FetchReservationResponseWrapper fetchReservationResponseWrapper = tableBookingService.getReservation(id);
		

		ResponseEntity<FetchReservationResponse> responseEntity = ResponseEntity.ok()
				.eTag(fetchReservationResponseWrapper.getETag())
				.body(fetchReservationResponseWrapper.getFetchReservationResponse());
		
		DeferredResult<ResponseEntity<FetchReservationResponse>> deferredResult = new DeferredResult<>();
		deferredResult.setResult(responseEntity);
		return deferredResult;
	}
	
	
	

	@GetMapping(path = URI_VI_GET_RESERVATIONS_BY_DATE)
	public DeferredResult<ResponseEntity<List<FetchReservationResponse>>> getReservationsByDate(
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

		
		List<FetchReservationResponse> reservationList = tableBookingService.getReservation(date);
		

		DeferredResult<ResponseEntity<List<FetchReservationResponse>>> deferredResult = new DeferredResult<>();
		deferredResult.setResult(new ResponseEntity(reservationList, HttpStatus.OK));
		return deferredResult;
	}
	
	
	
	
	
	@PutMapping(path = URI_VI_UPDATE_RESERVATIONS_BY_ID)
	public DeferredResult<ResponseEntity<CreateReservationResponse>> updateReservation(WebRequest webRequest,
			@PathVariable Long id,
			@RequestBody CreateReservationRequest updateReservationRequest) throws ValidationException {

		

		String ifMatchValue = webRequest.getHeader("If-Match");
		if(StringUtils.isEmpty(ifMatchValue)) {
			throw new EtagNotFoundException("Etag not found");
		}
			
		
		CreateReservationResponse reservationUpdateResponse = tableBookingService.updateTableBooking(id, updateReservationRequest, ifMatchValue);
		
		
		
		DeferredResult<ResponseEntity<CreateReservationResponse>> deferredResult = new DeferredResult<>();
		deferredResult.setResult(new ResponseEntity(reservationUpdateResponse, HttpStatus.OK));
		return deferredResult;
	}
	
	
	
	
	
	@DeleteMapping(path = URI_VI_DELETE_RESERVATIONS_BY_ID)
	public DeferredResult<ResponseEntity<CreateReservationResponse>> deleteReservation(@PathVariable Long id) {

		
		CreateReservationResponse deleteReservationResponse = tableBookingService.deleteTableBooking(id);			
		DeferredResult<ResponseEntity<CreateReservationResponse>> deferredResult = new DeferredResult<>();
		deferredResult.setResult(new ResponseEntity(deleteReservationResponse,HttpStatus.OK));

		return deferredResult;
	}
	

}
