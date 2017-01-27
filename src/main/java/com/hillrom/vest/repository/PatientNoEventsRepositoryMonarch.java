package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;

public interface PatientNoEventsRepositoryMonarch extends
		JpaRepository<PatientNoEventMonarch, Long> {

	List<PatientNoEventMonarch> findByUserCreatedDateBeforeAndPatientUserIdIn(LocalDate date,List<Long> userIds);
	PatientNoEventMonarch findByPatientUserId(Long patientUserId);
	PatientNoEventMonarch findByPatientId(String patientId);
	List<PatientNoEventMonarch> findByPatientUserIdIn(List<Long> userIds);
}
