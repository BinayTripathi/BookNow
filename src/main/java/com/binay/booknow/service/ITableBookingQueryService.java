package com.binay.booknow.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;

public interface ITableBookingQueryService {
	
	
	/**
	 * Get all free slot and table for a given date
	 * @param reservationDate :  Date on which to check for all available slot and table
	 * @return List of Objects array. 0th entry is the slot and 1st entry is the table name
	 */
	public List<String[]> getFreeSlotAndTable(Date reservationDate);
	
	public TableBooking getReservationById(Long id);	
	
	public Optional<List<TableBooking>> getReservationByDate(Date reservationDate);
	
	public RestaurantTable getResturantTableByName(String tableName) throws ValidationException;
	
	
	public RestaurantSlot getResturantSlotByTime(String reservationTime) throws ValidationException;
	

}
