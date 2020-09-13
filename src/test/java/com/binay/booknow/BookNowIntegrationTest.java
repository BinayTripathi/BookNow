package com.binay.booknow;

import static com.binay.booknow.ApplicationConstants.URI_VI_CREATE_RESERVATION;
import static com.binay.booknow.ApplicationConstants.URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE;
import static com.binay.booknow.ApplicationConstants.URI_VI_GET_RESERVATIONS_BY_ID;
import static com.binay.booknow.ApplicationConstants.URI_VI_GET_RESERVATIONS_BY_DATE;
import static com.binay.booknow.ApplicationConstants.URI_VI_UPDATE_RESERVATIONS_BY_ID;
import static com.binay.booknow.ApplicationConstants.URI_VI_DELETE_RESERVATIONS_BY_ID;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.binay.booknow.rest.dto.AvailableSlotResponse;
import com.binay.booknow.rest.dto.BookingAvailability;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = BookNowApplication.class)
public class BookNowIntegrationTest {

	@LocalServerPort
	private int port;

	private TestRestTemplate restTemplate = new TestRestTemplate();

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	ObjectMapper objectMapper = new ObjectMapper();

	String uriPrefix;

	@Before
	public void setup() {
		uriPrefix = "http://localhost:" + port;
	}

	@Test
	@DirtiesContext
	public void when_reservation_is_created_that_date_slot_table_combination_becomes_unavailable() throws Exception {

		String dateOfReservation = "2021-09-30";

		ResponseEntity<AvailableSlotResponse[]> responseEntity = this.restTemplate.getForEntity(
				uriPrefix + URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE.replace("{date}", dateOfReservation),
				AvailableSlotResponse[].class);

		AvailableSlotResponse[] body = responseEntity.getBody();
		assertTrue(body.length == 80);
		assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

		CreateReservationRequest req = new CreateReservationRequest(1L, "name", "contact",
				dateFormat.parse(dateOfReservation), "5PM-7PM", "table1");

		ResponseEntity<CreateReservationResponse> postForEntity = this.restTemplate
				.postForEntity(uriPrefix + URI_VI_CREATE_RESERVATION, req, CreateReservationResponse.class);

		responseEntity = this.restTemplate.getForEntity(
				uriPrefix + URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE.replace("{date}", dateOfReservation),
				AvailableSlotResponse[].class);

		body = responseEntity.getBody();
		assertTrue(body.length == 79);
		assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

	}

	@Test
	@DirtiesContext
	public void when_reservation_is_created_find_by_id_and_find_by_date_works() throws Exception {

		String idOfBooking = "1";
		String dateOfReservation = "2021-12-12";

		// CREATE RESERVATION
		CreateReservationRequest req = new CreateReservationRequest(1L, "name", "contact",
				dateFormat.parse(dateOfReservation), "5PM-7PM", "table1");

		ResponseEntity<CreateReservationResponse> postForEntity = this.restTemplate
				.postForEntity(uriPrefix + URI_VI_CREATE_RESERVATION, req, CreateReservationResponse.class);
		assertTrue(postForEntity.getStatusCode() == HttpStatus.CREATED);

		// GET RESERVATION BY ID
		ResponseEntity<FetchReservationResponse> responseEntity = this.restTemplate.getForEntity(
				uriPrefix + URI_VI_GET_RESERVATIONS_BY_ID.replace("{id}", idOfBooking), FetchReservationResponse.class);

		FetchReservationResponse body = responseEntity.getBody();
		HttpHeaders headers = responseEntity.getHeaders();
		List<String> etagList = headers.get("etag");
		assertTrue(etagList.get(0).compareTo("\"0\"") == 0);
		assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

		// GET RESERVATIONS BY DATE
		ResponseEntity<FetchReservationResponse[]> responseEntityReservationByDate = this.restTemplate.getForEntity(
				uriPrefix + URI_VI_GET_RESERVATIONS_BY_DATE.replace("{date}", dateOfReservation),
				FetchReservationResponse[].class);

		FetchReservationResponse[] respEntityList = responseEntityReservationByDate.getBody();
		assertTrue(respEntityList.length == 1);
		assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

	}

	@Test
	@DirtiesContext
	public void reservation_creation_update_delete_test() throws Exception {

		String idOfBooking = "1";
		String dateOfReservation = "2021-12-12";

		// CREATE RESERVATION
		CreateReservationRequest reservationRequest = new CreateReservationRequest(1L, "name", "contact",
				dateFormat.parse(dateOfReservation), "5PM-7PM", "table1");

		ResponseEntity<CreateReservationResponse> postForEntity = this.restTemplate.postForEntity(
				uriPrefix + URI_VI_CREATE_RESERVATION, reservationRequest, CreateReservationResponse.class);
		assertTrue(postForEntity.getStatusCode() == HttpStatus.CREATED);

		// GET RESERVATION BY ID
		ResponseEntity<FetchReservationResponse> responseEntity = this.restTemplate.getForEntity(
				uriPrefix + URI_VI_GET_RESERVATIONS_BY_ID.replace("{id}", idOfBooking), FetchReservationResponse.class);

		FetchReservationResponse body = responseEntity.getBody();
		HttpHeaders headers = responseEntity.getHeaders();
		List<String> etagList = headers.get("etag");
		assertTrue(etagList.get(0).compareTo("\"0\"") == 0);
		assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

		// UPDATE RESERVATION
		String updatedName = "UpdatedName";
		reservationRequest.setName(updatedName);
		MultiValueMap<String, String> headersReq = new LinkedMultiValueMap<String, String>();
		headersReq.add("HeaderName", "value");
		headersReq.add("Content-Type", "application/json");
		headersReq.add("If-Match", "\"0\"");
		HttpEntity<CreateReservationRequest> requestUpdate = new HttpEntity<>(reservationRequest, headersReq);
		ResponseEntity<CreateReservationResponse> updateResponse = this.restTemplate.exchange(
				uriPrefix + URI_VI_UPDATE_RESERVATIONS_BY_ID.replace("{id}", "1"), HttpMethod.PUT, requestUpdate, CreateReservationResponse.class);
		
		assertTrue(updateResponse.getStatusCode() == HttpStatus.OK);
		assertTrue(updateResponse.getBody().getBookingAvailability().compareTo(BookingAvailability.BOOKED) == 0);
		

	}

}
