package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.Notification;

public interface NotificationRepository extends
		JpaRepository<Notification, Long> {
	
	Page<Notification> findByPatientUserIdAndDateBetweenAndIsAcknowledged(Long patientUserId, LocalDate fromDate,LocalDate toDate,boolean isAcknowledged,Pageable pageable);
	
	Notification findByPatientUserIdAndDate(Long patientUserId, LocalDate date);
	
	List<Notification> findByDate(LocalDate date);
	
	List<Notification> findByDateAndIsAcknowledgedAndPatientUserIdIn(LocalDate date,Boolean isAcknowledged,List<Long> patientUserIds);
	
	List<Notification> findByDateAndIsAcknowledgedAndNotificationTypeAndPatientUserIdIn(LocalDate date,Boolean isAcknowledged,String notificationType,List<Long> patientUserIds);
	
	@Query("from Notification n where n.patientUser.id in (?1) and n.date between (?2) and (?3) and n.isAcknowledged = ?4")
	List<Notification> findByPatientUserIdInAndDateBetweenAndIsAcknowledged(List<Long> patientUserId, LocalDate fromDate,LocalDate toDate,boolean isAcknowledged);
}
