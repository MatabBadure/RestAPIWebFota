package com.hillrom.vest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import com.hillrom.vest.domain.PatientProtocolData;

public interface PatientProtocolRepository extends
		JpaRepository<PatientProtocolData, Long> {

	@Query("from PatientProtocolData ppd where ppd.patient.id = ?1")
	Optional<PatientProtocolData> findOneByPatientId(String patientId);
	
	/**
	 * This returns hillromId of the protocol from stored procedure.
	 * @return String hillromId
	 */
	@Procedure(outputParameterName="hillrom_id",procedureName="get_next_protocol_hillromid")
	String id();
}
