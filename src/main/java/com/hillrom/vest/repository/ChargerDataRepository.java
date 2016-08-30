package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.ChargerData;



/**
 * Spring Data JPA repository for the ChargerData entity.
 */
public interface ChargerDataRepository extends JpaRepository<ChargerData,String> {

	@Query("from ChargerData charger where id = (select max(id) from ChargerData)")
	ChargerData findLatestData();
	

	@Query("Select id, createdTime from ChargerData charger order by id desc")
	Page<ChargerData> findAll(Pageable pageable);
	

	ChargerData findById(Long id);
	
}
