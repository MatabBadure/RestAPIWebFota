package com.hillrom.vest.repository.monarch;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientComplianceMonarch;

public interface PatientComplianceMonarchRepository extends
		JpaRepository<PatientComplianceMonarch, Long> {

	PatientComplianceMonarch findTop1ByPatientUserIdOrderByDateDesc(Long patientUserId);
	
	PatientComplianceMonarch findByPatientUserIdAndDate(Long patientUSerId,LocalDate date);
	
	PatientComplianceMonarch findById(Long id);
	
	@Query(nativeQuery=true,value=" SELECT * FROM ( SELECT * FROM ( SELECT * FROM PATIENT_COMPLIANCE_MONARCH order by user_id asc, date desc) "
			+ " t1 GROUP BY user_id ) as pctable join (SELECT user_id,curdate() FROM PATIENT_VEST_THERAPY_DATA_MONARCH where user_id not in "
			+ " (SELECT user_id FROM PATIENT_VEST_THERAPY_DATA_MONARCH where date = curdate()) group by user_id) as pvtd "
			+ " on pctable.user_id = pvtd.user_id "
			+ " union "
			+ " SELECT * FROM ( SELECT * FROM ( SELECT * FROM PATIENT_COMPLIANCE_MONARCH order by user_id asc, date desc) "
			+ " t1 GROUP BY user_id ) as pctable1 join (select user_id,first_transmission_date from PATIENT_NO_EVENT_MONARCH ) as pne "
			+ " on (pne.user_id =  pctable1.user_id and pne.first_transmission_date IS NULL) or "
			+ " (pne.user_id =  pctable1.user_id and pctable1.user_id not in ( SELECT user_id FROM PATIENT_VEST_THERAPY_DATA_MONARCH)) "
			)
	List<PatientComplianceMonarch> findMissedTherapyPatientsRecords();
	
	List<PatientComplianceMonarch> findByDateAndIsSettingsDeviatedAndPatientUserIdIn(LocalDate date,Boolean isSettingsDeviated,List<Long> patientUserIds);
	
	List<PatientComplianceMonarch> findByDateAndIsHmrCompliantAndPatientUserIdIn(LocalDate date,Boolean isHmrCompliant,List<Long> patientUserIds);
	
	@Query("from PatientComplianceMonarch pc where pc.date = ?1 and pc.missedTherapyCount >= 3  and pc.patientUser.id in ?2 ")
	List<PatientComplianceMonarch> findByDateAndMissedtherapyAndPatientUserIdIn(LocalDate date,List<Long> patientUserIds);
	
	List<PatientComplianceMonarch> findByDateBetweenAndPatientUserIdIn(LocalDate from,LocalDate to,List<Long> patientUserIds);
	
	List<PatientComplianceMonarch> findByPatientUserId(Long patientUserId);

	@Query(nativeQuery=true,value=" SELECT id,patient_id,user_id,date,compliance_score,hmr_run_rate,hmr,is_hmr_compliant,"
			+ "is_settings_deviated,missed_therapy_count,last_therapy_session_date,settings_deviated_days_count,"
			+ "global_hmr_non_adherence_count,global_settings_deviated_count,global_missed_therapy_days_count,"
			+ "IF(created_by='','system',created_by) as created_by,"
			+ "IF(created_date='0000-00-00 00:00:00','2016-07-01 23:30:08',created_date) as created_date,"
			+ "last_modified_by,last_modified_date "
			+ "from PATIENT_COMPLIANCE_MONARCH where date >= :adherStDate and user_id = :userId order by date")
	List<PatientComplianceMonarch> returnComplianceForPatientIdDates(
			@Param("adherStDate")String adherStDate, @Param("userId")Long userId);
	
	@Query(nativeQuery=true,value=" SELECT * from PATIENT_COMPLIANCE_MONARCH where date < :adherStDate and user_id = :userId order by date desc limit 1")
	PatientComplianceMonarch returnPrevDayScore(
			@Param("adherStDate")String adherStDate, @Param("userId")Long userId);
	
	@Query("from PatientComplianceMonarch pc where pc.date = ?1 and pc.patientUser.id = ?2")
	PatientComplianceMonarch findByDateAndPatientUserId(LocalDate date,Long patientUserId);
	
	
	  	@Query("from PatientComplianceMonarch pc where (pc.date between ?1 and ?2) and pc.missedTherapyCount >= 3  and pc.patientUser.id in ?3 group by pc.patientUser.id ")
	    List<PatientComplianceMonarch> findByDateBetweenAndMissedtherapyAndPatientUserIdIn(LocalDate from,LocalDate to,List<Long> patientUserIds);
	    
	    @Query("from PatientComplianceMonarch pc where (pc.date between ?1 and ?2) and pc.isHmrCompliant = ?3 and pc.patientUser.id in ?4 group by pc.patientUser.id ")
	    List<PatientComplianceMonarch> findByDateBetweenAndIsHmrCompliantAndPatientUserIdIn(LocalDate from,LocalDate to,Boolean isHmrCompliant,List<Long> patientUserIds);
	    
	    @Query("from PatientComplianceMonarch pc where (pc.date between ?1 and ?2) and pc.isSettingsDeviated = ?3 and pc.patientUser.id in ?4 group by pc.patientUser.id ")
	    List<PatientComplianceMonarch> findByDateBetweenAndIsSettingsDeviatedAndPatientUserIdIn(LocalDate from,LocalDate to,Boolean isSettingsDeviated,List<Long> patientUserIds);
    
}
