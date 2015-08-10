package com.hillrom.vest.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.Clinic;

/**
 * Spring Data JPA repository for the Clinic entity.
 */
public interface ClinicRepository extends JpaRepository<Clinic,Long> , QueryDslPredicateExecutor<Clinic> {
	
	@Query("from Clinic clinic where  "
			+ "LOWER(clinic.name) like LOWER(:queryString) or "
			+ "LOWER(clinic.hillromId) like LOWER(:queryString) or "
			+ "LOWER(clinic.zipcode) like LOWER(:queryString) or "
			+ "LOWER(clinic.state) like LOWER(:queryString) ")
	Page<Clinic> findBy(@Param("queryString") String queryString,Pageable pageable); 

    Optional<Clinic> findOneByName(String name);
    
    @Override
    void delete(Clinic t);

    
}
