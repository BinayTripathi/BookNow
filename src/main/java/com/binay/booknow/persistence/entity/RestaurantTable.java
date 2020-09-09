package com.binay.booknow.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 
 * @author Binay Entity to represent all tables in the resturant
 *
 */
@Cacheable
@Entity
@Data
@Table(indexes = { @Index(name = "table_name_index", columnList = "tableName", unique = true) })
public class RestaurantTable {

	@Id
	Long id;

	@NotNull
	String tableName;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "TABLE_SLOT", joinColumns = {
			@JoinColumn(name = "RESTAURANT_TABLE_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "RESTAURANT_SLOT_ID", referencedColumnName = "ID") })
	private Set<RestaurantSlot> restaurantSlots;// = new ArrayList<>();
}
