package com.binay.booknow.service.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.binay.booknow.persistence.repository.RestaurantSlotRepository;
import com.binay.booknow.persistence.repository.RestaurantTableRepository;
import com.binay.booknow.persistence.repository.TableBookingRepository;

@RunWith(MockitoJUnitRunner.class)
class TableBookingQueryServiceImplTest {

	@Mock
	private TableBookingRepository tableBookingRepositoryMock;

	@Mock
	private RestaurantTableRepository restaurantTableRepositoryMock;

	@Mock
	private RestaurantSlotRepository restaurantSlotRepositoryMock;
	
	@InjectMocks
	TableBookingQueryServiceImpl tableBookingQueryService;

	@Test
	void getFreeSlotAndTable_should_return_list_of_table_slot_available() {

		// Given
		String[] emptySlot1 = new String[] { "11AM-1PM", "table1" };
		String[] emptySlot2 = new String[] { "11AM-1PM", "table2" };
		List<String[]> listByRepository = new ArrayList();
		listByRepository.add(emptySlot1);
		listByRepository.add(emptySlot2);
		Date checkcReservationDate = new Date();
		
		when(tableBookingRepositoryMock.getAllFreeTableAndSlotForDate(anyObject())).thenReturn(listByRepository);
		
		//When
		List<String[]> freeSlotAndTable = tableBookingQueryService.getFreeSlotAndTable(checkcReservationDate);
		
		//Then
		assertTrue(freeSlotAndTable.size() == listByRepository.size());
	}

}
