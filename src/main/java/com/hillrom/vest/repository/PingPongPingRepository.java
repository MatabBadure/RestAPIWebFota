package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.PingPongPing;



/**
 * Spring Data JPA repository for the PingPongPing entity.
 */
public interface PingPongPingRepository extends JpaRepository<PingPongPing,String> {

	@Query("from PingPongPing charger where id = (select max(id) from PingPongPing)")
	PingPongPing findLatestPing();
	

	@Query("Select id, createdTime from PingPongPing charger order by id desc")
	Page<PingPongPing> findAll(Pageable pageable);
	

	PingPongPing findById(Long id);
	
}
