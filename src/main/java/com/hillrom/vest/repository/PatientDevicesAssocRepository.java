package com.hillrom.vest.repository;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientDevicesAssoc;


/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface PatientDevicesAssocRepository extends JpaRepository<PatientDevicesAssoc, Long> {
	
	@Query("from PatientDevicesAssoc PDA where PDA.serialNumber = ?1 and isActive=1")
	Optional<PatientDevicesAssoc> findOneBySerialNumber(String deviceAddress);

	@Query(nativeQuery=true,value=" SELECT * from PATIENT_DEVICES_ASSOC where is_active=1 and date(created_date)=:createdDate ")
	List<PatientDevicesAssoc> findByCreatedDate(@Param("createdDate")String createdDate);
	
	@Query("from PatientDevicesAssoc where patientId = ?1 and isActive=1")	 
	List<PatientDevicesAssoc> findByPatientId(String patientId);
	
	@Query("from PatientDevicesAssoc where oldPatientId = ?1 and isActive=1 ")	 
	List<PatientDevicesAssoc> findByOldPatientId(String oldPatientId);
	
}
