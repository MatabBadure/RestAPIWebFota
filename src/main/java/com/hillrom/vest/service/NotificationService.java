package com.hillrom.vest.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_MISSED_THERAPY_DAYS_COUNT;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.data.domain.PageRequest;
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
	 * Creates or Updates Notification with provided details
	 * @param patientUser
	 * @param patient
	 * @param patientUserId
	 * @param currentTherapyDate
	 * @param notificationType
	 * @param isAcknowledged
	 * @param existingNotificationofTheDay
	 * @return
	 */
	public Notification createOrUpdateNotification(User patientUser,
			PatientInfo patient, Long patientUserId,
			LocalDate currentTherapyDate, String notificationType,boolean isAcknowledged,Notification existingNotificationofTheDay) {
		
		// Commenting the repository call and getting the current date notification from the param 
		//Notification existingNotificationofTheDay = notificationRepository.findByPatientUserIdAndDate(patientUserId, currentTherapyDate);
		
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
	
	// To get the notification object for the specific notification date
	public Notification getNotificationForDay(List<Notification> notificationList, LocalDate reqDate) {
		List<Notification> notificationFilter = notificationList.stream().filter(Notification->Notification.getDate().equals(reqDate)).collect(Collectors.toList());
		if(!notificationFilter.isEmpty())
			return notificationFilter.get(0);
		else
			return null;
	}

	/**
	 * Deletes Notification if adherence to protocol or not a missed therapy.
	 * @param patientUserId
	 * @param currentTherapyDate
	 */
	public void deleteNotificationIfExists(Long patientUserId,
			LocalDate currentTherapyDate,int missedTherapyCount,
			boolean isHmrCompliant,boolean isSettingsDeviated, Integer adherenceSetting) {
		List<Notification> existingNotificationofTheDayList = notificationRepository.findAllByPatientUserIdAndDate(patientUserId, currentTherapyDate);
		if(!existingNotificationofTheDayList.isEmpty()){
			for(Notification existingNotificationofTheDay : existingNotificationofTheDayList){
				String notificationType = existingNotificationofTheDay.getNotificationType();
				if((MISSED_THERAPY.equalsIgnoreCase(notificationType) && missedTherapyCount < adherenceSetting) ||
					(HMR_NON_COMPLIANCE.equalsIgnoreCase(notificationType) && isHmrCompliant) ||
					(SETTINGS_DEVIATION.equalsIgnoreCase(notificationType) && !isSettingsDeviated) ||
					(HMR_AND_SETTINGS_DEVIATION.equalsIgnoreCase(notificationType) && isHmrCompliant && !isSettingsDeviated)){
					notificationRepository.delete(existingNotificationofTheDay);
				}
			}
		}
	}
	
	public List<Notification> findNotificationsByUserIdAndDateRange(Long patientUserId,LocalDate from,LocalDate to){
		List<Long> patientUserIds = new LinkedList<>();
		patientUserIds.add(patientUserId);
		return notificationRepository.findByDateBetweenAndIsAcknowledgedAndPatientUserIdIn(from, to, false,patientUserIds);
	}
	
	public Map<Long,List<Notification>> getNotificationMapByPatientIdsAndDate(List<Long> patientUserIds,LocalDate from,LocalDate to){
		List<Notification> notifications = notificationRepository.findByDateBetweenAndIsAcknowledgedAndPatientUserIdIn(from, to, false,patientUserIds);
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

	public List<Notification> getNotificationMapByDateAndNotificationTypeAndPatientId(LocalDate date,String notificationType, Long patientUserId){
		List<Notification> notifications = notificationRepository.findByNotificationTypeAndPatientUserIdIn(notificationType, patientUserId);
		Collections.sort(notifications,new NotificationComparator());
		Collections.reverse(notifications);
		List<Notification> prevNotifications = new LinkedList<Notification>();
		for(Notification notification : notifications){
			if(notification.getDate().isBefore(date))
			prevNotifications.add(notification);
		}
	

		List<Notification> prev3Notifications = new LinkedList<Notification>(prevNotifications.subList(0, prevNotifications.size()<3?prevNotifications.size():3));
		
		return prev3Notifications;
	}
	
	
	
	public void saveAll(Collection<Notification> notifications){
		notificationRepository.save(notifications);
	}
	
	public class NotificationComparator implements Comparator<Notification>
	{
	    public int compare(Notification n1, Notification n2)
	    {
	       return n1.getDate().compareTo(n2.getDate());
	   }
	}
}
