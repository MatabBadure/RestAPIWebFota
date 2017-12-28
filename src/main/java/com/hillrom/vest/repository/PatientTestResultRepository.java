package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.PatientTestResult;
import com.hillrom.vest.web.rest.dto.PatientTestResultVO;

public interface PatientTestResultRepository extends JpaRepository<PatientTestResult, Long> {
	
	@Query("from PatientTestResult patientTestResult where patientTestResult.user.id = ?1 and patientTestResult.testResultDate between ?2 and ?3 order by patientTestResult.testResultDate asc")
    List<PatientTestResult> findByUserIdAndBetweenTestResultDate(Long userId, LocalDate from, LocalDate to);
	
	List<PatientTestResult> findByTestResultDateBetweenOrderByTestResultDateDesc(LocalDate from, LocalDate to);
}
