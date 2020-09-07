package com.binay.booknow.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.binay.booknow.persistence.entity.RestaurantSlot;


/**
 * 
 * @author Binay
 * Repository interface for RestaurantSlot
 */
public interface RestaurantSlotRepository extends JpaRepository<RestaurantSlot, Long>{

}
