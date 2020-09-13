package com.binay.booknow.persistence.entity;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * 
 * @author Binay
 * Entity to represent all slots in the resturant
 *
 */
@Cacheable
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = { @Index(name = "slot_index", columnList = "slot", unique = true) })
public class RestaurantSlot {
	
	@Id
	long id;
	
	@NotNull
	private String slot;
	
	@ManyToMany(mappedBy="restaurantSlots", fetch=FetchType.LAZY)
	private Set<RestaurantTable> restaurantTables;// = new ArrayList<>();

}
