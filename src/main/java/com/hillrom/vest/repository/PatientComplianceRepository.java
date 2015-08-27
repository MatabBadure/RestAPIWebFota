package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.PatientCompliance;

public interface PatientComplianceRepository extends
		JpaRepository<PatientCompliance, Long> {

	PatientCompliance findTop1ByPatientUserIdOrderByDateDesc(Long patientUserId);
	
	PatientCompliance findByPatientUserIdAndDate(Long patientUSerId,LocalDate date);
	
	@Query("select NEW com.hillrom.vest.domain.PatientCompliance(pc.score,max(pc.date),pc.patient,pc.patientUser,pc.hmrRunRate) from PatientCompliance pc group by pc.patientUser.id")
	List<PatientCompliance> findAllGroupByPatientUserIdOrderByDateDesc();
}