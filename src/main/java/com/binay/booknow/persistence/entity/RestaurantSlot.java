package com.binay.booknow.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;



/**
 * 
 * @author Binay
 * Entity to represent all slots in the resturant
 *
 */

@Entity
public class RestaurantSlot {
	
	@Id
	long id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private DailySlot dailySlot;
	
	@ManyToMany(mappedBy="restaurantSlots")
	private List<RestaurantTable> restaurantTables = new ArrayList<>();

}
