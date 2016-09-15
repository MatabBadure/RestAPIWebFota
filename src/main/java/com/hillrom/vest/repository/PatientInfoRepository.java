package com.hillrom.vest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import com.hillrom.vest.domain.PatientInfo;

/**
 * Spring Data JPA repository for the PatientInfo entity.
 */
public interface PatientInfoRepository extends JpaRepository<PatientInfo,String> {
  
	@Query("from PatientInfo where hillromId = ?1")
    Optional<PatientInfo> findOneByHillromId(String hillRomId);
	
	@Query("from PatientInfo where id = ?1")
    PatientInfo findOneById (String id);
	
	@Query("from PatientInfo where bluetoothId = ?1")
	Optional<PatientInfo> findByBluetoothId(String serialNumber);
	
	/**
	 * This returns hillromId of the patient from stored procedure.
	 * @return String hillromId
	 */
	@Procedure(outputParameterName="hillrom_id",procedureName="get_next_patient_hillromid")
	String id();

	@Query("from PatientInfo where serialNumber = ?1")
	Optional<PatientInfo> findOneBySerialNumber(String deviceAddress);
}
