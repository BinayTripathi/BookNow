package com.binay.booknow.rest.controller;

import static com.binay.booknow.ApplicationConstants.URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE;
import static com.binay.booknow.ApplicationConstants.URI_VI_CREATE_RESERVATION;
import static com.binay.booknow.ApplicationConstants.URI_VI_DELETE_RESERVATIONS_BY_ID;
import static com.binay.booknow.ApplicationConstants.URI_VI_GET_RESERVATIONS_BY_DATE;
import static com.binay.booknow.ApplicationConstants.URI_VI_GET_RESERVATIONS_BY_ID;
import static com.binay.booknow.ApplicationConstants.URI_VI_UPDATE_RESERVATIONS_BY_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.binay.booknow.rest.dto.AvailableSlotResponse;
import com.binay.booknow.rest.dto.BookingAvailability;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponseWrapper;
import com.binay.booknow.rest.exception.ReservationNotFoundException;
import com.binay.booknow.service.ITableBookingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	ITableBookingService tableBookingServiceMock;

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void test_getAllAvailabilityOnDate() throws Exception {

		// Setup
		String table1 = "table1";
		String table2 = "table2";

		String slot1 = "1PM-3PM";
		String slot2 = "5PM-7PM";

		String date = "2020-12-12";

		AvailableSlotResponse ars1 = new AvailableSlotResponse(table1, date, slot1);
		AvailableSlotResponse ars2 = new AvailableSlotResponse(table2, date, slot2);
		List<AvailableSlotResponse> respList = new ArrayList<>();
		respList.add(ars1);
		respList.add(ars2);
		ObjectMapper objectMapper = new ObjectMapper();
		String respAsString = objectMapper.writeValueAsString(respList);

		// Given
		when(tableBookingServiceMock.getFreeSlotAndTable(any())).thenReturn(respList);

		// When
		MvcResult mvcResult = mockMvc.perform(get(URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE, "2020-12-12")).andReturn();

		// Then
		mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()).andExpect(content().json(respAsString));

	}

	@Test
	public void test_createReservation() throws Throwable {

		// Setup
		
		CreateReservationResponse successResponse = new CreateReservationResponse("1", BookingAvailability.BOOKED);
		CreateReservationRequest req = new CreateReservationRequest(1L, "name", "contact",
				dateFormat.parse("2020-12-12"), "slot1", "table1");

		String reqBodyAsString = objectMapper.writeValueAsString(req);
		String respAsString = objectMapper.writeValueAsString(successResponse);

		// Given
		when(tableBookingServiceMock.createReservation(any())).thenReturn(successResponse);

		// when
		MvcResult mvcResult = mockMvc.perform(post(URI_VI_CREATE_RESERVATION).contentType(APPLICATION_JSON)
				.content(reqBodyAsString).accept(APPLICATION_JSON)).andReturn();

		// then
		mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isCreated())
				.andExpect(content().contentType(APPLICATION_JSON_VALUE)).andExpect(content().json(respAsString));
	}

	@Test
	public void getReservationById_should_return_valid_response_when_reservation_found() throws Exception {

		String id = "1";
		String name = "name";
		String contact = "contact";
		String resrvDate = "2020-12-12";
		String table = "table1";
		String slot = "5pm-7pm";

		FetchReservationResponse response = new FetchReservationResponse(id, name, contact, resrvDate, slot, table);
		FetchReservationResponseWrapper respWrapper = new FetchReservationResponseWrapper(response, "0");
		String responseString = objectMapper.writeValueAsString(response);

		// Given
		when(tableBookingServiceMock.getReservation(anyLong())).thenReturn(respWrapper);

		// when
		MvcResult mvcResult = mockMvc.perform(get(URI_VI_GET_RESERVATIONS_BY_ID,1)
				.accept(APPLICATION_JSON)).andReturn();

		// then
		mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_VALUE)).andExpect(content().json(responseString));
	}
	
	
	@Test
	public void getReservationById_should_return_error_response_when_reservation_not_found() throws Exception {

		when(tableBookingServiceMock.getReservation(anyLong())).thenThrow(ReservationNotFoundException.class);

		 mockMvc.perform(get(URI_VI_GET_RESERVATIONS_BY_ID,1))
				.andExpect(status().isNotFound());
	}
	
	
	
	
	@Test
	public void getReservationByDate_should_return_valid_response_when_reservation_found() throws Exception {

		String id = "1";
		String name = "name";
		String contact = "contact";
		String resrvDate = "2020-12-12";
		String table = "table1";
		String slot = "5pm-7pm";

		FetchReservationResponse response = new FetchReservationResponse(id, name, contact, resrvDate, slot, table);
		List<FetchReservationResponse> respList = new ArrayList<>();
		respList.add(response);
		String responseString = objectMapper.writeValueAsString(respList);

		// Given
		when(tableBookingServiceMock.getReservation(any(Date.class))).thenReturn(respList);

		// when
		MvcResult mvcResult = mockMvc.perform(get(URI_VI_GET_RESERVATIONS_BY_DATE,"2020-12-12")
				.accept(APPLICATION_JSON)).andReturn();

		// then
		mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_VALUE)).andExpect(content().json(responseString));
	}
	
	
	
	@Test
	public void test_updateReservation_should_update_reservation() throws Throwable {

		// Setup
		
		CreateReservationResponse successResponse = new CreateReservationResponse("1", BookingAvailability.BOOKED);
		CreateReservationRequest req = new CreateReservationRequest(1L, "name", "contact",
				dateFormat.parse("2020-12-12"), "slot1", "table1");

		String reqBodyAsString = objectMapper.writeValueAsString(req);
		String respAsString = objectMapper.writeValueAsString(successResponse);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("If-Match", "\"1\"");

		// Given
		when(tableBookingServiceMock.updateTableBooking(anyLong(),any(CreateReservationRequest.class),anyString()))
		.thenReturn(successResponse);

		// when
		MvcResult mvcResult = mockMvc.perform(put(URI_VI_UPDATE_RESERVATIONS_BY_ID,1).contentType(APPLICATION_JSON)
										.headers(headers).content(reqBodyAsString).accept(APPLICATION_JSON)).andReturn();

		// then
		mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_VALUE)).andExpect(content().json(respAsString));
	}
	
	
	@Test
	public void test_updateReservation_should_give_error_response_if_etag_header_is_misisng() throws Throwable {

		// Setup
		
		CreateReservationResponse successResponse = new CreateReservationResponse("0", BookingAvailability.UNAVAILABLE);
		CreateReservationRequest req = new CreateReservationRequest(1L, "name", "contact",
				dateFormat.parse("2020-12-12"), "slot1", "table1");

		String reqBodyAsString = objectMapper.writeValueAsString(req);
		String respAsString = objectMapper.writeValueAsString(successResponse);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("If-Match_NOT_PROVIDED", "\"1\"");

		// Given
		when(tableBookingServiceMock.updateTableBooking(anyLong(),any(CreateReservationRequest.class),anyString()))
		.thenReturn(successResponse);

		// when
		mockMvc.perform(put(URI_VI_UPDATE_RESERVATIONS_BY_ID,1)
				.contentType(APPLICATION_JSON)
				.headers(headers).content(reqBodyAsString)
				.accept(APPLICATION_JSON))
				.andExpect(status().isBadRequest());
				
	}
	
	
	@Test
	public void test_deteleReservation_should_send_Valid_Response_when_reservation_is_deleted() throws Throwable {

		// Setup		
		CreateReservationResponse successResponse = new CreateReservationResponse("1", BookingAvailability.UNRESERVED);
		String respAsString = objectMapper.writeValueAsString(successResponse);


		// Given
		when(tableBookingServiceMock.deleteTableBooking(anyLong())).thenReturn(successResponse);

		// when
		MvcResult mvcResult = mockMvc.perform(delete(URI_VI_DELETE_RESERVATIONS_BY_ID,1).contentType(APPLICATION_JSON)).andReturn();

		// then
		mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_VALUE)).andExpect(content().json(respAsString));
	}
	
	
	@Test
	public void test_deteleReservation_should_send_erro_Response_when_reservation_with_id_does_not_exists() throws Throwable {
		
		CreateReservationResponse successResponse = new CreateReservationResponse("1", BookingAvailability.UNRESERVED);
		String respAsString = objectMapper.writeValueAsString(successResponse);

		when(tableBookingServiceMock.deleteTableBooking(anyLong())).thenThrow(ReservationNotFoundException.class);

		mockMvc.perform(delete(URI_VI_DELETE_RESERVATIONS_BY_ID,1)).andExpect(status().isNotFound());

	}

}
