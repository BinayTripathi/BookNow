package com.binay.booknow.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.binay.booknow.persistence.entity.TableBooking;
/**
 * 
 * @author Binay
 * Repository interface for RestaurantTable
 */
public interface TableBookingRepository extends JpaRepository<TableBooking, Long>{

}
