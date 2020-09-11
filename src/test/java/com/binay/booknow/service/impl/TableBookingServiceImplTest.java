package com.binay.booknow.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.TransactionTimedOutException;

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.persistence.repository.RestaurantSlotRepository;
import com.binay.booknow.persistence.repository.RestaurantTableRepository;
import com.binay.booknow.persistence.repository.TableBookingRepository;

@RunWith(MockitoJUnitRunner.class)
public class TableBookingServiceImplTest {
	
	
	@Mock
	TableBookingRepository tableBookingRepository;

	@MockBean
	RestaurantTableRepository restaurantTableRepository;

	@MockBean
	RestaurantSlotRepository restaurantSlotRepository;
	
	@InjectMocks
	TableBookingServiceImpl tableBookingService; 
	
	@Captor
	ArgumentCaptor<Date> dateArgsCaptor;
	
	@Captor
	ArgumentCaptor<String> tableArgCaptor;
	
	@Captor
	ArgumentCaptor<String> slotArgCaptor;

	@Test
	public void getSlotAndTable_should_return_list_of_object_received_from_tableBookingRepository() {
		
		//Given
		String[] emptySlot1 = new String[]{"11AM-1PM", "table1"};
		String[] emptySlot2 = new String[]{"11AM-1PM", "table2"};
		List<String[]> listByRepository = new ArrayList();
		listByRepository.add(emptySlot1);
		listByRepository.add(emptySlot2);		
		Date checkcReservationDate = new Date();	
		
		//When		
		when(tableBookingRepository.getAllFreeTableAndSlotForDate(anyObject())).thenReturn(listByRepository);
		
		List<String[]> freeSlotAndTable = tableBookingService.getFreeSlotAndTable(checkcReservationDate);
		
		//Then		
		verify(tableBookingRepository).getAllFreeTableAndSlotForDate(dateArgsCaptor.capture());
		assertEquals(dateArgsCaptor.getValue(), checkcReservationDate);
		assertEquals(listByRepository.size(),freeSlotAndTable.size());
		for(int index = 0 ; index < listByRepository.size(); index++) {			
			 assertTrue(((String)listByRepository.get(index)[0]).compareTo((String)freeSlotAndTable.get(index)[0]) == 0);
			 assertTrue(((String)listByRepository.get(index)[1]).compareTo((String)freeSlotAndTable.get(index)[1]) == 0);			
		}
		
	}
	
	
	
	
	
	
	@Test
	public void create_reservation_should_create_reservation_when_slot_is_free() {
		
		//Given
		RestaurantSlot restaurantSlot = new RestaurantSlot();
		restaurantSlot.setSlot("1PM-3PM");
		
		RestaurantTable restaurantTable = new RestaurantTable();
		restaurantTable.setTableName("table1");
		
		Date reservationDate = null;
		SimpleDateFormat parser=new SimpleDateFormat("yyyy-MM-dd");
		try {
			reservationDate = parser.parse("2021-12-12");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String contact = "CONTACT";
		String name = "NAME";
		TableBooking tableBookingToBeDone = TableBooking.builder()
				.contact(contact)
				.personName(name)
				.reservationDate(reservationDate)
				.restaurantSlot(restaurantSlot)
				.restaurantTable(restaurantTable)
				.build();
		
		TableBooking tableBooked = TableBooking.builder()
				.contact(tableBookingToBeDone.getContact())
				.personName(tableBookingToBeDone.getPersonName())
				.reservationDate(reservationDate)
				.restaurantSlot(restaurantSlot)
				.restaurantTable(restaurantTable)
				.id(1L)
				.version(1L)
				.build();
		
		//When
		Optional<TableBooking> tableBookingOptional = Optional.empty();
		when(tableBookingRepository.getReservationByDateTableAndSlot(anyObject(), anyString(), anyString()))
		.thenReturn(tableBookingOptional);
		
		

		when(tableBookingRepository.save(anyObject())).thenReturn(tableBooked);
		
		//Then
		Optional<TableBooking> tableBookedOptional = tableBookingService.createReservation(tableBookingToBeDone);
		TableBooking tableBookedTest = tableBookedOptional.get();
		verify(tableBookingRepository).getReservationByDateTableAndSlot(dateArgsCaptor.capture(), tableArgCaptor.capture(), slotArgCaptor.capture());
		assertEquals(dateArgsCaptor.getValue(), tableBookingToBeDone.getReservationDate());
		assertEquals(tableArgCaptor.getValue(), tableBookingToBeDone.getRestaurantTable().getTableName());
		assertEquals(slotArgCaptor.getValue(), tableBookingToBeDone.getRestaurantSlot().getSlot());
		
		assertEquals(tableBookedTest.getContact(), tableBooked.getContact());
		assertEquals(tableBookedTest.getPersonName(), tableBooked.getPersonName());
		assertEquals(tableBookedTest.getReservationDate(), tableBooked.getReservationDate());
		assertEquals(tableBookedTest.getRestaurantSlot().getId(), tableBooked.getRestaurantSlot().getId());
		assertEquals(tableBookedTest.getRestaurantTable().getId(), tableBooked.getRestaurantTable().getId());
	}
	
	
	@Test
	public void create_reservation_should_not_create_reservation_if_slot_is_booked() {
		
		//Given
		RestaurantSlot restaurantSlot = new RestaurantSlot();
		restaurantSlot.setSlot("1PM-3PM");
		
		RestaurantTable restaurantTable = new RestaurantTable();
		restaurantTable.setTableName("table1");
		
		Date reservationDate = null;
		SimpleDateFormat parser=new SimpleDateFormat("yyyy-MM-dd");
		try {
			reservationDate = parser.parse("2021-12-12");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String contact = "CONTACT";
		String name = "NAME";
		TableBooking tableBookingToBeDone = TableBooking.builder()
				.contact(contact)
				.personName(name)
				.reservationDate(reservationDate)
				.restaurantSlot(restaurantSlot)
				.restaurantTable(restaurantTable)
				.build();
		
		TableBooking tableBooked = TableBooking.builder()
				.contact(tableBookingToBeDone.getContact())
				.personName(tableBookingToBeDone.getPersonName())
				.reservationDate(reservationDate)
				.restaurantSlot(restaurantSlot)
				.restaurantTable(restaurantTable)
				.id(1L)
				.version(1L)
				.build();
		
		//When
		Optional<TableBooking> tableBookingOptional = Optional.of(tableBooked);
		when(tableBookingRepository.getReservationByDateTableAndSlot(anyObject(), anyString(), anyString()))
		.thenReturn(tableBookingOptional);
		
		//Then
		Optional<TableBooking> createReservation = tableBookingService.createReservation(tableBookingToBeDone);
		verify(tableBookingRepository).getReservationByDateTableAndSlot(dateArgsCaptor.capture(), tableArgCaptor.capture(), slotArgCaptor.capture());
		assertEquals(dateArgsCaptor.getValue(), tableBookingToBeDone.getReservationDate());
		assertEquals(tableArgCaptor.getValue(), tableBookingToBeDone.getRestaurantTable().getTableName());
		assertEquals(slotArgCaptor.getValue(), tableBookingToBeDone.getRestaurantSlot().getSlot());
		assertFalse(createReservation.isPresent());
		
		
	}

	
	@Test(expected = TransactionTimedOutException.class)
	public void create_reservation_should_throw_exception_when_transaction_timesout() {
		
		//Given
		RestaurantSlot restaurantSlot = new RestaurantSlot();
		restaurantSlot.setSlot("1PM-3PM");
		
		RestaurantTable restaurantTable = new RestaurantTable();
		restaurantTable.setTableName("table1");
		
		Date reservationDate = null;
		SimpleDateFormat parser=new SimpleDateFormat("yyyy-MM-dd");
		try {
			reservationDate = parser.parse("2021-12-12");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String contact = "CONTACT";
		String name = "NAME";
		TableBooking tableBookingToBeDone = TableBooking.builder()
				.contact(contact)
				.personName(name)
				.reservationDate(reservationDate)
				.restaurantSlot(restaurantSlot)
				.restaurantTable(restaurantTable)
				.build();
		
		
		//When
		when(tableBookingRepository.getReservationByDateTableAndSlot(anyObject(), anyString(), anyString()))
		.thenThrow(new TransactionTimedOutException("Transaction timeout"));
		
		
		//Then
		Optional<TableBooking> tableBookedOptional = tableBookingService.createReservation(tableBookingToBeDone);
		verify(tableBookingRepository).getReservationByDateTableAndSlot(dateArgsCaptor.capture(), tableArgCaptor.capture(), slotArgCaptor.capture());
		assertEquals(dateArgsCaptor.getValue(), tableBookingToBeDone.getReservationDate());
		assertEquals(tableArgCaptor.getValue(), tableBookingToBeDone.getRestaurantTable().getTableName());
		assertEquals(slotArgCaptor.getValue(), tableBookingToBeDone.getRestaurantSlot().getSlot());
	}
}
