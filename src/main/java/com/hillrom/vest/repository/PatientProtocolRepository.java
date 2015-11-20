package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import com.hillrom.vest.domain.PatientProtocolData;

public interface PatientProtocolRepository extends
		JpaRepository<PatientProtocolData, String> {

	@Query("from PatientProtocolData ppd where ppd.patient.id = ?1 group by ppd.protocolKey")
	List<PatientProtocolData> findByPatientId(String patientId);
	
	@Query("from PatientProtocolData ppd where ppd.protocolKey = ?1")
	List<PatientProtocolData> findByProtocolKey(String protocolKey);
	
	/**
	 * This returns hillromId of the protocol from stored procedure.
	 * @return String hillromId
	 */
	@Procedure(outputParameterName="hillrom_id",procedureName="get_next_protocol_hillromid")
	String id();
	
	List<PatientProtocolData> findByPatientUserIdAndDeleted(Long patientId,boolean deleted);
	
	@Query("from PatientProtocolData ppd where ppd.patient.id = ?1 and ppd.deleted = false group by ppd.protocolKey")
	List<PatientProtocolData> findByPatientIdAndActiveStatus(String patientId);
	
	List<PatientProtocolData> findByDeletedAndPatientUserIdIn(boolean deleted,List<Long> patientUserId);
}
