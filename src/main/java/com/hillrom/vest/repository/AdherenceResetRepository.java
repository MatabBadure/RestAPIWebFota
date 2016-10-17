package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.AdherenceReset;

public interface AdherenceResetRepository extends
		JpaRepository<AdherenceReset, Long> {

	AdherenceReset findOneById(Long Id);
	
	@Query("from AdherenceReset reset where  reset.patientUser.id = ?1  and reset.createdBy = ?2 and reset.resetDate = ?3")
	Optional<AdherenceReset> findOneByPatientUserIdAndCreatedByAndResetDate(Long patientUserId, Long createdByUserId, LocalDate resetDate);

	//hill-1847
	@Query("from AdherenceReset reset where  reset.patientUser.id = ?1  and reset.resetDate = ?2")
	Optional<AdherenceReset> findOneByPatientUserIdAndResetDate(Long patientUserId, LocalDate resetDate);
	//hill-1847
	
	List<AdherenceReset> findByPatientUserId(Long patientUserId);
}
