package com.binay.booknow.service.impl;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.persistence.repository.RestaurantSlotRepository;
import com.binay.booknow.persistence.repository.RestaurantTableRepository;
import com.binay.booknow.persistence.repository.TableBookingRepository;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.exception.ReservationNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class TableBookingCommandServiceImplTest {
	
	
	@Mock
	private TableBookingRepository tableBookingRepositoryMock;

	@Mock
	private RestaurantTableRepository restaurantTableRepositoryMock;

	@Mock
	private RestaurantSlotRepository restaurantSlotRepositoryMock;
	
	@InjectMocks
	TableBookingCommandServiceImpl tableBookingCommandServiceImpl;
	
	
	
	CreateReservationRequest createReservationRequest;
	TableBooking tableBooking;
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

		tableBooking = TableBooking.builder().contact(contact).personName(contact)
				.reservationDate(reservationDate).restaurantSlot(restaurantSlot).restaurantTable(restaurantTable).id(id)
				.version(0).build();
		
	}

	@Test
	public void createReservation_should_save_reservation() {
		
		when(tableBookingRepositoryMock.getReservationByDateTableAndSlot(any(), anyString(), anyString())).thenReturn(Optional.empty());
		when(tableBookingRepositoryMock.save(any())).thenReturn(tableBooking);
		Optional<TableBooking> createReservation = tableBookingCommandServiceImpl.createReservation(tableBooking);
		
		assertTrue(createReservation.get().getPersonName().compareTo(tableBooking.getPersonName())==0);
		
	}
	
	@Test
	public void createReservation_should_not_save_booking_if_booking_already_exists() {
		
		when(tableBookingRepositoryMock.getReservationByDateTableAndSlot(any(), anyString(), anyString())).thenReturn(Optional.of(tableBooking));
		
		Optional<TableBooking> createReservation = tableBookingCommandServiceImpl.createReservation(tableBooking);
		
		assertFalse(createReservation.isPresent());
		
	}
	
	
	@Test
	public void updateTableBooking_should_save_table_data() {
		
		when(tableBookingRepositoryMock.save(any())).thenReturn(tableBooking);
		
		TableBooking updateTableBooking = tableBookingCommandServiceImpl.updateTableBooking(tableBooking);
		
		assertTrue(null != updateTableBooking);
		
	}
	
	@Test(expected = ObjectOptimisticLockingFailureException.class)
	public void updateTableBooking_should_should_throw_ObjectOptimisticLockingFailureException_if_update_fails() {
		
		when(tableBookingRepositoryMock.save(any())).thenThrow(ObjectOptimisticLockingFailureException.class);
		
		TableBooking updateTableBooking = tableBookingCommandServiceImpl.updateTableBooking(tableBooking);
		
		//throw exception
		
	}
	
	
	@Test
	public void deleteTableBooking_should_delete_booking_if_id_exixts() {
		
		when(tableBookingRepositoryMock.existsById(anyLong())).thenReturn(true);
		
		boolean updateTableBooking = tableBookingCommandServiceImpl.deleteTableBooking(10L);
		
		assertTrue(updateTableBooking);
		
	}
	
	@Test(expected = ReservationNotFoundException.class)
	public void deleteTableBooking_should_throw_error_if_id_does_not_exists() {
		
		when(tableBookingRepositoryMock.existsById(anyLong())).thenReturn(false);
		
		boolean updateTableBooking = tableBookingCommandServiceImpl.deleteTableBooking(10L);
		
		//throw exception
		
	}

}
