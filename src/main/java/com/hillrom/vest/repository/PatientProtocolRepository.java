package com.hillrom.vest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.PatientProtocolData;

public interface PatientProtocolRepository extends
		JpaRepository<PatientProtocolData, Long> {

	@Query("from PatientProtocolData ppd where ppd.patient.id = ?1")
	Optional<PatientProtocolData> findOneByPatientId(String patientId);
}
