package com.hillrom.vest.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_MISSED_THERAPY_DAYS_COUNT;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;

import java.util.List;
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
	 * Deletes Notification if adherence to protocol or not a missed therapy.
	 * @param patientUserId
	 * @param currentTherapyDate
	 */
	public void deleteNotificationIfExists(Long patientUserId,
			LocalDate currentTherapyDate,int missedTherapyCount,
			boolean isHmrCompliant,boolean isSettingsDeviated) {
		Notification existingNotificationofTheDay = notificationRepository.findByPatientUserIdAndDate(patientUserId, currentTherapyDate);
		if(Objects.nonNull(existingNotificationofTheDay)){
			String notificationType = existingNotificationofTheDay.getNotificationType();
			if((MISSED_THERAPY.equalsIgnoreCase(notificationType) && missedTherapyCount < DEFAULT_MISSED_THERAPY_DAYS_COUNT) ||
				(HMR_NON_COMPLIANCE.equalsIgnoreCase(notificationType) && isHmrCompliant) ||
				(SETTINGS_DEVIATION.equalsIgnoreCase(notificationType) && !isSettingsDeviated) ||
				(HMR_AND_SETTINGS_DEVIATION.equalsIgnoreCase(notificationType) && isHmrCompliant && !isSettingsDeviated)){
				notificationRepository.delete(existingNotificationofTheDay);
			}
		}
	}
	
	public List<Notification> findNotificationsByUserIdAndDateRange(Long patientUserId,LocalDate from,LocalDate to){
		return notificationRepository.findByPatientUserIdAndDateBetweenAndIsAcknowledged(patientUserId, from, to, false);
	} 
}
