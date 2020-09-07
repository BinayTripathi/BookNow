package com.binay.booknow.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class TableBooking {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	@NotNull
	private long version;

	@NotNull
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date reservationDate;
	
	@NotNull
	@OneToOne
	private RestaurantTable restaurantTable;
	
	@NotNull
	@OneToOne
	private RestaurantSlot restaurantSlot;
	
	@NotNull
	String personName;
	
	@NotNull
	String contact;
	
	
	
	
}
