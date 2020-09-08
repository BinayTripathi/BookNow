package com.binay.booknow.persistence.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.binay.booknow.persistence.entity.RestaurantSlot;


/**
 * 
 * @author Binay
 * Repository interface for RestaurantSlot
 */

@Repository
public interface RestaurantSlotRepository extends JpaRepository<RestaurantSlot, Long>{
	
	Optional<RestaurantSlot> findBySlot(String slot);
}
