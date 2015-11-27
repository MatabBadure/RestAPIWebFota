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
	
	@Query(nativeQuery=true,value=" SELECT * FROM ( SELECT * FROM ( SELECT * FROM PATIENT_COMPLIANCE order by user_id asc, date desc) t1 GROUP BY user_id ) as pctable "
			+ " join (SELECT user_id,curdate() FROM PATIENT_VEST_THERAPY_DATA where user_id not in "
			+ " (SELECT user_id FROM PATIENT_VEST_THERAPY_DATA where date = curdate()) group by user_id) as pvtd on pctable.user_id = pvtd.user_id "
			+ " union "
			+ " SELECT * FROM ( SELECT * FROM ( SELECT * FROM PATIENT_COMPLIANCE order by user_id asc, date desc) t1 GROUP BY user_id ) as pctable1 "
			+ " join (select user_id,first_transmission_date from PATIENT_NO_EVENT ) as pne "
			+ " on pne.user_id =  pctable1.user_id and pne.first_transmission_date IS NULL"
			)
	List<PatientCompliance> findMissedTherapyPatientsRecords();
	
	List<PatientCompliance> findByDateAndIsSettingsDeviatedAndPatientUserIdIn(LocalDate date,Boolean isSettingsDeviated,List<Long> patientUserIds);
	
	List<PatientCompliance> findByDateAndIsHmrCompliantAndPatientUserIdIn(LocalDate date,Boolean isHmrCompliant,List<Long> patientUserIds);
	
	@Query("from PatientCompliance pc where pc.date = ?1 and pc.missedTherapyCount >= 3  and pc.patientUser.id in ?2 ")
	List<PatientCompliance> findByDateAndMissedtherapyAndPatientUserIdIn(LocalDate date,List<Long> patientUserIds);
	
	List<PatientCompliance> findByDateBetweenAndPatientUserIdIn(LocalDate from,LocalDate to,List<Long> patientUserIds);
	
	List<PatientCompliance> findByPatientUserId(Long patientUserId);
}
