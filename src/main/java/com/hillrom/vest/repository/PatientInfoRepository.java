package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.PatientInfo;

/**
 * Spring Data JPA repository for the PatientInfo entity.
 */
public interface PatientInfoRepository extends JpaRepository<PatientInfo,Long> {
  
    @Query("from PatientInfo  where hillromId = ?1 and isDeleted = ?#{0}")
    Optional<PatientInfo> findOneByHillromId( String hillRomId);

    @Query("update PatientInfo set webLoginCreated = 1 where hillromId = ?1 and deleted <> ?#{1}")
    void updateWebLoginCreated( String hillRomId);
}
