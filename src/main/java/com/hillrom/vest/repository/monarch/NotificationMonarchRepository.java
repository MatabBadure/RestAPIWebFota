package com.hillrom.vest.repository.monarch;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.NotificationMonarch;

public interface NotificationMonarchRepository extends
		JpaRepository<NotificationMonarch, Long> {
	
	Page<NotificationMonarch> findByPatientUserIdAndDateBetweenAndIsAcknowledged(Long patientUserId, LocalDate fromDate,LocalDate toDate,boolean isAcknowledged,Pageable pageable);
	
	NotificationMonarch findByPatientUserIdAndDate(Long patientUserId, LocalDate date);
	
	List<NotificationMonarch> findByPatientUserId(Long patientUserId);
	
	List<NotificationMonarch> findByDate(LocalDate date);
	
	List<NotificationMonarch> findByDateAndIsAcknowledgedAndPatientUserIdIn(LocalDate date,Boolean isAcknowledged,List<Long> patientUserIds);
	
	List<NotificationMonarch> findByDateAndIsAcknowledgedAndNotificationTypeAndPatientUserIdIn(LocalDate date,Boolean isAcknowledged,String notificationType,List<Long> patientUserIds);
	
	List<NotificationMonarch> findByDateBetweenAndIsAcknowledgedAndPatientUserIdIn(LocalDate fromDate,LocalDate toDate,boolean isAcknowledged,List<Long> patientUserId);
	

	List<NotificationMonarch> findByNotificationTypeAndPatientUserIdIn(String notificationType,Long patientUserId);
	
	@Query("from NotificationMonarch nf where nf.patientUser.id = ?1 and nf.date = ?2")
	List<NotificationMonarch> findAllByPatientUserIdAndDate(Long patientUserId, LocalDate date);
}
