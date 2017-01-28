package com.hillrom.vest.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.AdherenceReset;

public interface AdherenceResetRepository extends
		JpaRepository<AdherenceReset, Long> {

	AdherenceReset findOneById(Long Id);
	
	@Query("from AdherenceReset reset where  reset.patientUser.id = ?1  and reset.createdBy = ?2 and reset.resetDate = ?3")
	Optional<AdherenceReset> findOneByPatientUserIdAndCreatedByAndResetDate(Long patientUserId, Long createdByUserId, DateTime resetDate);


	@Query("from AdherenceReset reset where  reset.patient.id = ?1 ORDER BY reset.resetDate desc")
	List<AdherenceReset> findAllByPatientId(String patientId);

	@Query("from AdherenceReset reset where  reset.patientUser.id = ?1 ORDER BY reset.resetDate desc")
	List<AdherenceReset> findAllByPatientUserId(Long userId);
	
	//hill-1956
	 @Query("from AdherenceReset reset where  reset.patientUser.id = ?1  and reset.resetStartDate = ?2")
	 List<AdherenceReset> findOneByPatientUserIdAndResetStartDate(Long patientUserId, LocalDate resetStartDate);
	
	 @Query("from AdherenceReset reset where  reset.patientUser.id = ?1  and reset.resetStartDate between  ?2 and  ?3 ORDER BY reset.resetStartDate asc")
	 List<AdherenceReset> findOneByPatientUserIdAndResetStartDates(Long patientUserId, LocalDate firstStartDate, LocalDate lastStartDate);
	//hill-1956
	
	@Query("from AdherenceReset reset where reset.patientUser.id = ?1 ORDER BY reset.resetStartDate desc")
	List<AdherenceReset> findOneByPatientUserIdLatestResetStartDate(Long patientUserId);
}