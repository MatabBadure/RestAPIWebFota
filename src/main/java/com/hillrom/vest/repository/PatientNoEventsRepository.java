package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.PatientNoEvent;

public interface PatientNoEventsRepository extends
		JpaRepository<PatientNoEvent, Long> {

	List<PatientNoEvent> findByUserCreatedDateBeforeAndPatientUserIdIn(LocalDate date,List<Long> userIds);
	PatientNoEvent findByPatientUserId(Long patientUserId);
}
