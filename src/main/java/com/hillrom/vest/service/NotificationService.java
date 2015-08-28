package com.hillrom.vest.service;

import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.NotificationRepository;

@Service
@Transactional
public class NotificationService {

	@Inject
	private NotificationRepository notificationRepository;
	
	/**
	 * Creates or Updates Notification with provided details
	 * @param patientUser
	 * @param patient
	 * @param patientUserId
	 * @param currentTherapyDate
	 * @param notificationType
	 * @param isAcknowledged
	 * @return
	 */
	public Notification createOrUpdateNotification(User patientUser,
			PatientInfo patient, Long patientUserId,
			LocalDate currentTherapyDate, String notificationType,boolean isAcknowledged) {
		Notification existingNotificationofTheDay = notificationRepository.findByPatientUserIdAndDate(patientUserId, currentTherapyDate);
		// Update missed therapy notification if exists for the day
		if(Objects.nonNull(existingNotificationofTheDay)){
			existingNotificationofTheDay.setNotificationType(notificationType);
			existingNotificationofTheDay.setAcknowledged(isAcknowledged);
			notificationRepository.save(existingNotificationofTheDay);
		}else{
			existingNotificationofTheDay = new Notification(notificationType,currentTherapyDate,patientUser,patient,false);
			notificationRepository.save(existingNotificationofTheDay);
		}
		return existingNotificationofTheDay;
	}

	/**
	 * Deletes Missed Therapy Notification if data received on 3rd day.
	 * @param patientUserId
	 * @param currentTherapyDate
	 */
	public void deleteNotification(Long patientUserId,
			LocalDate currentTherapyDate) {
		Notification existingNotificationofTheDay = notificationRepository.findByPatientUserIdAndDate(patientUserId, currentTherapyDate);
		if(Objects.nonNull(existingNotificationofTheDay)){
			notificationRepository.delete(existingNotificationofTheDay);
		}
	}
	
}
