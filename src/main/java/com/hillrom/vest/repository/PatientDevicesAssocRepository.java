package com.hillrom.vest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.PatientDevicesAssoc;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface PatientDevicesAssocRepository extends JpaRepository<PatientDevicesAssoc, Long> {
	
	@Query("from PatientDevicesAssoc where serialNumber = ?1")
	Optional<PatientDevicesAssoc> findOneBySerialNumber(String deviceAddress);
}
