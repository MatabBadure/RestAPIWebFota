package com.hillrom.vest.repository.monarch;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.PatientProtocolDataMonarch;

public interface PatientProtocolMonarchRepository extends
		JpaRepository<PatientProtocolDataMonarch, String> {

	@Query("from PatientProtocolDataMonarch ppd where ppd.patient.id = ?1 group by ppd.protocolKey")
	List<PatientProtocolDataMonarch> findByPatientId(String patientId);
	
	@Query("from PatientProtocolDataMonarch ppd where ppd.protocolKey = ?1")
	List<PatientProtocolDataMonarch> findByProtocolKey(String protocolKey);
	
	/**
	 * This returns hillromId of the protocol from stored procedure.
	 * @return String hillromId
	 */
	@Procedure(outputParameterName="hillrom_id",procedureName="get_next_protocol_hillromid") // SP to be created
	String id();
	
	List<PatientProtocolDataMonarch> findByPatientUserIdAndDeleted(Long patientId,boolean deleted);
	
	@Query("from PatientProtocolDataMonarch ppd where ppd.patient.id = ?1 and ppd.deleted = false group by ppd.protocolKey")
	List<PatientProtocolDataMonarch> findByPatientIdAndActiveStatus(String patientId);
	
	List<PatientProtocolDataMonarch> findByDeletedAndPatientUserIdIn(boolean deleted,List<Long> patientUserId);
	
	List<PatientProtocolDataMonarch> findByPatientUserIdOrderByCreatedDateAsc(Long patientId);
}
