package com.hillrom.monarch.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.PatientNoEventMonarch;

public interface PatientNoEventsMonarchRepository extends
		JpaRepository<PatientNoEventMonarch, Long> {

	List<PatientNoEventMonarch> findByUserCreatedDateBeforeAndPatientUserIdIn(LocalDate date,List<Long> userIds);
	
	PatientNoEventMonarch findByPatientUserId(Long patientUserId);
	
	PatientNoEventMonarch findByPatientId(String patientId);
	
	
	List<PatientNoEventMonarch> findByPatientUserIdIn(List<Long> userIds);
	
	@Query(nativeQuery=true,value=" SELECT * from PATIENT_NO_EVENT_MONARCH where date(date_first_transmission_date_updated)=:modifiedDate ")
	List<PatientNoEventMonarch> findByModifiedDate(@Param("modifiedDate")String modifiedDate);
	
}
