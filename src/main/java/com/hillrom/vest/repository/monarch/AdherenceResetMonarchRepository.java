package com.hillrom.vest.repository.monarch;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.AdherenceReset;
import com.hillrom.vest.domain.AdherenceResetMonarch;

public interface AdherenceResetMonarchRepository extends
		JpaRepository<AdherenceResetMonarch, Long> {

	AdherenceResetMonarch findOneById(Long Id);
	
	@Query("from AdherenceResetMonarch reset where  reset.patientUser.id = ?1  and reset.createdBy = ?2 and reset.resetDate = ?3")
	Optional<AdherenceResetMonarch> findOneByPatientUserIdAndCreatedByAndResetDate(Long patientUserId, Long createdByUserId, DateTime resetDate);


	@Query("from AdherenceResetMonarch reset where  reset.patient.id = ?1 ORDER BY reset.resetDate desc")
	List<AdherenceResetMonarch> findAllByPatientId(String patientId);

	@Query("from AdherenceResetMonarch reset where  reset.patientUser.id = ?1 ORDER BY reset.resetDate desc")
	List<AdherenceResetMonarch> findAllByPatientUserId(Long userId);
	
	//hill-1956
	 @Query("from AdherenceResetMonarch reset where  reset.patientUser.id = ?1  and reset.resetStartDate = ?2")
	 List<AdherenceResetMonarch> findOneByPatientUserIdAndResetStartDate(Long patientUserId, LocalDate resetStartDate);
	
	 @Query("from AdherenceResetMonarch reset where  reset.patientUser.id = ?1  and reset.resetStartDate between  ?2 and  ?3 ORDER BY reset.resetStartDate asc")
	 List<AdherenceResetMonarch> findOneByPatientUserIdAndResetStartDates(Long patientUserId, LocalDate firstStartDate, LocalDate lastStartDate);
	//hill-1956
	
	@Query("from AdherenceResetMonarch reset where reset.patientUser.id = ?1 ORDER BY reset.resetStartDate desc")
	List<AdherenceResetMonarch> findOneByPatientUserIdLatestResetStartDate(Long patientUserId);
}