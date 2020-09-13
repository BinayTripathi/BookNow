package com.binay.booknow;

import static com.binay.booknow.ApplicationConstants.URI_VI_CREATE_RESERVATION;
import static com.binay.booknow.ApplicationConstants.URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE;
import static com.binay.booknow.ApplicationConstants.URI_VI_GET_RESERVATIONS_BY_ID;
import static com.binay.booknow.ApplicationConstants.URI_VI_GET_RESERVATIONS_BY_DATE;
import static com.binay.booknow.ApplicationConstants.URI_VI_UPDATE_RESERVATIONS_BY_ID;
import static com.binay.booknow.ApplicationConstants.URI_VI_DELETE_RESERVATIONS_BY_ID;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.binay.booknow.rest.dto.AvailableSlotResponse;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;
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
	public void test_getAllAvailabilityOnDate() throws Exception {
		
		String dateOfReservation = "2021-09-30";
		
		 ResponseEntity<AvailableSlotResponse[]> responseEntity = this.restTemplate
				 .getForEntity(uriPrefix + URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE.replace("{date}",
						 dateOfReservation), AvailableSlotResponse[].class);
		 
		 AvailableSlotResponse[] body = responseEntity.getBody();
		 assertTrue(body.length == 80);
		 assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);
		 
		 
		 CreateReservationRequest req = new CreateReservationRequest(1L, "name", "contact",
					dateFormat.parse(dateOfReservation), "5PM-7PM", "table1");
		 
		 ResponseEntity<CreateReservationResponse> postForEntity = this.restTemplate
				 		.postForEntity(uriPrefix + URI_VI_CREATE_RESERVATION,req,CreateReservationResponse.class);
		 
		 
		 responseEntity = this.restTemplate
				 .getForEntity(uriPrefix + URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE.replace("{date}",
						 dateOfReservation), AvailableSlotResponse[].class);
		 
		 body = responseEntity.getBody();
		 assertTrue(body.length == 79);
		 assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);

	}


}
