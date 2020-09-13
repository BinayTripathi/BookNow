package com.binay.booknow.persistence.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.binay.booknow.persistence.entity.TableBooking;

/**
 * 
 * @author Binay Repository interface for RestaurantTable
 */
@Repository
public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {
	

	@Query("SELECT t from TableBooking tb join tb.restaurantTable t join tb.restaurantSlot s where tb.reservationDate = :reservDate and t.tableName = :tableName and s.slot= :slot")
	//@Query("SELECT tb.id from TableBooking tb join tb.restaurantTable t join tb.restaurantSlot s where tb.reservationDate = :reservDate and t.tableName = :tableName and s.slot= :slot")
	//@Query(value = "Select count(tb.id) from table_booking tb , Restaurant_slot s, Restaurant_table t where tb.restaurant_slot_id = s.id and tb.restaurant_table_id = t.id", nativeQuery = true)
	Optional<Long> getReservationByDateTableAndSlot(@Param("reservDate") Date reservDate,
			@Param("tableName") String tableName, @Param("slot") String slot);
	
	
	
	@Query("SELECT t.tableName,s.slot FROM RestaurantTable t join  t.restaurantSlots s "
			+ "where not exists "
			+ "(SELECT t1.tableName,s1.slot from TableBooking tb join tb.restaurantTable t1 join tb.restaurantSlot s1 "
			+ "where tb.reservationDate = :onDate  and t.tableName = t1.tableName and s.slot = s1.slot)")
	List<String[]> getAllFreeTableAndSlotForDate(@Param("onDate") Date onDate);
	
	
	Optional<List<TableBooking>> getByReservationDate(Date reservationDate);
	Optional<TableBooking> findById(Long id);

}

/*
 * @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
 * 
 * @Query("SELECT c FROM Customer c WHERE c.orgId = ?1") public List<Customer>
 * fetchCustomersByOrgId(Long orgId);
 */