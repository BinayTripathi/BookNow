package com.binay.booknow.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binay.booknow.persistence.entity.RestaurantSlot;
import com.binay.booknow.persistence.entity.RestaurantTable;
import com.binay.booknow.persistence.entity.TableBooking;
import com.binay.booknow.persistence.repository.RestaurantSlotRepository;
import com.binay.booknow.persistence.repository.RestaurantTableRepository;
import com.binay.booknow.persistence.repository.TableBookingRepository;
import com.binay.booknow.rest.exception.ReservationNotFoundException;
import com.binay.booknow.service.ITableBookingQueryService;


@Service
public class TableBookingQueryServiceImpl implements ITableBookingQueryService{
	
	
	@Autowired
	private TableBookingRepository tableBookingRepository;

	@Autowired
	private RestaurantTableRepository restaurantTableRepository;

	@Autowired
	private RestaurantSlotRepository restaurantSlotRepository;
	
	

	@Override
	public List<String[]> getFreeSlotAndTable(Date reservationDate) {
		
		List<String[]> allFreeTableAndSlotForDate = tableBookingRepository
				.getAllFreeTableAndSlotForDate(reservationDate);
		return allFreeTableAndSlotForDate;
	}

	@Override
	public TableBooking getReservationById(Long id) {
		
		return tableBookingRepository.findById(id)
				.orElseThrow(() -> new ReservationNotFoundException("No reservation found with id :" + id));
	}

	@Override
	public Optional<List<TableBooking>> getReservationByDate(Date reservationDate) {
		
		return tableBookingRepository.getByReservationDate(reservationDate);
		
	}

	
	@Override
	public RestaurantTable getResturantTableByName(String tableName) throws ValidationException {

		RestaurantTable resturantTable = restaurantTableRepository.findByTableName(tableName)
				.orElseThrow(() -> new ValidationException("Incorrect table name provided : " + tableName));

		return resturantTable;
	}

	
	@Override
	public RestaurantSlot getResturantSlotByTime(String reservationTime) throws ValidationException {
		RestaurantSlot slotProvided = restaurantSlotRepository.findBySlot(reservationTime)
				.orElseThrow(() -> new ValidationException("Incorrect reservation time provided : " + reservationTime));

		return slotProvided;
	}

}
