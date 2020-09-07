package com.binay.booknow.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 
 * @author Binay
 * Entity to represent all tables in the resturant
 *
 */

@Entity
@Data
public class RestaurantTable {

	@Id
	Long id;
	
	@NotNull
	String tableName;
	
	//@ManyToMany(fetch=FetchType.EAGER)
	@ManyToMany(cascade = {
		    CascadeType.PERSIST,
		    CascadeType.MERGE
		})
	 @JoinTable(name="TABLE_SLOT",
     joinColumns= {@JoinColumn(name="RESTAURANT_TABLE_ID", referencedColumnName = "ID")},
     inverseJoinColumns= {@JoinColumn(name="RESTAURANT_SLOT_ID" , referencedColumnName = "ID")})
	private List<RestaurantSlot> restaurantSlots = new ArrayList<>();
}
