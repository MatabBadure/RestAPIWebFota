package com.hillrom.vest.service.monarch;

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
import com.hillrom.vest.domain.NotificationMonarch;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.monarch.NotificationMonarchRepository;

@Service
@Transactional
public class NotificationMonarchService {

	@Inject
	private NotificationMonarchRepository notificationMonarchRepository;
	
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
	public NotificationMonarch createOrUpdateNotification(User patientUser,
			PatientInfo patient, Long patientUserId,
			LocalDate currentTherapyDate, String notificationType,boolean isAcknowledged) {
		
		List<NotificationMonarch> existingNotificationofTheDayList = notificationMonarchRepository.findAllByPatientUserIdAndDate(patientUserId, currentTherapyDate);
		NotificationMonarch existingNotificationofTheDay = null;
		// Update missed therapy notification if exists for the day
		if(!existingNotificationofTheDayList.isEmpty()){
			int counter = 0;
			for(NotificationMonarch existingNotificationofTheDayUpdate : existingNotificationofTheDayList){
				if(existingNotificationofTheDayList.size() > 1 && counter == 0){
					notificationMonarchRepository.delete(existingNotificationofTheDayUpdate);
				}else{
					existingNotificationofTheDayUpdate.setNotificationType(notificationType);
					existingNotificationofTheDayUpdate.setAcknowledged(isAcknowledged);
					notificationMonarchRepository.save(existingNotificationofTheDayUpdate);
				}
				counter++;
			}
		}else{
			existingNotificationofTheDay = new NotificationMonarch(notificationType,currentTherapyDate,patientUser,patient,false);
			notificationMonarchRepository.save(existingNotificationofTheDay);
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
	public NotificationMonarch createOrUpdateNotification(User patientUser,
			PatientInfo patient, Long patientUserId,
			LocalDate currentTherapyDate, String notificationType,boolean isAcknowledged,NotificationMonarch existingNotificationofTheDay) {
		
		// Commenting the repository call and getting the current date notification from the param 
		//Notification existingNotificationofTheDay = notificationRepository.findByPatientUserIdAndDate(patientUserId, currentTherapyDate);
		
		// Update missed therapy notification if exists for the day
		if(Objects.nonNull(existingNotificationofTheDay)){
			existingNotificationofTheDay.setNotificationType(notificationType);
			existingNotificationofTheDay.setAcknowledged(isAcknowledged);
			notificationMonarchRepository.save(existingNotificationofTheDay);
		}else{
			existingNotificationofTheDay = new NotificationMonarch(notificationType,currentTherapyDate,patientUser,patient,false);
			notificationMonarchRepository.save(existingNotificationofTheDay);
		}
		return existingNotificationofTheDay;
	}
	
	// To get the notification object for the specific notification date
	public NotificationMonarch getNotificationForDay(List<NotificationMonarch> notificationList, LocalDate reqDate) {
		List<NotificationMonarch> notificationFilter = notificationList.stream().filter(Notification->Notification.getDate().equals(reqDate)).collect(Collectors.toList());
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
		List<NotificationMonarch> existingNotificationofTheDayList = notificationMonarchRepository.findAllByPatientUserIdAndDate(patientUserId, currentTherapyDate);
		if(!existingNotificationofTheDayList.isEmpty()){
			for(NotificationMonarch existingNotificationofTheDay : existingNotificationofTheDayList){
				String notificationType = existingNotificationofTheDay.getNotificationType();
				if((MISSED_THERAPY.equalsIgnoreCase(notificationType) && missedTherapyCount < adherenceSetting) ||
					(HMR_NON_COMPLIANCE.equalsIgnoreCase(notificationType) && isHmrCompliant) ||
					(SETTINGS_DEVIATION.equalsIgnoreCase(notificationType) && !isSettingsDeviated) ||
					(HMR_AND_SETTINGS_DEVIATION.equalsIgnoreCase(notificationType) && isHmrCompliant && !isSettingsDeviated)){
					notificationMonarchRepository.delete(existingNotificationofTheDay);
				}
			}
		}
	}
	
	public List<NotificationMonarch> findNotificationsByUserIdAndDateRange(Long patientUserId,LocalDate from,LocalDate to){
		List<Long> patientUserIds = new LinkedList<>();
		patientUserIds.add(patientUserId);
		return notificationMonarchRepository.findByDateBetweenAndIsAcknowledgedAndPatientUserIdIn(from, to, false,patientUserIds);
	}
	
	public Map<Long,List<NotificationMonarch>> getNotificationMapByPatientIdsAndDate(List<Long> patientUserIds,LocalDate from,LocalDate to){
		List<NotificationMonarch> notifications = notificationMonarchRepository.findByDateBetweenAndIsAcknowledgedAndPatientUserIdIn(from, to, false,patientUserIds);
		Map<Long,List<NotificationMonarch>> notificationsMap = new HashMap<>();
		for(NotificationMonarch notification: notifications){
			List<NotificationMonarch> notificationsForUserId = notificationsMap.get(notification.getPatientUser().getId());
			if(Objects.isNull(notificationsForUserId)){
				notificationsForUserId = new LinkedList<>();
			}
			notificationsForUserId.add(notification);
			notificationsMap.put(notification.getPatientUser().getId(), notificationsForUserId);
		}
		return notificationsMap;
	}

	public List<NotificationMonarch> getNotificationMapByDateAndNotificationTypeAndPatientId(LocalDate date,String notificationType, Long patientUserId){
		List<NotificationMonarch> notifications = notificationMonarchRepository.findByNotificationTypeAndPatientUserIdIn(notificationType, patientUserId);
		Collections.sort(notifications,new NotificationMonarchComparator());
		Collections.reverse(notifications);
		List<NotificationMonarch> prevNotifications = new LinkedList<NotificationMonarch>();
		for(NotificationMonarch notification : notifications){
			if(notification.getDate().isBefore(date))
			prevNotifications.add(notification);
		}
	

		List<NotificationMonarch> prev3Notifications = new LinkedList<NotificationMonarch>(prevNotifications.subList(0, prevNotifications.size()<3?prevNotifications.size():3));
		
		return prev3Notifications;
	}
	
	
	
	public void saveAll(Collection<NotificationMonarch> notifications){
		notificationMonarchRepository.save(notifications);
	}
	
	public class NotificationMonarchComparator implements Comparator<NotificationMonarch>
	{
	    public int compare(NotificationMonarch n1, NotificationMonarch n2)
	    {
	       return n1.getDate().compareTo(n2.getDate());
	   }
	}
}
