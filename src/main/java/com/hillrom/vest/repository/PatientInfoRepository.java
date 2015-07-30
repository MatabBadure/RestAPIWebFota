package com.hillrom.vest.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.PatientInfo;

/**
 * Spring Data JPA repository for the PatientInfo entity.
 */
public interface PatientInfoRepository extends JpaRepository<PatientInfo,Long> {
  
	@Query("from PatientInfo where hillromId = ?1")
    Optional<PatientInfo> findOneByHillromId(String hillRomId);
	
	@Query("Select p from PatientInfo p where  "
			+ " ( LOWER(p.firstName) like LOWER(:queryString) or "
			+ " LOWER(p.lastName) like LOWER(:queryString) or "
			+ " LOWER(p.email) like LOWER(:queryString) or "
			+ " LOWER(p.hillromId) like LOWER(:queryString) )"
			+ " order by firstName,lastName,email ")
	Page<PatientInfo> findBy(@Param("queryString")String searchString,Pageable pageable);
	
	@Query("from PatientInfo where bluetoothId = ?1")
	Optional<PatientInfo> findByBluetoothId(String serialNumber);
}
