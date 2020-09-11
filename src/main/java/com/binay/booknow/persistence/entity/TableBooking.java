package com.binay.booknow.persistence.entity;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.binay.booknow.ApplicationConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Cacheable
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = { @Index(name = "table_booking_index", columnList = "reservationDate,restaurant_table_id, restaurant_slot_id", unique = false)})
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
	@DateTimeFormat(pattern = ApplicationConstants.ACCEPTED_DATE_FORMAT)
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
