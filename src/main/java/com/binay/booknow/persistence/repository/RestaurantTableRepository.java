package com.binay.booknow.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.binay.booknow.persistence.entity.RestaurantTable;
import java.lang.String;
import java.util.Optional;

/**
 * 
 * @author Binay
 * Repository interface for TableBooking
 */

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long>{

	Optional<RestaurantTable> findByTableName(String tablename);
}
