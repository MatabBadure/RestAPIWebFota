package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.Clinic;

/**
 * Spring Data JPA repository for the Clinic entity.
 */
public interface ClinicRepository extends JpaRepository<Clinic,Long> {
	
	static final String QUERY = "select clinic from Clinic clinic where  clinic.name like CONCAT(:queryString, '%') or clinic.address like(:queryString, '%') or clinic.city like CONCAT(:queryString,'%')";
	
	@Query(QUERY)
	List<Clinic> findBy( String queryString); 

    Optional<Clinic> findOneByName(String name);
    
    @Override
    void delete(Clinic t);

    
}
