package com.binay.booknow.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.rest.dto.CreateReservationRequest;
import com.binay.booknow.rest.dto.CreateReservationResponse;

public interface ITableBookingService {
	
	
	public List<Object[]> getSlotAndTable(Date reservationDate);
	
	public Optional<TableBooking> createReservation(TableBooking tableBooking)
			throws ValidationException;
	
	public TableBooking getReservationById(Long id);
	
	
	public Optional<List<TableBooking>> getReservationByDate(Date reservationDate);
	
	public RestaurantTable getResturantTableByName(String tableName) throws ValidationException;
	
	
	public RestaurantSlot getResturantSlotByTime(String reservationTime) throws ValidationException;
	
	
	public TableBooking updateTableBooking(TableBooking tableBooking, boolean updateTableDateTime);
	
	
	public boolean deleteTableBooking(Long id);
	
	
	
	

}
