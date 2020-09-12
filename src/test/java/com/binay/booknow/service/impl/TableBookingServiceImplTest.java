package com.binay.booknow.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.rest.dto.BookingAvailability;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponse;
import com.binay.booknow.rest.dto.FetchReservationResponseWrapper;
import com.binay.booknow.rest.exception.EtagMismatchException;
import com.binay.booknow.service.ITableBookingCommandService;
import com.binay.booknow.service.ITableBookingQueryService;

@RunWith(MockitoJUnitRunner.class)
public class TableBookingServiceImplTest {

	@Mock
	ITableBookingQueryService tableBookingQueryServiceMock;

	@Mock
	ITableBookingCommandService tableBookingCommandServiceMock;

	@InjectMocks
	TableBookingServiceImpl tableBookingService;

	@Captor
	ArgumentCaptor<Date> dateArgsCaptor;

	@Captor
	ArgumentCaptor<String> tableArgCaptor;

	@Captor
	ArgumentCaptor<String> slotArgCaptor;
	
	
	
	CreateReservationRequest createReservationRequest;
	TableBooking tableBooked;
	RestaurantSlot restaurantSlot;
	RestaurantTable restaurantTable;
	
	
	
	@Before
	public void setup() {
		
		Date reservationDate = null;
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
		try {
			reservationDate = parser.parse("2021-12-12");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String contact = "CONTACT";
		String name = "NAME";
		String reservationSlot = "1PM-3PM";
		String tableName = "table1";
		Long id = 10L;

		restaurantSlot = new RestaurantSlot();
		restaurantSlot.setSlot(reservationSlot);

		restaurantTable = new RestaurantTable();
		restaurantTable.setTableName(tableName);

		createReservationRequest = CreateReservationRequest.builder().contact(contact)
				.name(name).reservationDate(reservationDate).reservationTime(reservationSlot).tableName(tableName)
				.build();

		tableBooked = TableBooking.builder().contact(contact).personName(contact)
				.reservationDate(reservationDate).restaurantSlot(restaurantSlot).restaurantTable(restaurantTable).id(id)
				.version(0).build();
		
	}
	
	
	

	@Test
	public void getSlotAndTable_should_return_list_of_AvailableSlotResponse_received_from_tableBookingRepository() {

		// Given
		String[] emptySlot1 = new String[] { "11AM-1PM", "table1" };
		String[] emptySlot2 = new String[] { "11AM-1PM", "table2" };
		List<String[]> listByRepository = new ArrayList();
		listByRepository.add(emptySlot1);
		listByRepository.add(emptySlot2);
		Date checkcReservationDate = new Date();
		
		when(tableBookingQueryServiceMock.getFreeSlotAndTable(anyObject())).thenReturn(listByRepository);

		// When
		List<String[]> freeSlotAndTable = tableBookingQueryServiceMock.getFreeSlotAndTable(checkcReservationDate);

		// Then
		verify(tableBookingQueryServiceMock).getFreeSlotAndTable(dateArgsCaptor.capture());
		assertEquals(dateArgsCaptor.getValue(), checkcReservationDate);
		assertEquals(listByRepository.size(), freeSlotAndTable.size());
	}

	@Test
	public void create_reservation_should_create_reservation_when_slot_is_free() throws Throwable {

		// Given
		when(tableBookingQueryServiceMock.getResturantTableByName(anyString())).thenReturn(restaurantTable);
		when(tableBookingQueryServiceMock.getResturantSlotByTime(anyString())).thenReturn(restaurantSlot);

		Optional<TableBooking> tableBookingOptional = Optional.of(tableBooked);
		when(tableBookingCommandServiceMock.createReservation(anyObject())).thenReturn(tableBookingOptional);

		// When
		CreateReservationResponse createReservation = tableBookingService.createReservation(createReservationRequest);

		// Then
		verify(tableBookingQueryServiceMock).getResturantTableByName(tableArgCaptor.capture());
		verify(tableBookingQueryServiceMock).getResturantSlotByTime(slotArgCaptor.capture());

		assertEquals(tableArgCaptor.getValue(), restaurantTable.getTableName());
		assertEquals(slotArgCaptor.getValue(), restaurantSlot.getSlot());

		assertEquals(createReservation.getBookingAvailability(), BookingAvailability.BOOKED);
		assertEquals(createReservation.getId(), String.valueOf(tableBooked.getId()));

	}

	@Test
	public void create_reservation_should_not_create_reservation_if_slot_is_booked() throws Throwable {

		// Given
		when(tableBookingQueryServiceMock.getResturantTableByName(anyString())).thenReturn(restaurantTable);
		when(tableBookingQueryServiceMock.getResturantSlotByTime(anyString())).thenReturn(restaurantSlot);

		Optional<TableBooking> tableBookingOptional = Optional.empty();
		when(tableBookingCommandServiceMock.createReservation(anyObject())).thenReturn(tableBookingOptional);

		// When
		CreateReservationResponse createReservation = tableBookingService.createReservation(createReservationRequest);
		
		//Then
		verify(tableBookingQueryServiceMock).getResturantTableByName(tableArgCaptor.capture());
		verify(tableBookingQueryServiceMock).getResturantSlotByTime(slotArgCaptor.capture());

		assertEquals(tableArgCaptor.getValue(), restaurantTable.getTableName());
		assertEquals(slotArgCaptor.getValue(), restaurantSlot.getSlot());

		assertEquals(createReservation.getBookingAvailability(), BookingAvailability.UNAVAILABLE);
		assertEquals(createReservation.getId(), String.valueOf(0));

	}

	@Test(expected=ValidationException.class)
	public void create_reservation_should_throw_exception_when_wrong_table_name_requested() throws Throwable  {

		// Given
		String tableName = "WrongTable";
		CreateReservationRequest createReservationRequest = CreateReservationRequest.builder().tableName(tableName)
				.build();
		when(tableBookingQueryServiceMock.getResturantTableByName(anyString())).thenThrow(ValidationException.class);

		// When
		CreateReservationResponse createReservation = tableBookingService.createReservation(createReservationRequest);
		
				
		//Then throw exception
		
	}
	
	@Test(expected=ValidationException.class)
	public void create_reservation_should_throw_exception_when_wrong_slot_requested() throws Throwable  {

		// Given
		String slot = "WrongSlot";
		CreateReservationRequest createReservationRequest = CreateReservationRequest.builder().reservationTime(slot)
				.build();
		when(tableBookingQueryServiceMock.getResturantSlotByTime(anyString())).thenThrow(ValidationException.class);

		// When
		CreateReservationResponse createReservation = tableBookingService.createReservation(createReservationRequest);
		
		// Then throw exception
		
	}
	
	
	@Test
	public void getReservation_should_return_reservation_if_valid_reservation_id_provided() {
		
		//Given
		when(tableBookingQueryServiceMock.getReservationById(tableBooked.getId())).thenReturn(tableBooked);
		
		//Then
		FetchReservationResponseWrapper reservationWrapper = tableBookingService.getReservation(tableBooked.getId());
		
		//Then
		assertTrue(reservationWrapper.getFetchReservationResponse().getId().compareTo(String.valueOf(tableBooked.getId())) == 0);
	}
	
	@Test
	public void getReservation_should_return_reservation_if_valid_reservation_date_provided() {
		
		//Given
		List<TableBooking> bookingList = new ArrayList<TableBooking>();
		bookingList.add(tableBooked);
		when(tableBookingQueryServiceMock.getReservationByDate(tableBooked.getReservationDate())).thenReturn(Optional.of(bookingList));
		
		//When
		List<FetchReservationResponse> reservationList = tableBookingService.getReservation(tableBooked.getReservationDate());
		
		//Then
		assertTrue(reservationList.size() == bookingList.size());
		assertTrue(reservationList.get(0).getId().compareTo(String.valueOf(tableBooked.getId())) == 0);
	}
	
	@Test(expected = EtagMismatchException.class)
	public void updateTableBooking_should_thrown_error_etag_mismatches() throws ValidationException {
		
		
		//Given
		createReservationRequest.setContact("UpdatedContact");
		when(tableBookingQueryServiceMock.getReservationById(tableBooked.getId())).thenReturn(tableBooked);
		
		//When
		tableBookingService.updateTableBooking(tableBooked.getId(), createReservationRequest, "wrong etag");
		
		//Then throw exception
	}
	
	@Test
	public void updateTableBooking_should_update_reservation_when_contact_name_updated() throws ValidationException {
		
		
		//Given
		String updatedContact = "UpdatedContact";
		String updatedName = "updatedName";
		createReservationRequest.setContact(updatedContact);
		createReservationRequest.setName(updatedName);
		String etag = "\"" + tableBooked.getVersion() + "\"";
		
		TableBooking updatedTableBooking = new TableBooking();
		updatedTableBooking.setId(10L);
		
		when(tableBookingQueryServiceMock.getReservationById(any())).thenReturn(tableBooked);
		when(tableBookingCommandServiceMock.updateTableBooking(anyObject())).thenReturn(updatedTableBooking);
		
		
		//When		
		CreateReservationResponse updateTableBookingResp = tableBookingService.updateTableBooking(tableBooked.getId(), createReservationRequest, etag);
		
		//Then
		assertTrue(updateTableBookingResp.getBookingAvailability() == BookingAvailability.BOOKED);
		assertTrue(updateTableBookingResp.getId().compareTo(String.valueOf(updatedTableBooking.getId())) == 0);
		verify(tableBookingCommandServiceMock, never()).createReservation(anyObject());
		verify(tableBookingCommandServiceMock, times(1)).updateTableBooking(anyObject());
		
	}
	
	@Test(expected = ObjectOptimisticLockingFailureException.class)
	public void updateTableBooking_should_throw_exception_when_contact_name_updated_but_a_concurrent_update_happened() throws ValidationException {
		
		
		//Given
		String updatedContact = "UpdatedContact";
		String updatedName = "updatedName";
		createReservationRequest.setContact(updatedContact);
		createReservationRequest.setName(updatedName);
		String etag = "\"" + tableBooked.getVersion() + "\"";
		
		TableBooking updatedTableBooking = new TableBooking();
		updatedTableBooking.setId(10L);
		
		
		when(tableBookingQueryServiceMock.getReservationById(any())).thenReturn(tableBooked);
		when(tableBookingCommandServiceMock.updateTableBooking(anyObject())).thenThrow(ObjectOptimisticLockingFailureException.class);
		
		
		//When
		CreateReservationResponse updateTableBookingResp = tableBookingService.updateTableBooking(tableBooked.getId(), createReservationRequest, etag);
		
		//Then thrown exception

	}
	
	
	@Test
	public void updateTableBooking_should_update_reservation_when_date_slot_table_updated() throws ValidationException {
		
		
		//Given
		String updatedContact = "UpdatedContact";
		String updatedName = "updatedName";
		String updatedSlot = "updatedSlot";
		String updateTable = "updatedTable";
		Date updatedDated = new Date();
		createReservationRequest.setReservationDate(updatedDated);
		createReservationRequest.setReservationTime(updatedSlot);
		createReservationRequest.setTableName(updateTable);
		
		
		String etag = "\"" + tableBooked.getVersion() + "\"";
		
		TableBooking updatedTableBooking = new TableBooking();
		updatedTableBooking.setId(10L);
		
		when(tableBookingQueryServiceMock.getReservationById(any())).thenReturn(tableBooked);
		when(tableBookingCommandServiceMock.createReservation(anyObject())).thenReturn(Optional.of(updatedTableBooking));
		
		//When
		CreateReservationResponse updateTableBookingResp = tableBookingService.updateTableBooking(tableBooked.getId(), createReservationRequest, etag);
		
		//Then
		assertTrue(updateTableBookingResp.getBookingAvailability() == BookingAvailability.BOOKED);
		assertTrue(updateTableBookingResp.getId().compareTo(String.valueOf(updatedTableBooking.getId())) == 0);
		verify(tableBookingCommandServiceMock, times(1)).createReservation(anyObject());
		verify(tableBookingCommandServiceMock, never()).updateTableBooking(anyObject());
		
	}
	
	@Test
	public void updateTableBooking_should_not_update_reservation_when_table_not_available_on_date_and_time() throws ValidationException {
		
		
		//Given
		String updatedContact = "UpdatedContact";
		String updatedName = "updatedName";
		String updatedSlot = "updatedSlot";
		String updateTable = "updatedTable";
		Date updatedDated = new Date();
		createReservationRequest.setReservationDate(updatedDated);
		createReservationRequest.setReservationTime(updatedSlot);
		createReservationRequest.setTableName(updateTable);
		
		
		String etag = "\"" + tableBooked.getVersion() + "\"";
		
		TableBooking updatedTableBooking = new TableBooking();
		updatedTableBooking.setId(10L);
		
		when(tableBookingQueryServiceMock.getReservationById(any())).thenReturn(tableBooked);
		when(tableBookingCommandServiceMock.createReservation(anyObject())).thenReturn(Optional.empty());
		
		//When
		CreateReservationResponse updateTableBookingResp = tableBookingService.updateTableBooking(tableBooked.getId(), createReservationRequest, etag);
		
		//Then
		assertTrue(updateTableBookingResp.getBookingAvailability() == BookingAvailability.UNAVAILABLE);
		assertTrue(updateTableBookingResp.getId().compareTo("0") == 0);
		verify(tableBookingCommandServiceMock, times(1)).createReservation(anyObject());
		verify(tableBookingCommandServiceMock, never()).updateTableBooking(anyObject());
		
	}
	
	@Test
	public void deleteTableBooking_should_return_response_indicating_Reserved_is_Unreserved() {
		
		//Given
		when(tableBookingCommandServiceMock.deleteTableBooking(any())).thenReturn(true);
		
		//When
		CreateReservationResponse deleteTableBookingResp = tableBookingService.deleteTableBooking(10L);
		
		//Then
		assertTrue(deleteTableBookingResp.getBookingAvailability() == BookingAvailability.UNRESERVED);
		assertTrue(deleteTableBookingResp.getId().compareTo("0") == 0);
		verify(tableBookingCommandServiceMock, times(1)).deleteTableBooking(any());
		
	}
	
}
