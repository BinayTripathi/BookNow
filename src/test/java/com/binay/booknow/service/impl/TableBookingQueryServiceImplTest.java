package com.binay.booknow.service.impl;

import static org.junit.Assert.assertTrue;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.mockito.junit.MockitoJUnitRunner;

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.persistence.repository.RestaurantSlotRepository;
import com.binay.booknow.persistence.repository.RestaurantTableRepository;
import com.binay.booknow.persistence.repository.TableBookingRepository;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.exception.ReservationNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class TableBookingQueryServiceImplTest {

	@Mock
	private TableBookingRepository tableBookingRepositoryMock;

	@Mock
	private RestaurantTableRepository restaurantTableRepositoryMock;

	@Mock
	private RestaurantSlotRepository restaurantSlotRepositoryMock;

	@InjectMocks
	TableBookingQueryServiceImpl tableBookingQueryService;

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

		tableBooked = TableBooking.builder().contact(contact).personName(contact).reservationDate(reservationDate)
				.restaurantSlot(restaurantSlot).restaurantTable(restaurantTable).id(id).version(0).build();
	}

	@Test
	public void getFreeSlotAndTable_should_return_list_of_table_slot_available() {

		// Given
		String[] emptySlot1 = new String[] { "11AM-1PM", "table1" };
		String[] emptySlot2 = new String[] { "11AM-1PM", "table2" };
		List<String[]> listByRepository = new ArrayList();
		listByRepository.add(emptySlot1);
		listByRepository.add(emptySlot2);
		Date checkcReservationDate = new Date();

		when(tableBookingRepositoryMock.getAllFreeTableAndSlotForDate(anyObject())).thenReturn(listByRepository);

		// When
		List<String[]> freeSlotAndTable = tableBookingQueryService.getFreeSlotAndTable(checkcReservationDate);

		// Then
		assertTrue(freeSlotAndTable.size() == listByRepository.size());
	}

	public void getReservationById_should_return_booking_found() {

		// Given
		when(tableBookingRepositoryMock.findById(any())).thenReturn(Optional.of(tableBooked));

		// when
		TableBooking findReservationById = tableBookingQueryService.getReservationById(tableBooked.getId());

		// then
		assertTrue(findReservationById.getId() == tableBooked.getId());

	}

	@Test(expected = ReservationNotFoundException.class)
	public void getReservationById_should_throw_error_if_no_booking_found() {

		// Given
		when(tableBookingRepositoryMock.findById(any())).thenThrow(ReservationNotFoundException.class);

		// when
		TableBooking findById = tableBookingQueryService.getReservationById(tableBooked.getId());

		// then
		// Throw exception

	}

	@Test
	public void getReservationByDate_should_return_reservation_list() {

		// Given
		List<TableBooking> bookingList = new ArrayList<TableBooking>();
		bookingList.add(tableBooked);
		when(tableBookingRepositoryMock.getByReservationDate(any())).thenReturn(Optional.of(bookingList));

		Date randomDate = new Date();

		// When
		Optional<List<TableBooking>> reservationByDate = tableBookingQueryService.getReservationByDate(randomDate);

		// Then
		assertTrue(reservationByDate.get().size() == bookingList.size());

	}

	@Test
	public void getResturantTableByName_should_return_resturant() throws ValidationException {

		when(restaurantTableRepositoryMock.findByTableName(anyString())).thenReturn(Optional.of(restaurantTable));

		RestaurantTable resturantTableByName = tableBookingQueryService.getResturantTableByName("Table");

		assertTrue(resturantTableByName.getTableName() == restaurantTable.getTableName());

	}


}
