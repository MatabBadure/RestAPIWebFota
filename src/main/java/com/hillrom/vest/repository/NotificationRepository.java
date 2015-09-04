package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.Notification;

public interface NotificationRepository extends
		JpaRepository<Notification, Long> {
	
	Page<Notification> findByPatientUserIdAndDateBetweenAndIsAcknowledged(Long patientUserId, LocalDate fromDate,LocalDate toDate,boolean isAcknowledged,Pageable pageable);
	
	Notification findByPatientUserIdAndDate(Long patientUserId, LocalDate date);
	
	List<Notification> findByDate(LocalDate date);
}
