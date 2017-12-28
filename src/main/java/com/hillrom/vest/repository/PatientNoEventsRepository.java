package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.PatientNoEvent;

public interface PatientNoEventsRepository extends
		JpaRepository<PatientNoEvent, Long> {

	List<PatientNoEvent> findByUserCreatedDateBeforeAndPatientUserIdIn(LocalDate date,List<Long> userIds);
	PatientNoEvent findByPatientUserId(Long patientUserId);
	PatientNoEvent findByPatientId(String patientId);
	List<PatientNoEvent> findByPatientUserIdIn(List<Long> userIds);
	
	@Query(nativeQuery=true,value=" SELECT * from PATIENT_NO_EVENT where date(date_first_transmission_date_updated)=:modifiedDate ")
	List<PatientNoEvent> findByModifiedDate(@Param("modifiedDate")String modifiedDate);
}
