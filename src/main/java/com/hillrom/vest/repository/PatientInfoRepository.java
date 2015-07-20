package com.hillrom.vest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.PatientInfo;

/**
 * Spring Data JPA repository for the PatientInfo entity.
 */
public interface PatientInfoRepository extends JpaRepository<PatientInfo,Long> {
  
    Optional<PatientInfo> findOneByHillromId(String hillRomId);
}
