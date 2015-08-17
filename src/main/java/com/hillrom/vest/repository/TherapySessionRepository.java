package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.service.TherapySession;

public interface TherapySessionRepository extends
		JpaRepository<TherapySession, Long> {

	@Query("from TherapySession tps where tps.patientUser.id =?1 order by tps.date desc")
	public List<TherapySession> findByPatientUserId(Long id);
}
