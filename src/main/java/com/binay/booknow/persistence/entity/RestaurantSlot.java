package com.binay.booknow.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;



/**
 * 
 * @author Binay
 * Entity to represent all slots in the resturant
 *
 */

@Entity
@Data
public class RestaurantSlot {
	
	@Id
	long id;
	
	@NotNull
	private String slot;
	
	@ManyToMany(mappedBy="restaurantSlots", fetch=FetchType.LAZY)
	private Set<RestaurantTable> restaurantTables;// = new ArrayList<>();

}
