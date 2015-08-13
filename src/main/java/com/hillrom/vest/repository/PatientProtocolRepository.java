package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.PatientProtocolDataPK;

public interface PatientProtocolRepository extends
		JpaRepository<PatientProtocolData, PatientProtocolDataPK> {

	@Query("from PatientProtocolData ppd where ppd.patientProtocolDataPK.patient.id = ?1")
	List<PatientProtocolData> findByPatientId(String patientId);
	
	@Query("from PatientProtocolData ppd where ppd.patientProtocolDataPK.patient.id = ?1 and ppd.patientProtocolDataPK.id = ?2")
	Optional<PatientProtocolData> findOneByPatientIdAndProtocolId(String patientId, Long id);
	
	@Query("from PatientProtocolData ppd where ppd.patientProtocolDataPK.patient.id = ?1 and ppd.active = ?2")
	Optional<PatientProtocolData> findOneByPatientIdAndActiveStatus(String patientId, Boolean active);
}
