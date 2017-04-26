package com.hillrom.vest.service.monarch;

import static com.hillrom.vest.config.AdherenceScoreConstants.BONUS_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;
import static com.hillrom.vest.config.AdherenceScoreConstants.HMR_NON_COMPLIANCE_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.MISSED_THERAPY_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.SETTING_DEVIATION_POINTS;
import static com.hillrom.vest.config.NotificationTypeConstants.ADHERENCE_SCORE_RESET;
import static com.hillrom.vest.config.NotificationTypeConstants.ADHERENCE_SCORE_RESET_DISPLAY_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION_MONARCH;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION_VEST;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_MONARCH_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_MONARCH_AND_SETTINGS_DEVIATION_MONARCH;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_MONARCH_AND_SETTINGS_DEVIATION_VEST;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE_DISPLAY_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE_MONARCH_DISPLAY_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE_VEST_DISPLAY_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_VEST_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_VEST_AND_SETTINGS_DEVIATION_MONARCH;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_VEST_AND_SETTINGS_DEVIATION_VEST;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY_DISPLAY_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION_DISPLAY_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION_MONARCH_DISPLAY_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION_VEST_DISPLAY_VALUE;


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
import com.hillrom.vest.audit.service.monarch.PatientProtocolDataAuditMonarchService;
import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.AdherenceReset;
import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.NotificationMonarch;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientComplianceMonarch;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.TherapySessionMonarch;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AdherenceResetRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.ProtocolConstantsRepository;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.repository.monarch.AdherenceResetMonarchRepository;
import com.hillrom.vest.repository.monarch.PatientComplianceMonarchRepository;
import com.hillrom.vest.repository.monarch.TherapySessionMonarchRepository;
import com.hillrom.vest.web.rest.dto.AdherenceTrendVO;
import com.hillrom.vest.web.rest.dto.ProtocolRevisionVO;
import com.hillrom.vest.web.rest.dto.monarch.AdherenceTrendMonarchVO;
import com.hillrom.vest.web.rest.dto.monarch.ProtocolRevisionMonarchVO;
import com.hillrom.vest.web.rest.util.ProtocolDataVOBuilder;

@Service
@Transactional
public class PatientComplianceMonarchService {

	@Inject
	private PatientComplianceMonarchRepository complianceMonarchRepository;

	@Inject
	private NotificationMonarchService notificationMonarchService;
	
	@Inject
	private TherapySessionMonarchRepository therapySessionMonarchRepository;

	@Inject
	@Qualifier("patientProtocolDataAuditMonarchService")
	private PatientProtocolDataAuditMonarchService protocolAuditMonarchService;
	
	
	/**
	 * Creates Or Updates Compliance 
	 * @param compliance
	 * @return
	 */
	public PatientComplianceMonarch createOrUpdate(PatientComplianceMonarch compliance){
		LocalDate date = compliance.getDate();
		Long patientUserId = compliance.getPatientUser().getId();
		PatientComplianceMonarch existingCompliance = complianceMonarchRepository.findByPatientUserIdAndDate(patientUserId, date);
		if(Objects.nonNull(existingCompliance)){
			existingCompliance.setScore(compliance.getScore());
			existingCompliance.setHmrRunRate(compliance.getHmrRunRate());
			existingCompliance.setMissedTherapyCount(compliance.getMissedTherapyCount());
			existingCompliance.setSettingsDeviatedDaysCount(compliance.getSettingsDeviatedDaysCount());
			compliance = complianceMonarchRepository.save(existingCompliance);
		}else{
			complianceMonarchRepository.save(compliance);
		}
		return compliance;
	}
	
	public PatientComplianceMonarch findLatestComplianceByPatientUserId(Long patientUserId){
		return complianceMonarchRepository.findTop1ByPatientUserIdOrderByDateDesc(patientUserId);
	}
	
	public SortedMap<LocalDate,PatientComplianceMonarch> getPatientComplainceMapByPatientUserId(Long patientUserId){
		List<PatientComplianceMonarch> complianceList = complianceMonarchRepository.findByPatientUserId(patientUserId);
		SortedMap<LocalDate,PatientComplianceMonarch> existingComplainceMap = new TreeMap<>(); 
		for(PatientComplianceMonarch compliance : complianceList){
			existingComplainceMap.put(compliance.getDate(),compliance);
		}
		return existingComplainceMap;
	}
	
	public int getMissedTherapyCountByPatientUserId(Long patientUSerId){
		PatientComplianceMonarch existingCompliance = complianceMonarchRepository.findByPatientUserIdAndDate(patientUSerId,LocalDate.now());
		if (Objects.nonNull(existingCompliance))
			return existingCompliance.getMissedTherapyCount();
		else
			return 0;
	}
	
	public void saveAll(Collection<PatientComplianceMonarch> complainces){
		complianceMonarchRepository.save(complainces);
	}
	
	public List<ProtocolRevisionMonarchVO> findAdherenceTrendByUserIdAndDateRange(Long patientUserId,LocalDate from,LocalDate to)
	throws HillromException{

		List<Long> patientUserIds = new LinkedList<>();
		patientUserIds.add(patientUserId);
		TherapySessionMonarch therapySession = therapySessionMonarchRepository.findTop1ByPatientUserIdOrderByEndTimeDesc(patientUserId);
		LocalDate toDate = to;
		LocalDate fromDate = from.minusDays(1);
		if(Objects.isNull(therapySession)){
			toDate = to.minusDays(1);
		} else if(!to.equals(therapySession.getDate())){
			toDate = to.minusDays(1);
		}
		List<PatientComplianceMonarch> complianceList = complianceMonarchRepository.findByDateBetweenAndPatientUserIdIn(fromDate, toDate, patientUserIds);
		if(complianceList.isEmpty()){
			throw new HillromException("No Data Available");
		}
		SortedMap<LocalDate,PatientComplianceMonarch> complianceMap = new TreeMap<>();
		for(PatientComplianceMonarch compliance : complianceList){
			complianceMap.put(compliance.getDate(), compliance);
		}

		List<NotificationMonarch> notifications = notificationMonarchService.findNotificationsByUserIdAndDateRange(patientUserId,fromDate,toDate);
		Map<LocalDate,List<NotificationMonarch>> notificationsMap = notifications.stream().collect(Collectors.groupingBy(NotificationMonarch:: getDate));
		SortedMap<LocalDate,PatientComplianceMonarch> actualMapRequested = complianceMap.tailMap(from);
		
		PatientComplianceMonarch lastCompliance = actualMapRequested.get(actualMapRequested.lastKey());
		DateTime dateTime = Objects.nonNull(lastCompliance.getLastModifiedDate()) ? lastCompliance.getLastModifiedDate() : lastCompliance.getDate().toDateTimeAtStartOfDay();  
		SortedMap<DateTime,ProtocolRevisionMonarchVO> revisionData = protocolAuditMonarchService.findProtocolRevisionsByUserIdTillDate(patientUserId,dateTime);
		// if no revisions found,create a dummy revision with isValid = false
		if(revisionData.isEmpty()){
			ProtocolRevisionMonarchVO revisionVO = new ProtocolRevisionMonarchVO(lastCompliance.getPatientUser().getCreatedDate(),null);
			revisionData.put(lastCompliance.getPatientUser().getCreatedDate().minusSeconds(1), revisionVO);
		}else{
			ProtocolRevisionMonarchVO revisionVO = new ProtocolRevisionMonarchVO(lastCompliance.getPatientUser().getCreatedDate(),revisionData.firstKey());
			revisionData.put(lastCompliance.getPatientUser().getCreatedDate().minusSeconds(1), revisionVO);
		}

		for(LocalDate date: actualMapRequested.keySet()){
			AdherenceTrendMonarchVO trendVO = new AdherenceTrendMonarchVO();
			PatientComplianceMonarch compliance = complianceMap.get(date);
			trendVO.setDate(date);
			trendVO.setUpdatedScore(compliance.getScore());
			//hill-1847(bugfix)
			if(Objects.nonNull(notificationsMap.get(date)) && notificationsMap.get(date).get(0).getNotificationType().equalsIgnoreCase(ADHERENCE_SCORE_RESET))
			{
				trendVO.setScoreReset(true);
			}
			else
			{
				trendVO.setScoreReset(false);
			}
			//hill-1847(bugfix)
			setNotificationPointsMap(complianceMap,notificationsMap,date,trendVO);
			// Get datetime when compliance was processed
			ProtocolRevisionMonarchVO revisionVO = getProtocolRevisionByCompliance(
					revisionData, compliance);
			revisionVO.addAdherenceTrend(trendVO);
		}
		List<ProtocolRevisionMonarchVO> revisions = revisionData.values().stream().filter(rev -> rev.getAdherenceTrends().size() > 0).collect(Collectors.toList());
		System.out.print("Revisions : " + revisions);
		return revisions;
	}

	private ProtocolRevisionMonarchVO getProtocolRevisionByCompliance(
			SortedMap<DateTime, ProtocolRevisionMonarchVO> revisionData,
			PatientComplianceMonarch compliance) {
		DateTime processedTime = Objects.nonNull(compliance.getLastModifiedDate()) ? compliance.getLastModifiedDate() : compliance.getDate().toDateTimeAtStartOfDay().plusHours(23).plusMinutes(59);
		// Get the recent protocol revisions before the processed time of compliance
		SortedMap<DateTime,ProtocolRevisionMonarchVO> recentRevisionMap = revisionData.headMap(processedTime);
		ProtocolRevisionMonarchVO revisionVO = null;
		// if there is revision use the revision else use the default revision
		if(Objects.nonNull(recentRevisionMap) && recentRevisionMap.size() > 0){
			revisionVO = recentRevisionMap.get(recentRevisionMap.lastKey());
		}else{
			revisionVO = revisionData.get(revisionData.firstKey());
		}
		return revisionVO;
	}
	
	private void setNotificationPointsMap(
			SortedMap<LocalDate,PatientComplianceMonarch> complianceMap,
			Map<LocalDate, List<NotificationMonarch>> notificationsMap,
			LocalDate date,
			AdherenceTrendMonarchVO trendVO) throws HillromException {
		int pointsChanged = getChangeInScore(complianceMap, date);
		String notificationType = Objects.isNull(notificationsMap.get(date)) ? "No Notification" : notificationsMap.get(date).get(0).getNotificationType();
		
		try{
			if(Objects.nonNull(notificationsMap.get(date))){
				List<NotificationMonarch> prevNotificationDetails = getPreviousNotificationDetails(date,notificationType,notificationsMap.get(date).get(0).getPatientUser().getId());
				trendVO.setPrevNotificationDetails(prevNotificationDetails);
			}
		}catch(Exception ex){
			throw new HillromException("Error in retriving prevNotificationDetails");
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
		}else if(HMR_AND_SETTINGS_DEVIATION_VEST.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_DISPLAY_VALUE, -HMR_NON_COMPLIANCE_POINTS);
			trendVO.getNotificationPoints().put(SETTINGS_DEVIATION_VEST_DISPLAY_VALUE, -SETTING_DEVIATION_POINTS);
		}else if(HMR_AND_SETTINGS_DEVIATION_MONARCH.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_DISPLAY_VALUE, -HMR_NON_COMPLIANCE_POINTS);
			trendVO.getNotificationPoints().put(SETTINGS_DEVIATION_MONARCH_DISPLAY_VALUE, -SETTING_DEVIATION_POINTS);
		}else if(HMR_VEST_AND_SETTINGS_DEVIATION.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_VEST_DISPLAY_VALUE, -HMR_NON_COMPLIANCE_POINTS);
			trendVO.getNotificationPoints().put(SETTINGS_DEVIATION_DISPLAY_VALUE, -SETTING_DEVIATION_POINTS);
		}else if(HMR_MONARCH_AND_SETTINGS_DEVIATION.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_MONARCH_DISPLAY_VALUE, -HMR_NON_COMPLIANCE_POINTS);
			trendVO.getNotificationPoints().put(SETTINGS_DEVIATION_DISPLAY_VALUE, -SETTING_DEVIATION_POINTS);
		}else if(HMR_MONARCH_AND_SETTINGS_DEVIATION_VEST.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_MONARCH_DISPLAY_VALUE, -HMR_NON_COMPLIANCE_POINTS);
			trendVO.getNotificationPoints().put(SETTINGS_DEVIATION_VEST_DISPLAY_VALUE, -SETTING_DEVIATION_POINTS);
		}else if(HMR_VEST_AND_SETTINGS_DEVIATION_VEST.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_VEST_DISPLAY_VALUE, -HMR_NON_COMPLIANCE_POINTS);
			trendVO.getNotificationPoints().put(SETTINGS_DEVIATION_VEST_DISPLAY_VALUE, -SETTING_DEVIATION_POINTS);
		}else if(HMR_MONARCH_AND_SETTINGS_DEVIATION_MONARCH.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_MONARCH_DISPLAY_VALUE, -HMR_NON_COMPLIANCE_POINTS);
			trendVO.getNotificationPoints().put(SETTINGS_DEVIATION_MONARCH_DISPLAY_VALUE, -SETTING_DEVIATION_POINTS);
		}else if(HMR_VEST_AND_SETTINGS_DEVIATION_MONARCH.equalsIgnoreCase(notificationType)){
			trendVO.getNotificationPoints().put(HMR_NON_COMPLIANCE_VEST_DISPLAY_VALUE, -HMR_NON_COMPLIANCE_POINTS);
			trendVO.getNotificationPoints().put(SETTINGS_DEVIATION_MONARCH_DISPLAY_VALUE, -SETTING_DEVIATION_POINTS);		
		}else{
			trendVO.getNotificationPoints().put(notificationType,pointsChanged);
		}
	}

	private List<NotificationMonarch> getPreviousNotificationDetails(LocalDate date,String notificationType,Long patientUserId) {
		List<NotificationMonarch> prevNotifications = notificationMonarchService.getNotificationMapByDateAndNotificationTypeAndPatientId(date,notificationType,patientUserId);
		return prevNotifications;
	} 
	
	private int getChangeInScore(
			SortedMap<LocalDate, PatientComplianceMonarch> complianceMap,
			LocalDate date) {
		SortedMap<LocalDate,PatientComplianceMonarch> mostRecentComplianceMap = complianceMap.headMap(date);
		int pointsChanged = BONUS_POINTS;
		if(Objects.nonNull(mostRecentComplianceMap) && mostRecentComplianceMap.size() > 0){
			LocalDate lastComplianceDate = mostRecentComplianceMap.lastKey();
			PatientComplianceMonarch previousCompliance = mostRecentComplianceMap.get(lastComplianceDate);
			PatientComplianceMonarch nextCompliance = complianceMap.get(date);
			if(nextCompliance.getScore() - previousCompliance.getScore() == 0)
				pointsChanged = 0;
		}else{
			pointsChanged = 0;
		}
		return pointsChanged;
	} 
	
	public Map<Long,List<PatientComplianceMonarch>> getPatientComplainceMapByPatientUserId(List<Long> patientUserIds,LocalDate from,LocalDate to){
		List<PatientComplianceMonarch> complianceList = complianceMonarchRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<Long,List<PatientComplianceMonarch>> complianceMap = new HashMap<>();
		for(PatientComplianceMonarch compliance: complianceList){
			List<PatientComplianceMonarch> complianceListForUserId = complianceMap.get(compliance.getPatientUser().getId());
			if(Objects.isNull(complianceListForUserId)){
				complianceListForUserId = new LinkedList<>();
			}
			complianceListForUserId.add(compliance);
			complianceMap.put(compliance.getPatientUser().getId(), complianceListForUserId);
		}
		return complianceMap;
	}
	
}
