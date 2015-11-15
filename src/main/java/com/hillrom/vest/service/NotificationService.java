package com.hillrom.vest.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_MISSED_THERAPY_DAYS_COUNT;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientCompliance;
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
		List<Long> patientUserIds = new LinkedList<>();
		patientUserIds.add(patientUserId);
		return notificationRepository.findByPatientUserIdInAndDateBetweenAndIsAcknowledged(patientUserIds, from, to, false);
	}
	
	public Map<Long,List<Notification>> getNotificationMapByPatientIdsAndDate(List<Long> patientUserIds,LocalDate from,LocalDate to){
		List<Notification> notifications = notificationRepository.findByPatientUserIdInAndDateBetweenAndIsAcknowledged(patientUserIds, from, to, false);
		Map<Long,List<Notification>> notificationsMap = new HashMap<>();
		for(Notification notification: notifications){
			List<Notification> notificationsForUserId = notificationsMap.get(notification.getPatientUser().getId());
			if(Objects.isNull(notificationsForUserId)){
				notificationsForUserId = new LinkedList<>();
			}
			notificationsForUserId.add(notification);
			notificationsMap.put(notification.getPatientUser().getId(), notificationsForUserId);
		}
		return notificationsMap;
	}
	
	public void saveAll(Collection<Notification> notifications){
		notificationRepository.save(notifications);
	}
}
