package com.hillrom.vest.repository;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gemfire.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.Notification;

public interface NotificationRepository extends
		JpaRepository<Notification, Long> {
	
	@Query("from Notifications nfs where nfs.patientUser.id = ?1 and nfs.date < ?2 and nfs.isAcknowledged = ?3")
	Page<Notification> findByPatientUserIdAndDateAndIsAcknowledged(Long patientUserId, LocalDate date, boolean isAcknowledged,Pageable pageable);
	
	Notification findByPatientUserIdAndDate(Long patientUserId, LocalDate date);
}
