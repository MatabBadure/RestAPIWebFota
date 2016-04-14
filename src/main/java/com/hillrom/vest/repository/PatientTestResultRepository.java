package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.PatientTestResult;

public interface PatientTestResultRepository extends JpaRepository<PatientTestResult, Long> {
	
	@Query("from PatientTestResult patientTestResult where patientTestResult.user.id = ?1 and patientTestResult.completionDate between ?2 and ?3")
    List<PatientTestResult> findByUserId(Long userId, LocalDate from, LocalDate to);
	
	List<PatientTestResult> findByCompletionDateBetween(LocalDate from, LocalDate to);
}
