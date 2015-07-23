package com.hillrom.vest.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.Clinic;

/**
 * Spring Data JPA repository for the Clinic entity.
 */
public interface ClinicRepository extends JpaRepository<Clinic,Long> {
	
	@Query("from Clinic clinic where  "
			+ "LOWER(clinic.name) like LOWER(:queryString) or "
			+ "LOWER(clinic.address) like LOWER(:queryString) or "
			+ "clinic.city like LOWER(:queryString) "
			+ "order by clinic.name,clinic.address,clinic.city   ")
	Page<Clinic> findBy(@Param("queryString") String queryString,Pageable pageable); 

    Optional<Clinic> findOneByName(String name);
    
    @Override
    void delete(Clinic t);

    
}
