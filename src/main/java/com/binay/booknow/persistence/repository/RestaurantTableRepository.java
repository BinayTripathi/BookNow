package com.binay.booknow.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.binay.booknow.persistence.entity.TableBooking;


/**
 * 
 * @author Binay
 * Repository interface for TableBooking
 */
public interface RestaurantTableRepository extends JpaRepository<TableBooking, Long>{

}
