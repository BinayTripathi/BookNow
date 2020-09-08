package com.binay.booknow.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 
 * @author Binay
 * Enum representing two hours of daily slot
 *
 */

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DailySlot {	
	
	SLOT_11_AM("11AM-1PM"),
	SLOT_1_PM("1PM-3PM"),
	SLOT_3_PM("3PM-5PM"),
	SLOT_5_PM("5PM-7PM");
		
	private final String slot;	
	
	DailySlot(String slot) {
		this.slot = slot;
	}
	
	 public String getSlot() {
	        return this.slot;
	    }

}
