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
	
	@Query(nativeQuery=true,value="SELECT * FROM PATIENT_COMPLIANCE pc1 JOIN " 
			+" (SELECT max(date) latest_date,patient_id FROM PATIENT_COMPLIANCE WHERE patient_id in "
			+" (SELECT pi.id as pid FROM PATIENT_INFO AS pi LEFT JOIN PATIENT_VEST_THERAPY_DATA pvtd "
			+" ON pi.id = pvtd.patient_id and pvtd.date <> curdate() group by pvtd.patient_id) group by patient_id) pc2"
			+" ON pc1.patient_id = pc2.patient_id and pc1.date = pc2.latest_date"
			+" GROUP BY pc1.patient_id HAVING pc1.date <> current_date() or pc1.date = current_date()"
			)
	List<PatientCompliance> findMissedTherapyPatientsRecords();
	
	List<PatientCompliance> findByDateAndIsSettingsDeviatedAndPatientUserIdIn(LocalDate date,Boolean isSettingsDeviated,List<Long> patientUserIds);
	
	List<PatientCompliance> findByDateAndIsHmrCompliantAndPatientUserIdIn(LocalDate date,Boolean isHmrCompliant,List<Long> patientUserIds);
	
	@Query("from PatientCompliance pc where pc.date = ?1 and pc.missedTherapyCount >= 3  and pc.patientUser.id in ?2 ")
	List<PatientCompliance> findByDateAndMissedtherapyAndPatientUserIdIn(LocalDate date,List<Long> patientUserIds);
	
	List<PatientCompliance> findByDateBetweenAndPatientUserIdIn(LocalDate from,LocalDate to,List<Long> patientUserIds);
	
	List<PatientCompliance> findByPatientUserId(Long patientUserId);
}
