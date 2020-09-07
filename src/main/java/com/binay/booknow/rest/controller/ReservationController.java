package com.binay.booknow.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binay.booknow.persistence.dao.TableBookingDao;

@RestController
public class ReservationController {
	
	@Autowired
	TableBookingDao dao;
	
	
	@GetMapping(path="/getslot")
	public String test() {
		dao.getSlotAndTable();
		return "";
	}

}
