package com.hillrom.vest.repository;

import com.hillrom.vest.domain.Clinic;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Clinic entity.
 */
public interface ClinicRepository extends JpaRepository<Clinic,Long> {

    @Query("select c from Clinic c left join fetch c.users left join fetch c.patients where c.id =:id")
    Clinic findOneWithEagerRelationships(@Param("id") Long id);
    
    Optional<Clinic> findOneByName(String name);
    
    @Override
    void delete(Clinic t);

}
