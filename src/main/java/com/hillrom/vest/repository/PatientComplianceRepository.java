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
	
	@Query(nativeQuery=true,value="SELECT t1.* FROM PATIENT_COMPLIANCE t1 "
			+ " INNER JOIN (SELECT user_id, MAX(date) as maxDate "
			+ " FROM PATIENT_COMPLIANCE "
			+ " GROUP BY user_id) t2 "
			+ " ON t1.user_id=t2.user_id and t1.date = t2.maxDate "
			)
	List<PatientCompliance> findAllGroupByPatientUserIdOrderByDateDesc();
	
	List<PatientCompliance> findByDateAndIsSettingsDeviatedAndPatientUserIdIn(LocalDate date,Boolean isSettingsDeviated,List<Long> patientUserIds);
	
	List<PatientCompliance> findByDateAndIsHmrCompliantAndPatientUserIdIn(LocalDate date,Boolean isHmrCompliant,List<Long> patientUserIds);
	
	@Query("from PatientCompliance pc where pc.date = ?1 and pc.missedTherapyCount >= 3  and pc.patientUser.id in ?2 ")
	List<PatientCompliance> findByDateAndMissedtherapyAndPatientUserIdIn(LocalDate date,List<Long> patientUserIds);
	
	List<PatientCompliance> findByDateBetweenAndPatientUserIdIn(LocalDate from,LocalDate to,List<Long> patientUserIds);
	
	List<PatientCompliance> findByPatientUserId(Long patientUserId);
}
