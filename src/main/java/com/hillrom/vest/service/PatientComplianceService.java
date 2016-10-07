package com.hillrom.vest.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.BONUS_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;
import static com.hillrom.vest.config.AdherenceScoreConstants.HMR_NON_COMPLIANCE_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.MISSED_THERAPY_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.SETTING_DEVIATION_POINTS;
import static com.hillrom.vest.config.NotificationTypeConstants.ADHERENCE_SCORE_RESET;
import static com.hillrom.vest.config.NotificationTypeConstants.ADHERENCE_SCORE_RESET_DISPLAY_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE_DISPLAY_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY_DISPLAY_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION_DISPLAY_VALUE;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hillrom.vest.audit.service.PatientProtocolDataAuditService;
import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.ProtocolConstantsRepository;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.web.rest.dto.AdherenceTrendVO;
import com.hillrom.vest.web.rest.dto.ProtocolRevisionVO;
import com.hillrom.vest.web.rest.util.ProtocolDataVOBuilder;

@Service
@Transactional
public class PatientComplianceService {

	@Inject
	private PatientComplianceRepository complianceRepository;

	@Inject
	private NotificationService notificationService;
	
	@Inject
	private TherapySessionRepository therapySessionRepository;

	@Inject
	@Qualifier("patientProtocolDataAuditService")
	private PatientProtocolDataAuditService protocolAuditService;
	
	/**
	 * Creates Or Updates Compliance 
	 * @param compliance
	 * @return
	 */
	public PatientCompliance createOrUpdate(PatientCompliance compliance){
		LocalDate date = compliance.getDate();
		Long patientUserId = compliance.getPatientUser().getId();
		PatientCompliance existingCompliance = complianceRepository.findByPatientUserIdAndDate(patientUserId, date);
		if(Objects.nonNull(existingCompliance)){
			existingCompliance.setScore(compliance.getScore());
			existingCompliance.setHmrRunRate(compliance.getHmrRunRate());
			existingCompliance.setMissedTherapyCount(compliance.getMissedTherapyCount());
			existingCompliance.setSettingsDeviatedDaysCount(compliance.getSettingsDeviatedDaysCount());
			compliance = complianceRepository.save(existingCompliance);
		}else{
			complianceRepository.save(compliance);
		}
		return compliance;
	}
	
	public PatientCompliance findLatestComplianceByPatientUserId(Long patientUserId){
		return complianceRepository.findTop1ByPatientUserIdOrderByDateDesc(patientUserId);
	}
	
	public SortedMap<LocalDate,PatientCompliance> getPatientComplainceMapByPatientUserId(Long patientUserId){
		List<PatientCompliance> complianceList = complianceRepository.findByPatientUserId(patientUserId);
		SortedMap<LocalDate,PatientCompliance> existingComplainceMap = new TreeMap<>(); 
		for(PatientCompliance compliance : complianceList){
			existingComplainceMap.put(compliance.getDate(),compliance);
		}
		return existingComplainceMap;
	}
	
	public int getMissedTherapyCountByPatientUserId(Long patientUSerId){
		PatientCompliance existingCompliance = complianceRepository.findByPatientUserIdAndDate(patientUSerId,LocalDate.now());
		if (Objects.nonNull(existingCompliance))
			return existingCompliance.getMissedTherapyCount();
		else
			return 0;
	}
	
	public void saveAll(Collection<PatientCompliance> complainces){
		complianceRepository.save(complainces);
	}
	
	public List<ProtocolRevisionVO> findAdherenceTrendByUserIdAndDateRange(Long patientUserId,LocalDate from,LocalDate to)
	throws HillromException{


		
		List<Long> patientUserIds = new LinkedList<>();
		patientUserIds.add(patientUserId);
		TherapySession therapySession = therapySessionRepository.findTop1ByPatientUserIdOrderByEndTimeDesc(patientUserId);
		LocalDate toDate = to;
		LocalDate fromDate = from.minusDays(1);
		if(Objects.isNull(therapySession)){
			toDate = to.minusDays(1);
		} else if(!to.equals(therapySession.getDate())){
			toDate = to.minusDays(1);
		}
		List<PatientCompliance> complianceList = complianceRepository.findByDateBetweenAndPatientUserIdIn(fromDate, toDate, patientUserIds);
		if(complianceList.isEmpty()){
			throw new HillromException("No Data Available");
		}
		SortedMap<LocalDate,PatientCompliance> complianceMap = new TreeMap<>();
		for(PatientCompliance compliance : complianceList){
			complianceMap.put(compliance.getDate(), compliance);
		}

		List<Notification> notifications = notificationService.findNotificationsByUserIdAndDateRange(patientUserId,fromDate,toDate);
		Map<LocalDate,List<Notification>> notificationsMap = notifications.stream().collect(Collectors.groupingBy(Notification:: getDate));
		SortedMap<LocalDate,PatientCompliance> actualMapRequested = complianceMap.tailMap(from);
		
		PatientCompliance lastCompliance = actualMapRequested.get(actualMapRequested.lastKey());
		DateTime dateTime = Objects.nonNull(lastCompliance.getLastModifiedDate()) ? lastCompliance.getLastModifiedDate() : lastCompliance.getDate().toDateTimeAtStartOfDay();  
		SortedMap<DateTime,ProtocolRevisionVO> revisionData = protocolAuditService.findProtocolRevisionsByUserIdTillDate(patientUserId,dateTime);
		// if no revisions found,create a dummy revision with isValid = false
		if(revisionData.isEmpty()){
			ProtocolRevisionVO revisionVO = new ProtocolRevisionVO(lastCompliance.getPatientUser().getCreatedDate(),null);
			revisionData.put(lastCompliance.getPatientUser().getCreatedDate().minusSeconds(1), revisionVO);
		}else{
			ProtocolRevisionVO revisionVO = new ProtocolRevisionVO(lastCompliance.getPatientUser().getCreatedDate(),revisionData.firstKey());
			revisionData.put(lastCompliance.getPatientUser().getCreatedDate().minusSeconds(1), revisionVO);
		}

		for(LocalDate date: actualMapRequested.keySet()){
			AdherenceTrendVO trendVO = new AdherenceTrendVO();
			PatientCompliance compliance = complianceMap.get(date);
			trendVO.setDate(date);
			trendVO.setUpdatedScore(compliance.getScore());
			setNotificationPointsMap(complianceMap,notificationsMap,date,trendVO);
			// Get datetime when compliance was processed
			ProtocolRevisionVO revisionVO = getProtocolRevisionByCompliance(
					revisionData, compliance);
			revisionVO.addAdherenceTrend(trendVO);
		}
		List<ProtocolRevisionVO> revisions = revisionData.values().stream().filter(rev -> rev.getAdherenceTrends().size() > 0).collect(Collectors.toList());
		System.out.print("Revisions : " + revisions);
		return revisions;
	}

	private ProtocolRevisionVO getProtocolRevisionByCompliance(
			SortedMap<DateTime, ProtocolRevisionVO> revisionData,
			PatientCompliance compliance) {
		DateTime processedTime = Objects.nonNull(compliance.getLastModifiedDate()) ? compliance.getLastModifiedDate() : compliance.getDate().toDateTimeAtStartOfDay().plusHours(23).plusMinutes(59);
		// Get the recent protocol revisions before the processed time of compliance
		SortedMap<DateTime,ProtocolRevisionVO> recentRevisionMap = revisionData.headMap(processedTime);
		ProtocolRevisionVO revisionVO = null;
		// if there is revision use the revision else use the default revision
		if(Objects.nonNull(recentRevisionMap) && recentRevisionMap.size() > 0){
			revisionVO = recentRevisionMap.get(recentRevisionMap.lastKey());
		}else{
			revisionVO = revisionData.get(revisionData.firstKey());
		}
		return revisionVO;
	}
	
	private void setNotificationPointsMap(
			SortedMap<LocalDate,PatientCompliance> complianceMap,
			Map<LocalDate, List<Notification>> notificationsMap,
			LocalDate date,
			AdherenceTrendVO trendVO) {
		int pointsChanged = getChangeInScore(complianceMap, date);
		String notificationType = Objects.isNull(notificationsMap.get(date)) ? "No Notification" : notificationsMap.get(date).get(0).getNotificationType();
		try{
			if(Objects.nonNull(notificationsMap.get(date))){
				List<Notification> prevNotificationDetails = getPreviousNotificationDetails(date,notificationType,notificationsMap.get(date).get(0).getPatientUser().getId());
				trendVO.setPrevNotificationDetails(prevNotificationDetails);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

		if(SETTINGS_DEVIATION.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(SETTINGS_DEVIATION_DISPLAY_VALUE, -SETTING_DEVIATION_POINTS);
		}else if(MISSED_THERAPY.equalsIgnoreCase(notificationType)){
			if(pointsChanged > 0)
				trendVO.getNotificationPoints().put(MISSED_THERAPY_DISPLAY_VALUE, -MISSED_THERAPY_POINTS);
			else
				trendVO.getNotificationPoints().put(MISSED_THERAPY_DISPLAY_VALUE, pointsChanged);
		}else if(HMR_NON_COMPLIANCE.equalsIgnoreCase(notificationType)){
			if(pointsChanged > 0)
				trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_DISPLAY_VALUE, -HMR_NON_COMPLIANCE_POINTS);
			else
				trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_DISPLAY_VALUE, pointsChanged);
		}else if(HMR_AND_SETTINGS_DEVIATION.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_DISPLAY_VALUE, -HMR_NON_COMPLIANCE_POINTS);
			trendVO.getNotificationPoints().put(SETTINGS_DEVIATION_DISPLAY_VALUE, -SETTING_DEVIATION_POINTS);
		}else if(ADHERENCE_SCORE_RESET.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(ADHERENCE_SCORE_RESET_DISPLAY_VALUE, complianceMap.get(date).getScore());
		}else{
			trendVO.getNotificationPoints().put(notificationType,pointsChanged);
		}
	}

	private List<Notification> getPreviousNotificationDetails(LocalDate date,String notificationType,Long patientUserId) {
		List<Notification> prevNotifications = notificationService.getNotificationMapByDateAndNotificationTypeAndPatientId(date,notificationType,patientUserId);
		return prevNotifications;
	} 
	
	private int getChangeInScore(
			SortedMap<LocalDate, PatientCompliance> complianceMap,
			LocalDate date) {
		SortedMap<LocalDate,PatientCompliance> mostRecentComplianceMap = complianceMap.headMap(date);
		int pointsChanged = BONUS_POINTS;
		if(Objects.nonNull(mostRecentComplianceMap) && mostRecentComplianceMap.size() > 0){
			LocalDate lastComplianceDate = mostRecentComplianceMap.lastKey();
			PatientCompliance previousCompliance = mostRecentComplianceMap.get(lastComplianceDate);
			PatientCompliance nextCompliance = complianceMap.get(date);
			if(nextCompliance.getScore() - previousCompliance.getScore() == 0)
				pointsChanged = 0;
		}else{
			pointsChanged = 0;
		}
		return pointsChanged;
	} 
	
	public Map<Long,List<PatientCompliance>> getPatientComplainceMapByPatientUserId(List<Long> patientUserIds,LocalDate from,LocalDate to){
		List<PatientCompliance> complianceList = complianceRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<Long,List<PatientCompliance>> complianceMap = new HashMap<>();
		for(PatientCompliance compliance: complianceList){
			List<PatientCompliance> complianceListForUserId = complianceMap.get(compliance.getPatientUser().getId());
			if(Objects.isNull(complianceListForUserId)){
				complianceListForUserId = new LinkedList<>();
			}
			complianceListForUserId.add(compliance);
			complianceMap.put(compliance.getPatientUser().getId(), complianceListForUserId);
		}
		return complianceMap;
	}
	
}
