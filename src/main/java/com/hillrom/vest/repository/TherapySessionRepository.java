package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.TherapySession;

public interface TherapySessionRepository extends
		JpaRepository<TherapySession, Long> {

	public TherapySession findTop1ByPatientUserIdOrderByEndTimeDesc(Long id);

	@Query("from TherapySession tps where tps.patientUser.id =?1 and tps.date between ?2 and ?3")
	public List<TherapySession> findByPatientUserIdAndDateRange(Long id, LocalDate fromTimestamp,
			LocalDate toTimestamp);

	@Query("from TherapySession tps where tps.patientUser.id =?1 and tps.date = ?2")
	public List<TherapySession> findByPatientUserIdAndDate(Long id,
			LocalDate dateTime);
	
	public TherapySession findTop1ByPatientUserIdOrderByDateAsc(Long id);
	
	public List<TherapySession> findTop1ByPatientUserIdOrderByEndTimeDesc(List<Long> ids);
	
}
