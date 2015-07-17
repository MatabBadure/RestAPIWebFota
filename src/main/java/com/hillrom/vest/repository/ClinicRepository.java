package com.hillrom.vest.repository;

import com.hillrom.vest.domain.Clinic;

import org.springframework.data.jpa.repository.*;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Clinic entity.
 */
public interface ClinicRepository extends JpaRepository<Clinic,Long> {

    Optional<Clinic> findOneByName(String name);
    
    @Override
    void delete(Clinic t);

}
