package com.hillrom.vest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.PatientInfo;

/**
 * Spring Data JPA repository for the PatientInfo entity.
 */
public interface PatientInfoRepository extends JpaRepository<PatientInfo,Long> {
  
    @Query("from PatientInfo  where hillromId = ?1")
    Optional<PatientInfo> findOneByHillromId( String hillRomId);
}
