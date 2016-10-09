package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.TherapySession;

public interface TherapySessionRepository extends
		JpaRepository<TherapySession, Long> {

	public TherapySession findTop1ByPatientUserIdOrderByEndTimeDesc(Long patientUserId);

	@Query("from TherapySession tps where tps.patientUser.id =?1 and tps.date between ?2 and ?3")
	public List<TherapySession> findByPatientUserIdAndDateRange(Long patientUserId, LocalDate fromTimestamp,
			LocalDate toTimestamp);

	@Query("from TherapySession tps where tps.patientUser.id =?1 and tps.date = ?2")
	public List<TherapySession> findByPatientUserIdAndDate(Long patientUserId,
			LocalDate dateTime);
	
	public TherapySession findTop1ByPatientUserIdOrderByDateAsc(Long patientUserId);
	
	public List<TherapySession> findTop1ByPatientUserIdInOrderByEndTimeDesc(List<Long> patientUserIds);

	public List<TherapySession> findByDateBetweenAndPatientUserIdIn(LocalDate fromTimestamp,
			LocalDate toTimestamp,List<Long> patientUserIds);
	
	public List<TherapySession> findByDateBetweenAndPatientUserId(LocalDate fromTimestamp,
			LocalDate toTimestamp,Long patientUserId);
	
	public TherapySession findTop1ByPatientUserIdAndDateBeforeOrderByEndTimeDesc(Long patientUserId,LocalDate from);

	public List<TherapySession> findByPatientUserId(Long patientUserId);
}
