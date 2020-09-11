package com.binay.booknow.service.impl;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.persistence.repository.RestaurantSlotRepository;
import com.binay.booknow.persistence.repository.RestaurantTableRepository;
import com.binay.booknow.persistence.repository.TableBookingRepository;
import com.binay.booknow.rest.dto.AvailableSlotResponse;

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
	public void create_reservation_should_create_reservation_and_return_created_object() {
		
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
		
		TableBooking tableBookingToBeDone = TableBooking.builder()
				.contact("CONTACT")
				.personName("NAME")
				.reservationDate(reservationDate)
				.restaurantSlot(restaurantSlot)
				.restaurantTable(restaurantTable)
				.build();
		
		//When
		Optional<TableBooking> tableBookingOptional = Optional.empty();
		when(tableBookingRepository.getReservationByDateTableAndSlot(anyObject(), anyString(), anyString()))
		.thenReturn(tableBookingOptional);
		
		//Then
		
	}

}
