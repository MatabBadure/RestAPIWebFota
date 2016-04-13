package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.PatientTestResult;

public interface PatientTestResultRepository extends JpaRepository<PatientTestResult, Long> {
	
	@Query("from PatientTestResult patientTestResult where patientTestResult.user.id = ?1")
    List<PatientTestResult> findByUserId(Long userId);
}
