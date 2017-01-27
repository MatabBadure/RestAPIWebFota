package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.TherapySessionMonarch;

public interface TherapySessionRepositoryMonarch extends
		JpaRepository<TherapySessionMonarch, Long> {

	public TherapySessionMonarch findTop1ByPatientUserIdOrderByEndTimeDesc(Long patientUserId);

	@Query("from TherapySessionMonarch tps where tps.patientUser.id =?1 and tps.date between ?2 and ?3")
	public List<TherapySessionMonarch> findByPatientUserIdAndDateRange(Long patientUserId, LocalDate fromTimestamp,
			LocalDate toTimestamp);

	@Query("from TherapySessionMonarch tps where tps.patientUser.id =?1 and tps.date = ?2")
	public List<TherapySessionMonarch> findByPatientUserIdAndDate(Long patientUserId,
			LocalDate dateTime);
	
	public TherapySessionMonarch findTop1ByPatientUserIdOrderByDateAsc(Long patientUserId);
	
	public List<TherapySessionMonarch> findTop1ByPatientUserIdInOrderByEndTimeDesc(List<Long> patientUserIds);

	public List<TherapySessionMonarch> findByDateBetweenAndPatientUserIdIn(LocalDate fromTimestamp,
			LocalDate toTimestamp,List<Long> patientUserIds);
	
	public List<TherapySessionMonarch> findByDateBetweenAndPatientUserId(LocalDate fromTimestamp,
			LocalDate toTimestamp,Long patientUserId);
	
	public TherapySessionMonarch findTop1ByPatientUserIdAndDateBeforeOrderByEndTimeDesc(Long patientUserId,LocalDate from);

	public List<TherapySessionMonarch> findByPatientUserId(Long patientUserId);
}
