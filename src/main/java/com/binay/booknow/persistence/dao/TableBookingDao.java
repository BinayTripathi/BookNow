package com.binay.booknow.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TableBookingDao {
	
	@PersistenceContext
	EntityManager entityManager;
	
	public void getSlotAndTable( ) {
		
		String query = "SELECT * FROM TABLE_SLOT minus select Restaurant_table_id, restaurant_slot_id from table_booking";
		Query createNativeQuery = entityManager.createNativeQuery(query);
		List resultList = createNativeQuery.getResultList();
		System.out.println(resultList);
		
	}

}
