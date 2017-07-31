package com.hillrom.optimus.repository;


import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.optimus.domain.OptimusData;
import com.hillrom.vest.domain.ChargerData;



/**
 * Spring Data JPA repository for the OptimusData entity.
 */
public interface OptimusDataRepository extends JpaRepository<OptimusData,String> {

	@Query("from OptimusData optimus where id = (select max(id) from OptimusData)")
	OptimusData findLatestData();
	

	@Query("Select id, createdTime from OptimusData optimus order by id desc")
	Page<OptimusData> findAll(Pageable pageable);
	

	OptimusData findById(Long id);
	
}
