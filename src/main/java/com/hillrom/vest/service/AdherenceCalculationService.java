package com.hillrom.vest.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.BONUS_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;
import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_MISSED_THERAPY_DAYS_COUNT;
import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_SETTINGS_DEVIATION_COUNT;
import static com.hillrom.vest.config.AdherenceScoreConstants.HMR_NON_COMPLIANCE_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.LOWER_BOUND_VALUE;
import static com.hillrom.vest.config.AdherenceScoreConstants.MISSED_THERAPY_DAYS_COUNT_THRESHOLD;
import static com.hillrom.vest.config.AdherenceScoreConstants.MISSED_THERAPY_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.SETTING_DEVIATION_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.UPPER_BOUND_VALUE;
import static com.hillrom.vest.config.NotificationTypeConstants.ADHERENCE_SCORE_RESET;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;
import static com.hillrom.vest.service.util.DateUtil.getPlusOrMinusTodayLocalDate;
import static com.hillrom.vest.service.util.DateUtil.getDateBeforeSpecificDays;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateCumulativeDuration;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateHMRRunRatePerSession;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateWeightedAvg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.CareGiverStatsNotificationVO;
import com.hillrom.vest.web.rest.dto.ClinicStatsNotificationVO;
import com.hillrom.vest.web.rest.dto.PatientStatsVO;


@Service
@Transactional
public class AdherenceCalculationService {

	private static final String TOTAL_DURATION = "totalDuration";

	private static final String WEIGHTED_AVG_PRESSURE = "weightedAvgPressure";

	private static final String WEIGHTED_AVG_FREQUENCY = "weightedAvgFrequency";

	@Inject
	private PatientProtocolService protocolService;
	
	@Inject
	private TherapySessionRepository therapySessionRepository;
	
	@Inject
	private PatientComplianceRepository patientComplianceRepository;
	
	@Inject
	private NotificationRepository notificationRepository;
	
	@Inject
	private MailService mailService;
	
	@Inject
	private NotificationService notificationService;
	
	@Inject
	private PatientComplianceService complianceService;
	
	@Inject
	private PatientNoEventService noEventService;
	
	@Inject
	private ClinicRepository clinicRepository;

	private final Logger log = LoggerFactory.getLogger(AdherenceCalculationService.class);
	
	/**
	 * Get Protocol Constants by loading Protocol data
	 * @param patientUserId
	 * @return
	 */
	public ProtocolConstants getProtocolByPatientUserId(
			Long patientUserId) throws Exception{
		List<Long> patientUserIds = new LinkedList<>();
		patientUserIds.add(patientUserId);
		Map<Long,ProtocolConstants> userIdProtolConstantsMap = protocolService.getProtocolByPatientUserIds(patientUserIds);
		return userIdProtolConstantsMap.get(patientUserId);
	}

	/**
	 * Checks whether HMR Compliance violated(minHMRReading < actual < maxHMRReading)
	 * @param protocolConstant
	 * @param actualMetrics
	 * @return
	 */
	public boolean isHMRCompliant(ProtocolConstants protocolConstant,
			double actualTotalDuration3Days) {
		// Custom Protocol, Min/Max Duration calculation is done
		int minHMRReading = Objects.nonNull(protocolConstant
				.getMinDuration()) ? protocolConstant.getMinDuration()
				: protocolConstant.getTreatmentsPerDay()
						* protocolConstant.getMinMinutesPerTreatment();
		if(minHMRReading > Math.round(actualTotalDuration3Days/3)){
			return false;
		}
		return true;
	}

	/**
	 * Checks Whether Settings deviated(protocol.minFrequency < actualWeightedAvgFreq)
	 * @param protocolConstant
	 * @param actualMetrics
	 * @return
	 */
	public boolean isSettingsDeviated(ProtocolConstants protocolConstant,
			double weightedAvgFrequency) {
		if((protocolConstant.getMinFrequency()* LOWER_BOUND_VALUE) > weightedAvgFrequency){
			return true;
		}
		return false;
	}

	/**
	 * Calculates Metrics such as weightedAvgFrequency,Pressure,treatmentsPerDay,duration for last 3 days
	 * @param therapySessionsPerDay
	 * @return
	 */
	public Map<String,Double> calculateTherapyMetricsPer3Days(
			List<TherapySession> therapySessionsPerDay) {
		double totalDuration = calculateCumulativeDuration(therapySessionsPerDay);
		double weightedAvgFrequency = 0f;
		double weightedAvgPressure = 0f;
		for(TherapySession therapySession : therapySessionsPerDay){
			int durationInMinutes = therapySession.getDurationInMinutes(); 
			weightedAvgFrequency += calculateWeightedAvg(totalDuration,durationInMinutes,therapySession.getFrequency());
			weightedAvgPressure += calculateWeightedAvg(totalDuration,durationInMinutes,therapySession.getPressure());
		}
		Map<String,Double> actualMetrics = new HashMap<>();
		weightedAvgFrequency = Math.round(weightedAvgFrequency);
		weightedAvgPressure = Math.round(weightedAvgPressure);
		actualMetrics.put(WEIGHTED_AVG_FREQUENCY, weightedAvgFrequency);
		actualMetrics.put(WEIGHTED_AVG_PRESSURE, weightedAvgPressure);
		actualMetrics.put(TOTAL_DURATION, totalDuration);
		return actualMetrics;
	}

	/**
	 * Runs every midnight deducts the compliance score by 5 if therapy hasn't been done for 3 days
	 */
	@Scheduled(cron="0 30 23 * * * ")
	public void processMissedTherapySessions(){
		try{
			LocalDate today = LocalDate.now();
			log.debug("Started calculating missed therapy "+DateTime.now()+","+today);
			List<PatientCompliance> mstPatientComplianceList = patientComplianceRepository.findMissedTherapyPatientsRecords();
			Map<Long,PatientCompliance> mstNotificationMap = new HashMap<>();
			Map<Long,PatientCompliance> hmrNonComplianceMap = new HashMap<>();
			Map<Long,ProtocolConstants> userProtocolConstantsMap = new HashMap<>();
			Map<Long,PatientCompliance> complianceMap = new HashMap<>();
			Map<Long,Notification> notificationMap = new HashMap<>();
			Map<Long,PatientNoEvent> userIdNoEventMap = noEventService.findAllGroupByPatientUserId();
			
			for(PatientCompliance compliance : mstPatientComplianceList){
				Long userId = compliance.getPatientUser().getId();
				PatientNoEvent noEvent = userIdNoEventMap.get(compliance.getPatientUser().getId());
				//global counters
				int globalMissedTherapyCounter = compliance.getGlobalMissedTherapyCounter();
				int globalHMRNonAdherenceCounter = compliance.getGlobalHMRNonAdherenceCounter();
				int globalSettingsDeviationCounter = compliance.getGlobalSettingsDeviationCounter();
				// For No transmission users , compliance shouldn't be updated until transmission happens
				if(Objects.nonNull(noEvent)&& (Objects.isNull(noEvent.getFirstTransmissionDate()))){
					PatientCompliance newCompliance = new PatientCompliance(compliance.getScore(), today,
							compliance.getPatient(), compliance.getPatientUser(),compliance.getHmrRunRate(),true,
							false,0);
					newCompliance.setLatestTherapyDate(null);// since no transmission 
					complianceMap.put(userId, newCompliance);
					// HMR Compliance shouldn't be checked for Patients for initial 2 days of transmission date
				}else if(Objects.nonNull(noEvent)&& (Objects.nonNull(noEvent.getFirstTransmissionDate()) && 
						DateUtil.getDaysCountBetweenLocalDates(noEvent.getFirstTransmissionDate(), today) < 2)){
					// For Transmitted users no notification for first two days
					PatientCompliance newCompliance = new PatientCompliance(today,compliance.getPatient(),compliance.getPatientUser(),
							compliance.getHmrRunRate(),compliance.getMissedTherapyCount()+1,compliance.getLatestTherapyDate(),
							Objects.nonNull(compliance.getHmr())? compliance.getHmr():0.0d);
					newCompliance.setScore(compliance.getScore());
					updateGlobalCounters(++globalMissedTherapyCounter,
							globalHMRNonAdherenceCounter,
							globalSettingsDeviationCounter, newCompliance);
					complianceMap.put(userId, newCompliance);
				}else {
					PatientCompliance newCompliance = new PatientCompliance(
							today,
							compliance.getPatient(),
							compliance.getPatientUser(),
							compliance.getHmrRunRate(),
							compliance.getMissedTherapyCount()+1,
							compliance.getLatestTherapyDate(),
							Objects.nonNull(compliance.getHmr())? compliance.getHmr():0.0d);
					newCompliance.setScore(compliance.getScore());
					newCompliance.setSettingsDeviatedDaysCount(0);
					// increment global missed therapy counter
					updateGlobalCounters(++globalMissedTherapyCounter,
							globalHMRNonAdherenceCounter,
							globalSettingsDeviationCounter, newCompliance);
					log.debug("Compliance before calc "+newCompliance);
					if(newCompliance.getMissedTherapyCount() >= DEFAULT_MISSED_THERAPY_DAYS_COUNT){ // missed Therapy for 3rd day or more than 3 days
						mstNotificationMap.put(compliance.getPatientUser().getId(), newCompliance);
					}else{ // missed therapy for 1 or 2 days , might fall under hmrNonCompliance
						hmrNonComplianceMap.put(compliance.getPatientUser().getId(), newCompliance);
					}
				}
			}
			
			userProtocolConstantsMap = protocolService.getProtocolByPatientUserIds(new LinkedList<>(hmrNonComplianceMap.keySet()));
			
			Map<Long,List<TherapySession>> userIdLast3DaysTherapiesMap = getLast3DaysTherapiesGroupByUserId(new LinkedList<>(hmrNonComplianceMap.keySet()),getPlusOrMinusTodayLocalDate(-2),today);

			calculateHMRComplianceForMST(today, hmrNonComplianceMap,
					userProtocolConstantsMap, complianceMap, notificationMap,
					userIdLast3DaysTherapiesMap);
			
			calculateMissedTherapy(today, mstNotificationMap,
					hmrNonComplianceMap, complianceMap, notificationMap);
			
			updateNotificationsOnMST(today, notificationMap);
			updateComplianceForMST(today, complianceMap);
			log.debug("Started calculating missed therapy "+DateTime.now()+","+today);
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotification("processMissedTherapySessions",writer.toString());
		}
	}
	
	// Resetting the adherence score for the specific user from the adherence reset start date	
	public void adherenceResetForPatient(Long userId, String patientId, LocalDate adherenceStartDate, Integer adherenceScore){
		try{			
			// Adherence Start date in string for query
			String sAdherenceStDate = adherenceStartDate.toString();
			
			// Get the list of rows for the user id from the adherence reset start date 
			List<PatientCompliance> patientComplianceList = patientComplianceRepository.returnComplianceForPatientIdDates(sAdherenceStDate, userId);
			
			// Getting the protocol constants for the user
			ProtocolConstants userProtocolConstant = protocolService.getProtocolForPatientUserId(userId);
			
			for(PatientCompliance compliance : patientComplianceList){
				
				PatientCompliance currentCompliance = patientComplianceRepository.findById(compliance.getId());
				
				// Score remains the adherence score which has been set for first 2 days
				if(DateUtil.getDaysCountBetweenLocalDates(adherenceStartDate, compliance.getDate()) <= 1){	
					currentCompliance.setScore(adherenceScore);
					patientComplianceRepository.save(currentCompliance);					
				}else{
					if(currentCompliance.getMissedTherapyCount() >= DEFAULT_MISSED_THERAPY_DAYS_COUNT){
						// Missed therapy days
						calculateUserMissedTherapy(currentCompliance,currentCompliance.getDate(), userId);
					}else{
						// HMR Non Compliance
						calculateUserHMRComplianceForMST(currentCompliance, userProtocolConstant, currentCompliance.getDate(), userId);
					}
				}
			}		
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
		}
	}

	private void updateGlobalCounters(int globalMissedTherapyCounter,
			int globalHMRNonAdherenceCounter,
			int globalSettingsDeviationCounter, PatientCompliance newCompliance) {
		newCompliance.setGlobalMissedTherapyCounter(globalMissedTherapyCounter);
		newCompliance.setGlobalHMRNonAdherenceCounter(globalHMRNonAdherenceCounter);
		newCompliance.setGlobalSettingsDeviationCounter(globalSettingsDeviationCounter);
	}

	// Create or Update Adherence Score on Missed Therapy day
	private void updateComplianceForMST(LocalDate today,
			Map<Long, PatientCompliance> complianceMap) {
		Map<Long,List<PatientCompliance>> existingCompliances = complianceService.getPatientComplainceMapByPatientUserId(new LinkedList<>(complianceMap.keySet()),today,today);
		if(existingCompliances.isEmpty()){
			complianceService.saveAll(complianceMap.values());
		}else{
			for(Long puId : existingCompliances.keySet()){
				List<PatientCompliance> complianceForDay = existingCompliances.get(puId);
				if(complianceForDay.size() > 0){
					PatientCompliance existingCompliance = complianceForDay.get(0);
					PatientCompliance currentCompliance = complianceMap.get(puId);
					existingCompliance.setScore(currentCompliance.getScore());
					existingCompliance.setHmr(currentCompliance.getHmr());
					existingCompliance.setHmrRunRate(currentCompliance.getHmrRunRate());
					existingCompliance.setHmrCompliant(currentCompliance.isHmrCompliant());
					existingCompliance.setLatestTherapyDate(currentCompliance.getLatestTherapyDate());
					existingCompliance.setMissedTherapyCount(currentCompliance.getMissedTherapyCount());
					updateGlobalCounters(currentCompliance.getGlobalMissedTherapyCounter(), currentCompliance.getGlobalHMRNonAdherenceCounter(),currentCompliance.getGlobalSettingsDeviationCounter(), existingCompliance);
					complianceMap.put(puId, existingCompliance);
				} 
			}
			complianceService.saveAll(complianceMap.values());
		}
	}

	// Create or Update notifications on Missed Therapy
	private void updateNotificationsOnMST(LocalDate today,
			Map<Long, Notification> notificationMap) {
		Map<Long,List<Notification>> existingNotifications = notificationService.getNotificationMapByPatientIdsAndDate(new LinkedList<>(notificationMap.keySet()), today, today);
		if(existingNotifications.isEmpty()){
			notificationService.saveAll(notificationMap.values());
		}else{
			for(Long puId : existingNotifications.keySet()){
				List<Notification> notificationsforDay = existingNotifications.get(puId);
				if(notificationsforDay.size() > 0){
					Notification existingNotification = notificationsforDay.get(0);
					Notification currentNotification = notificationMap.get(puId);
					existingNotification.setNotificationType(currentNotification.getNotificationType());
					notificationMap.put(puId, existingNotification);
				} 
			}
			notificationService.saveAll(notificationMap.values());
		}
	}

	// calculate missed therapies and points
	private void calculateMissedTherapy(LocalDate today,
			Map<Long, PatientCompliance> mstNotificationMap,
			Map<Long, PatientCompliance> hmrNonComplianceMap,
			Map<Long, PatientCompliance> complianceMap,
			Map<Long, Notification> notificationMap) {
		for(Long patientUserId : mstNotificationMap.keySet()){
			PatientCompliance newCompliance = mstNotificationMap.get(patientUserId);
			int score = newCompliance.getScore();
			score = score < MISSED_THERAPY_POINTS ? 0 :  score - MISSED_THERAPY_POINTS ;
			notificationMap.put(patientUserId, new Notification(MISSED_THERAPY,today,newCompliance.getPatientUser(), newCompliance.getPatient(),false));
			newCompliance.setHmrCompliant(false);
			newCompliance.setScore(score);
			newCompliance.setHmrRunRate(0);
			complianceMap.put(patientUserId, newCompliance);
		}
	}

	// calculate HMRCompliance on Missed Therapy Date
	private void calculateHMRComplianceForMST(LocalDate today,
			Map<Long, PatientCompliance> hmrNonComplianceMap,
			Map<Long, ProtocolConstants> userProtocolConstantsMap,
			Map<Long, PatientCompliance> complianceMap,
			Map<Long, Notification> notificationMap,
			Map<Long,List<TherapySession>> userIdLast3DaysTherapiesMap) {
		for(Long patientUserId : hmrNonComplianceMap.keySet()){
			PatientCompliance newCompliance = hmrNonComplianceMap.get(patientUserId);
			int score = newCompliance.getScore();
			List<TherapySession> therapySessions = userIdLast3DaysTherapiesMap.get(patientUserId);
			if(Objects.isNull(therapySessions)){
				therapySessions = new LinkedList<>();
			}
			int hmrRunRate = calculateHMRRunRatePerSession(therapySessions);
			newCompliance.setHmrRunRate(hmrRunRate);
			double durationFor3Days = hmrRunRate*therapySessions.size(); // runrate*totalsessions = total duration
			ProtocolConstants protocolConstant = userProtocolConstantsMap.get(patientUserId);

			if(!isHMRCompliant(protocolConstant, durationFor3Days)){
				score = score < HMR_NON_COMPLIANCE_POINTS ? 0 : score - HMR_NON_COMPLIANCE_POINTS;
				newCompliance.setHmrCompliant(false);
				// increment HMR Non Adherence Counter
				int globalHMRNonAdherenceCounter = newCompliance.getGlobalHMRNonAdherenceCounter();
				newCompliance.setGlobalHMRNonAdherenceCounter(++globalHMRNonAdherenceCounter);
				notificationMap.put(patientUserId, new Notification(HMR_NON_COMPLIANCE,today,newCompliance.getPatientUser(), newCompliance.getPatient(),false));
			}else{
				score = score <=  DEFAULT_COMPLIANCE_SCORE - BONUS_POINTS ? score + BONUS_POINTS : DEFAULT_COMPLIANCE_SCORE;
				newCompliance.setHmrCompliant(true);
			}
			newCompliance.setScore(score);
			// reset settings deviated count and flag on missed therapy
			newCompliance.setSettingsDeviatedDaysCount(0);
			newCompliance.setSettingsDeviated(false);
			complianceMap.put(patientUserId, newCompliance);
		}
	}
	
	// calculate HMRCompliance on Missed Therapy Date for Per UserId
	private void calculateUserHMRComplianceForMST(
			PatientCompliance newCompliance,
			ProtocolConstants userProtocolConstants,
			LocalDate complianceDate,
			Long userId) {
		
		// Getting previous day score
		PatientCompliance prevCompliance = patientComplianceRepository.returnPrevDayScore(complianceDate.toString(),userId);
		int score = prevCompliance.getScore();
		
		// Get earlier third day to finding therapy session
		LocalDate threeDaysEarlyDate = getDateBeforeSpecificDays(complianceDate,3);
		
		// Get therapy session for last 3 days
		List<TherapySession> therapySessions = therapySessionRepository.findByDateBetweenAndPatientUserId(threeDaysEarlyDate, complianceDate, userId);
				
		if(Objects.isNull(therapySessions)){
			therapySessions = new LinkedList<>();
		}
		
		int hmrRunRate = calculateHMRRunRatePerSession(therapySessions);
		double durationFor3Days = hmrRunRate*therapySessions.size(); // runrate*totalsessions = total duration
		
		// validating the last 3 days therapies with respect to the user protocol
		if(!isHMRCompliant(userProtocolConstants, durationFor3Days)){
			score = score < HMR_NON_COMPLIANCE_POINTS ? 0 : score - HMR_NON_COMPLIANCE_POINTS;
		}else{
			score = score <=  DEFAULT_COMPLIANCE_SCORE - BONUS_POINTS ? score + BONUS_POINTS : DEFAULT_COMPLIANCE_SCORE;
		}
		
		// Setting the new score with respect to the compliance deduction
		newCompliance.setScore(score);
		
		// Saving the updated score for the specific date of compliance
		patientComplianceRepository.save(newCompliance);
	}
		
	
	
	// calculate score with respective to missed therapies
	private void calculateUserMissedTherapy(
			PatientCompliance newCompliance,
			LocalDate complianceDate,
			Long userId) {
		
		// Get the previous day compliance score
		PatientCompliance prevCompliance = patientComplianceRepository.returnPrevDayScore(complianceDate.toString(),userId);
		int score = prevCompliance.getScore();
		
		// Calculating the score on basis of missed therapy
		score = score < MISSED_THERAPY_POINTS ? 0 :  score - MISSED_THERAPY_POINTS ;
		
		// Setting the score
		newCompliance.setScore(score);
		
		// Saving the score values for the specific date of compliance
		patientComplianceRepository.save(newCompliance);
	
	}
		
	/**
	 * Get the therapy data between days and user ids
	 * @param patientUserId, from date and to date
	 * @return
	 */
	public List<TherapySession> getLast3DaysTherapiesForUserId(Long patientUserId,LocalDate from,LocalDate to){
		List<TherapySession> therapySessions = therapySessionRepository.findByDateBetweenAndPatientUserId(from, to, patientUserId);
		return therapySessions;
	}	
	
	/**
	 * Calculate HMRRunRate For PatientUsers
	 * @param patientUserIds
	 * @return
	 */
	public Map<Long,List<TherapySession>> getLast3DaysTherapiesGroupByUserId(List<Long> patientUserIds,LocalDate from,LocalDate to){
		Map<Long,List<TherapySession>> patientUserTherapyMap = new HashMap<>();
		List<TherapySession> therapySessions = therapySessionRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<User,List<TherapySession>> therapySessionsPerPatient = therapySessions.stream().collect(Collectors.groupingBy(TherapySession::getPatientUser));
		for(User patientUser : therapySessionsPerPatient.keySet()){
			List<TherapySession> sessions = therapySessionsPerPatient.get(patientUser);
			patientUserTherapyMap.put(patientUser.getId(), sessions);
		}
		return patientUserTherapyMap;
	}
	
	/**
	 * Runs every midnight , sends the notifications to Patient User.
	 */
	@Async
	@Scheduled(cron="0 15 0 * * *")
	public void processPatientNotifications(){
		LocalDate yesterday = LocalDate.now().minusDays(1);
		List<Notification> notifications = notificationRepository.findByDate(yesterday);
		if(notifications.size() > 0){
			List<Long> patientUserIds = new LinkedList<>();
			for(Notification notification : notifications){
				patientUserIds.add(notification.getPatientUser().getId());
			}
			List<PatientCompliance> complianceList = patientComplianceRepository.findByDateBetweenAndPatientUserIdIn(yesterday,
					yesterday,patientUserIds);
			Map<User,Integer> complianceMap = new HashMap<>();
			for(PatientCompliance compliance : complianceList){
				complianceMap.put(compliance.getPatientUser(), compliance.getMissedTherapyCount());
			}
			try{
				notifications.forEach(notification -> {
					User patientUser = notification.getPatientUser();
					if(Objects.nonNull(patientUser.getEmail())){
						// integrated Accepting mail notifications
						String notificationType = notification.getNotificationType();
						int missedTherapyCount = complianceMap.get(patientUser);
						if(isPatientUserAcceptNotification(patientUser,
								notificationType))
							mailService.sendNotificationMailToPatient(patientUser,notificationType,missedTherapyCount);
					}
				});
			}catch(Exception ex){
				StringWriter writer = new StringWriter();
				PrintWriter printWriter = new PrintWriter( writer );
				ex.printStackTrace( printWriter );
				mailService.sendJobFailureNotification("processPatientNotifications",writer.toString());
			}
		}
	}
	
	private boolean isPatientUserAcceptNotification(User patientUser,
			String notificationType) {
		return (patientUser.isNonHMRNotification() && HMR_NON_COMPLIANCE.equalsIgnoreCase(notificationType)) || 
				(patientUser.isSettingDeviationNotification() && SETTINGS_DEVIATION.equalsIgnoreCase(notificationType)) ||
				(patientUser.isMissedTherapyNotification() && MISSED_THERAPY.equalsIgnoreCase(notificationType) ||
				(HMR_AND_SETTINGS_DEVIATION.equalsIgnoreCase(notificationType) && 
						(patientUser.isNonHMRNotification() || patientUser.isSettingDeviationNotification())));
	}

	/**
	 * Runs every midnight , sends the statistics notifications to Clinic Admin and HCP.
	 * @throws HillromException 
	 */
	@Scheduled(cron="0 15 0 * * * ")
	public void processHcpClinicAdminNotifications() throws HillromException{
		try{
			List<ClinicStatsNotificationVO> statsNotificationVOs = getPatientStatsWithHcpAndClinicAdminAssociation();
			Map<BigInteger, User> idUserMap = getIdUserMapFromPatientStats(statsNotificationVOs);
			Map<String, String> clinicIdNameMap = getClinicIdNameMapFromPatientStats(statsNotificationVOs);
			Map<BigInteger, Map<String, Map<String, Integer>>> adminClinicStats = getPatientStatsWithClinicAdminClinicAssociation(statsNotificationVOs);
			Map<BigInteger, Map<String, Map<String, Integer>>> hcpClinicStats = getPatientStatsWithHcpClinicAssociation(statsNotificationVOs);

			Map<User, Map<String, Map<String, Integer>>> hcpClinicStatsMap = getProcessedUserClinicStatsMap(idUserMap,
					clinicIdNameMap, hcpClinicStats);
			Map<User, Map<String, Map<String, Integer>>> adminClinicStatsMap = getProcessedUserClinicStatsMap(idUserMap,
					clinicIdNameMap, adminClinicStats);
			for(User hcpUser : hcpClinicStatsMap.keySet()){
				mailService.sendNotificationMailToHCPAndClinicAdmin(hcpUser, hcpClinicStatsMap.get(hcpUser));
			}
			for(User adminUser : adminClinicStatsMap.keySet()){
				mailService.sendNotificationMailToHCPAndClinicAdmin(adminUser, adminClinicStatsMap.get(adminUser));
			}
			
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotification("processHcpClinicAdminNotifications",writer.toString());
		}
	}
	
	@Scheduled(cron="0 15 0 * * *")
	public void processCareGiverNotifications() throws HillromException{
		try{
			List<CareGiverStatsNotificationVO> statsNotificationVOs = findPatientStatisticsCareGiver();

			Map<String,CareGiverStatsNotificationVO> cgIdNameMap = new HashMap<>();
			
			Map<String,List<PatientStatsVO>> cgIdPatientStatsMap = new HashMap<>();
			
			for(CareGiverStatsNotificationVO statsNotificationVO : statsNotificationVOs){
				cgIdNameMap.put(statsNotificationVO.getCGEmail(), statsNotificationVO);
				BigInteger patientUserId = statsNotificationVO.getPatientUserid();
				String pFirstName = statsNotificationVO.getPatientFirstname();
				String pLastName = statsNotificationVO.getPatientLastname();
				int missedTherapyCount = statsNotificationVO.getMissedTherapyCount();
				boolean isSettingsDeviated = statsNotificationVO.isSettingsDeviated();
				boolean isHMRCompliant = statsNotificationVO.isHMRCompliant();
				
				List<PatientStatsVO> patientStatsList = cgIdPatientStatsMap.get(statsNotificationVO.getCGEmail());
				if(Objects.isNull(patientStatsList))
					patientStatsList = new LinkedList<>();
				patientStatsList.add(new PatientStatsVO(patientUserId, pFirstName, pLastName, missedTherapyCount, isSettingsDeviated, isHMRCompliant));
				cgIdPatientStatsMap.put(statsNotificationVO.getCGEmail(), patientStatsList);
			}
			
			for(String cgEmail : cgIdNameMap.keySet()){
				CareGiverStatsNotificationVO careGiverStatsNotificationVO = cgIdNameMap.get(cgEmail);
				if(careGiverStatsNotificationVO.getIsHcpAcceptHMRNotification()|
						careGiverStatsNotificationVO.getIsHcpAcceptSettingsNotification()|
						careGiverStatsNotificationVO.getIsHcpAcceptTherapyNotification())
					mailService.sendNotificationCareGiver(cgIdNameMap.get(cgEmail), cgIdPatientStatsMap.get(cgEmail));
			}
			
			
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotification("processHcpClinicAdminNotifications",writer.toString());
		}
	}

	private Map<User, Map<String, Map<String, Integer>>> getProcessedUserClinicStatsMap(
			Map<BigInteger, User> idUserMap,
			Map<String, String> clinicIdNameMap,
			Map<BigInteger, Map<String, Map<String, Integer>>> userClinicStats
			) {
		Map<User, Map<String, Map<String, Integer>>> userClinicStatsMap = new HashMap<>();
		for(BigInteger userId: userClinicStats.keySet()){
			User user = idUserMap.get(userId);
			Map<String,Map<String,Integer>> clinicidStats =  userClinicStats.get(userId);
			for(String clinicId : clinicidStats.keySet()){
				String clinicName = clinicIdNameMap.get(clinicId);
				Map<String,Integer> stats = clinicidStats.get(clinicId);
				int missedTherapyPatients = Objects.nonNull(stats.get("patientsWithMissedTherapy"))?
						stats.get("patientsWithMissedTherapy"):0;
				int settingsDeviatedPatients = Objects.nonNull(stats.get("patientsWithSettingDeviation"))?
						stats.get("patientsWithSettingDeviation"):0;
				int hmrNonCompliantPatients = Objects.nonNull(stats.get("patientsWithHmrNonCompliance"))?
						stats.get("patientsWithHmrNonCompliance"):0;
				if(missedTherapyPatients > 0 || settingsDeviatedPatients > 0
						|| hmrNonCompliantPatients > 0){
					Map<String,Map<String,Integer>> clinicNameStatsMap = new HashMap<>();
					clinicNameStatsMap.put(clinicName, stats);
					userClinicStatsMap.put(user, clinicNameStatsMap);
				}
			}
		}
		return userClinicStatsMap;
	}

	private Map<BigInteger, Map<String, Map<String, Integer>>> getPatientStatsWithHcpClinicAssociation(
			List<ClinicStatsNotificationVO> statsNotificationVOs) {
		Map<BigInteger,List<ClinicStatsNotificationVO>> hcpClinicStatsMap = statsNotificationVOs.stream()
				.collect(Collectors.groupingBy(ClinicStatsNotificationVO :: getHcpId));
		Map<BigInteger,Map<String,Map<String,Integer>>> hcpClinicStats = getClinicWiseStatistics(hcpClinicStatsMap,statsNotificationVOs);
		return hcpClinicStats;
	}

	private Map<BigInteger, Map<String, Map<String, Integer>>> getPatientStatsWithClinicAdminClinicAssociation(
			List<ClinicStatsNotificationVO> statsNotificationVOs) {
		List<ClinicStatsNotificationVO> statsNotificationVOsForAdmin = statsNotificationVOs.stream().filter(statsNotificationVO -> 
			Objects.nonNull(statsNotificationVO.getClinicAdminId())
		).collect(Collectors.toList());
		Map<BigInteger,List<ClinicStatsNotificationVO>> adminClinicStatsMap = statsNotificationVOsForAdmin.stream()
				.collect(Collectors.groupingBy(ClinicStatsNotificationVO :: getClinicAdminId));
		Map<BigInteger,Map<String,Map<String,Integer>>> adminClinicStats = getClinicWiseStatistics(adminClinicStatsMap,statsNotificationVOsForAdmin);
		return adminClinicStats;
	}

	private Map<String, String> getClinicIdNameMapFromPatientStats(
			List<ClinicStatsNotificationVO> statsNotificationVOs) {
		Map<String,String> clinicIdNameMap = new HashMap<>();
		for(ClinicStatsNotificationVO statsNotificationVO : statsNotificationVOs){
			clinicIdNameMap.put(statsNotificationVO.getClinicId(), statsNotificationVO.getClinicName());
		}
		return clinicIdNameMap;
	}

	private Map<BigInteger, User> getIdUserMapFromPatientStats(
			List<ClinicStatsNotificationVO> statsNotificationVOs) {
		Map<BigInteger,User> idUserMap = new HashMap<>();
		for(ClinicStatsNotificationVO statsNotificationVO : statsNotificationVOs){
			idUserMap.put(statsNotificationVO.getHcpId(),new User(statsNotificationVO.getHcpFirstname(),
					statsNotificationVO.getHcpLastname(),statsNotificationVO.getHcpEmail(),
					statsNotificationVO.isHcpAcceptTherapyNotification(),statsNotificationVO.isHcpAcceptHMRNotification()
					,statsNotificationVO.isHcpAcceptSettingsNotification()));
			if(Objects.nonNull(statsNotificationVO.getClinicAdminId())){
				idUserMap.put(statsNotificationVO.getClinicAdminId(),new User(statsNotificationVO.getCaFirstname(),
						statsNotificationVO.getCaLastname(),statsNotificationVO.getCaEmail(),
						statsNotificationVO.isCAAcceptTherapyNotification(),statsNotificationVO.isCAAcceptHMRNotification()
						,statsNotificationVO.isCAAcceptSettingsNotification()));
			}
			
		}
		return idUserMap;
	}

	private List<ClinicStatsNotificationVO> getPatientStatsWithHcpAndClinicAdminAssociation() {
		List<Object[]> results =  clinicRepository.findPatientStatisticsClinicForActiveClinics();
		List<ClinicStatsNotificationVO> statsNotificationVOs = new LinkedList<>();
		for(Object[] result : results){
			statsNotificationVOs.add(new ClinicStatsNotificationVO((BigInteger)result[0], (String)result[1], (String)result[2],
					(BigInteger)result[3],(BigInteger)result[4], (String)result[5], (String)result[6],(String)result[7],
					(BigInteger)result[8],(Integer)result[9], (Boolean)result[10],
					(Boolean)result[11], (Boolean)result[12], (Boolean)result[13], (Boolean)result[14],
					(String)result[15],(String)result[16],(Integer)result[17], (Integer)result[18], (Integer)result[19],
					(String)result[20]));
		}
		return statsNotificationVOs;
	}
	private List<CareGiverStatsNotificationVO> findPatientStatisticsCareGiver() {
		List<Object[]> results =  clinicRepository.findPatientStatisticsCareGiver();
		List<CareGiverStatsNotificationVO> statsNotificationVOs = new LinkedList<>();
		for(Object[] result : results){
			statsNotificationVOs.add(new CareGiverStatsNotificationVO((BigInteger)result[0], (String) result[1], (String)result[2],
					(BigInteger)result[3],(String)result[4], (Integer)result[5],
					(Boolean)result[6], (Boolean)result[7], (String)result[8],
					(Boolean)result[9], (Boolean)result[10], (Boolean)result[11]));
		}		
		return statsNotificationVOs;
	}

	private Map<BigInteger,Map<String,Map<String,Integer>>> getClinicWiseStatistics(
			Map<BigInteger,List<ClinicStatsNotificationVO>> userClinicStatsMap,
			List<ClinicStatsNotificationVO> statsNotificationVOs) {
		Map<BigInteger,Map<String,Map<String,Integer>>> userClinicStats = new HashMap<>();
		for(BigInteger userId : userClinicStatsMap.keySet()){
			List<ClinicStatsNotificationVO> clinicStatsNVO = userClinicStatsMap.get(userId);
			Map<String,Map<String,Integer>> clinicWiseStats = userClinicStats.get(userId);
			for(ClinicStatsNotificationVO cNotificationVO : clinicStatsNVO){
				if(Objects.isNull(clinicWiseStats))
					clinicWiseStats = new HashMap<>();
				Map<String,Integer> stats = clinicWiseStats.get(cNotificationVO.getClinicId());
				if(Objects.isNull(stats))
					stats = new HashMap<>();
				int missedTherapyPatients = Objects.isNull(stats
						.get("patientsWithMissedTherapy")) ? 0 : stats
						.get("patientsWithMissedTherapy");
				int settingsDeviatedPatients = Objects.isNull(stats
						.get("patientsWithSettingDeviation")) ? 0 : stats
						.get("patientsWithSettingDeviation");
				int hmrNonCompliantPatients = Objects.isNull(stats
						.get("patientsWithHmrNonCompliance")) ? 0 : stats
						.get("patientsWithHmrNonCompliance");
				if(!cNotificationVO.isHMRCompliant())
					hmrNonCompliantPatients++;
				if(cNotificationVO.isSettingsDeviated())
					settingsDeviatedPatients++;
				if(cNotificationVO.getMissedTherapyCount() >= DEFAULT_MISSED_THERAPY_DAYS_COUNT)
					missedTherapyPatients++;
				
				stats.put("patientsWithMissedTherapy", missedTherapyPatients);
				stats.put("patientsWithSettingDeviation", settingsDeviatedPatients);
				stats.put("patientsWithHmrNonCompliance", hmrNonCompliantPatients);
				clinicWiseStats.put(cNotificationVO.getClinicId(), stats);
			}
			userClinicStats.put(userId, clinicWiseStats);
		}
		return userClinicStats;
	}

	private boolean isUserAcceptMailNotification(User user) {
		return Objects.nonNull(user.getEmail()) && (user.isMissedTherapyNotification() 
				|| user.isNonHMRNotification() || user.isSettingDeviationNotification());
	}

	public void processAdherenceScore(PatientNoEvent patientNoEvent,
			SortedMap<LocalDate,List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate,List<TherapySession>> receivedTherapySessionsMap,
			SortedMap<LocalDate,PatientCompliance> existingComplianceMap,
			ProtocolConstants protocolConstant) throws Exception{
		for(LocalDate currentTherapySessionDate : receivedTherapySessionsMap.keySet()){
			List<TherapySession> receivedTherapySessions = receivedTherapySessionsMap.get(currentTherapySessionDate);
			LocalDate firstTransmittedDate = null;
			LocalDate latestTherapyDate = null;
			PatientInfo patient = null;
			User patientUser = null;
			if(receivedTherapySessions.size() > 0){
				patient = receivedTherapySessions.get(0).getPatientInfo();
				patientUser = receivedTherapySessions.get(0).getPatientUser();
				
				if(Objects.nonNull(patientNoEvent) && Objects.nonNull(patientNoEvent.getFirstTransmissionDate()))
					firstTransmittedDate = patientNoEvent.getFirstTransmissionDate();
				else
					firstTransmittedDate = currentTherapySessionDate;
			}
			
			int totalDuration = calculateCumulativeDuration(receivedTherapySessions);		
			// Existing User First Time Transmission Data OR New User First Time Transmission Data
			if(existingTherapySessionMap.isEmpty()){
				handleFirstTimeTransmit(existingTherapySessionMap,
						receivedTherapySessionsMap, existingComplianceMap,
						protocolConstant, currentTherapySessionDate,
						firstTransmittedDate, patient, patientUser,
						totalDuration);
			}else{ // User Transmitting data in Subsequent requests
				// data is sent in sorted order
				latestTherapyDate = existingTherapySessionMap.lastKey();
				if (Objects.nonNull(firstTransmittedDate) && Objects.nonNull(currentTherapySessionDate)
						&& firstTransmittedDate.isBefore(currentTherapySessionDate)){
					// Data sent in sorted order
					calculateAdherenceScoreForTheDuration(patientUser,patient,firstTransmittedDate,
							currentTherapySessionDate,protocolConstant,existingComplianceMap,
							existingTherapySessionMap,receivedTherapySessionsMap);
				}else{
					// Older data sent
					firstTransmittedDate = currentTherapySessionDate;
					handleFirstTimeTransmit(existingTherapySessionMap,
							receivedTherapySessionsMap, existingComplianceMap,
							protocolConstant, currentTherapySessionDate,
							firstTransmittedDate, patient, patientUser,
							totalDuration);
				}
			}

 		}
		saveOrUpdateComplianceMap(existingComplianceMap);
		saveOrUpdateTherapySessions(receivedTherapySessionsMap);
	}

	private synchronized void saveOrUpdateTherapySessions(
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap) {
		Map<LocalDate, List<TherapySession>> allTherapySessionMap = eleminateDuplicateTherapySessions(receivedTherapySessionsMap);
		
		List<TherapySession> newTherapySessions = new LinkedList<>();
		for(LocalDate date : allTherapySessionMap.keySet()){
			List<TherapySession> sessionsTobeSaved = allTherapySessionMap.get(date);
			newTherapySessions.addAll(sessionsTobeSaved);
		}
		therapySessionRepository.save(newTherapySessions);
	}

	private Map<LocalDate, List<TherapySession>> eleminateDuplicateTherapySessions(
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap) {
		List<List<TherapySession>> therapySessionsList = new LinkedList<>(receivedTherapySessionsMap.values());
		Long patientUserId = therapySessionsList.get(0).get(0).getPatientUser().getId();
		LocalDate from = receivedTherapySessionsMap.firstKey();
		LocalDate to = receivedTherapySessionsMap.lastKey();
		List<TherapySession> existingTherapySessions = therapySessionRepository.findByPatientUserIdAndDateRange(patientUserId, from, to);
		Map<LocalDate,List<TherapySession>> existingTherapySessionMap = existingTherapySessions.stream().collect(Collectors.groupingBy(TherapySession::getDate));
		Map<LocalDate,List<TherapySession>> allTherapySessionMap = new HashMap<>();
		for(LocalDate date : receivedTherapySessionsMap.keySet()){
			List<TherapySession> therapySessionsPerDate = existingTherapySessionMap.get(date);
			if(Objects.nonNull(therapySessionsPerDate)){
				List<TherapySession> receivedTherapySessions = receivedTherapySessionsMap.get(date);
				for(TherapySession existingSession : therapySessionsPerDate){
					Iterator<TherapySession> itr = receivedTherapySessions.iterator();
					while(itr.hasNext()){
						TherapySession receivedSession = itr.next();
						if(existingSession.getDate().equals(receivedSession.getDate()) &&
								existingSession.getStartTime().equals(receivedSession.getStartTime()) &&
								existingSession.getEndTime().equals(receivedSession.getEndTime()) &&
								existingSession.getFrequency().equals(receivedSession.getFrequency()) && 
								existingSession.getPressure().equals(receivedSession.getPressure()) &&
								existingSession.getHmr().equals(receivedSession.getHmr())){
							itr.remove();
						}
					}
				}
				therapySessionsPerDate.addAll(receivedTherapySessionsMap.get(date));
				Collections.sort(therapySessionsPerDate);
				int sessionNo = 0;
				for(TherapySession session : therapySessionsPerDate){
					session.setSessionNo(++sessionNo);
				}
				allTherapySessionMap.put(date, therapySessionsPerDate);
			}else{
				for(LocalDate receivedDate : receivedTherapySessionsMap.keySet()){
					allTherapySessionMap.put(receivedDate, receivedTherapySessionsMap.get(receivedDate));
				}
			}
		}
		return allTherapySessionMap;
	}

	private synchronized void saveOrUpdateComplianceMap(
			SortedMap<LocalDate, PatientCompliance> existingComplianceMap) {
		// Save or update all compliance
		List<PatientCompliance> compliances = new LinkedList<>(existingComplianceMap.values());
		Long patientUserId = compliances.get(0).getPatientUser().getId();
		SortedMap<LocalDate, PatientCompliance>  complainceMapFromDB = complianceService.getPatientComplainceMapByPatientUserId(patientUserId);
		for(LocalDate date: existingComplianceMap.keySet()){
			//	complianceService.createOrUpdate(existingComplianceMap.get(date));
			PatientCompliance existingCompliance = complainceMapFromDB.get(date);
			PatientCompliance newCompliance = existingComplianceMap.get(date);
			if(Objects.nonNull(existingCompliance)){
				newCompliance.setId(existingCompliance.getId());
				existingComplianceMap.put(date,newCompliance);
			}	
		}
		complianceService.saveAll(existingComplianceMap.values());
	}

	private void handleFirstTimeTransmit(
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap,
			SortedMap<LocalDate, PatientCompliance> existingComplianceMap,
			ProtocolConstants protocolConstant,
			LocalDate currentTherapySessionDate,
			LocalDate firstTransmittedDate, PatientInfo patient,
			User patientUser, int totalDuration) throws Exception{
		noEventService.updatePatientFirstTransmittedDate(patientUser.getId(), currentTherapySessionDate);
		PatientCompliance currentCompliance = new PatientCompliance(DEFAULT_COMPLIANCE_SCORE, currentTherapySessionDate,
				patient, patientUser,totalDuration/3,true,false,0d);
		existingComplianceMap.put(currentTherapySessionDate, currentCompliance);
		calculateAdherenceScoreForTheDuration(patientUser,patient,firstTransmittedDate,
				currentTherapySessionDate,protocolConstant,existingComplianceMap,
				existingTherapySessionMap,receivedTherapySessionsMap);
	}

	private void calculateAdherenceScoreForTheDuration(
			User patientUser,
			PatientInfo patient,
			LocalDate firstTransmittedDate,
			LocalDate currentTherapyDate,
			ProtocolConstants protocolConstant,
			SortedMap<LocalDate, PatientCompliance> existingComplianceMap,
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap) throws Exception{
		
		LocalDate latestComplianceDate = existingComplianceMap.lastKey();
		
		List<TherapySession> sessionsTobeSaved = receivedTherapySessionsMap.get(currentTherapyDate);
		// Get the therapy sessions for currentTherapyDate from existing therapies
		List<TherapySession> existingTherapies = existingTherapySessionMap.get(currentTherapyDate);
		// add existing therapies to calculate metrics (HMR Run rate)
		if(Objects.nonNull(existingTherapies)){
			sessionsTobeSaved.addAll(existingTherapies);
		}
		existingTherapySessionMap.put(currentTherapyDate, sessionsTobeSaved);

		List<LocalDate> allDates = new LinkedList<>();
		// Older Data has been sent, hence recalculate compliance till date
		if(currentTherapyDate.isBefore(latestComplianceDate))
			allDates = DateUtil.getAllLocalDatesBetweenDates(currentTherapyDate, latestComplianceDate);
		else // Future Data has been sent 
			allDates = DateUtil.getAllLocalDatesBetweenDates(latestComplianceDate, currentTherapyDate);
		for(LocalDate therapyDate : allDates){
			// First Transmission Date to be updated
			if(firstTransmittedDate.isAfter(therapyDate)){
				noEventService.updatePatientFirstTransmittedDate(patientUser.getId(),therapyDate);
				firstTransmittedDate = therapyDate;
			}
			
			int daysBetween = DateUtil.getDaysCountBetweenLocalDates(firstTransmittedDate, therapyDate);
			List<TherapySession> latest3DaysTherapySessions = prepareTherapySessionsForLast3days(therapyDate,
					existingTherapySessionMap,receivedTherapySessionsMap);
			
			double hmr = getLatestHMR(existingTherapySessionMap, receivedTherapySessionsMap,therapyDate,
					latest3DaysTherapySessions);
			
			int hmrRunrate = calculateHMRRunRatePerSession(latest3DaysTherapySessions);
			
			LocalDate lastTransmissionDate = getLatestTransmissionDate(
					existingTherapySessionMap,receivedTherapySessionsMap, therapyDate);
			int missedTherapyCount = 0;
			if(daysBetween <= 1){ // first transmit
				PatientCompliance compliance = existingComplianceMap.get(therapyDate);
				if(Objects.nonNull(compliance)){
					compliance.setScore(DEFAULT_COMPLIANCE_SCORE);
					compliance.setHmr(hmr);
					compliance.setHmrRunRate(hmrRunrate);
					compliance.setHmrCompliant(true);
					compliance.setSettingsDeviated(false);
					compliance.setMissedTherapyCount(0);
				}else{
					compliance = new PatientCompliance(DEFAULT_COMPLIANCE_SCORE, therapyDate,
							patient, patientUser,hmrRunrate,true,false,missedTherapyCount,lastTransmissionDate,hmr);
				}
				if(daysBetween == 1){ // second day of the transmission
					missedTherapyCount = DateUtil.getDaysCountBetweenLocalDates(lastTransmissionDate, therapyDate);
					if(LocalDate.now().equals(therapyDate)){
						compliance.setMissedTherapyCount(0);
					}else{
						compliance.setMissedTherapyCount(missedTherapyCount);
						// increment global Missed Therapy counter
						compliance.setGlobalMissedTherapyCounter(missedTherapyCount);
					}
					compliance.setLatestTherapyDate(lastTransmissionDate);
				}
				existingComplianceMap.put(therapyDate, compliance);
			}else{
				missedTherapyCount = DateUtil.getDaysCountBetweenLocalDates(lastTransmissionDate, therapyDate);
				PatientCompliance compliance = getLatestCompliance(patientUser, patient,
						existingComplianceMap, therapyDate);
				compliance.setLatestTherapyDate(lastTransmissionDate);
				compliance.setHmr(hmr);
				compliance.setHmrRunRate(hmrRunrate);
				calculateAdherenceScoreForTheDay(compliance, missedTherapyCount,firstTransmittedDate,
						existingComplianceMap,existingTherapySessionMap,
						receivedTherapySessionsMap, protocolConstant);
			}
		}
	}

	private PatientCompliance getLatestCompliance(User patientUser,
			PatientInfo patient,
			SortedMap<LocalDate, PatientCompliance> existingComplianceMap,
			LocalDate therapyDate) throws Exception {
			SortedMap<LocalDate,PatientCompliance> mostRecentComplianceMap = existingComplianceMap.headMap(therapyDate);
			PatientCompliance latestCompliance = null;
			if(mostRecentComplianceMap.size() > 0){
				latestCompliance = mostRecentComplianceMap.get(mostRecentComplianceMap.lastKey());
				return buildPatientCompliance(therapyDate, latestCompliance,latestCompliance.getMissedTherapyCount());
			}else{
				return new PatientCompliance(DEFAULT_COMPLIANCE_SCORE, therapyDate,
						patient, patientUser,0,true,false,0d);
			}
	}

	private LocalDate getLatestTransmissionDate(
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap,
			LocalDate date) throws Exception{
		LocalDate lastTransmissionDate = date;
		// Get Latest TransmissionDate, if data has not been transmitted for the day get mostRecent date
		if(Objects.isNull(existingTherapySessionMap.get(date))){
			SortedMap<LocalDate,List<TherapySession>> mostRecentTherapySessionMap = existingTherapySessionMap.headMap(date);
			if(mostRecentTherapySessionMap.size()>0)
				lastTransmissionDate = mostRecentTherapySessionMap.lastKey();
		}
		return lastTransmissionDate;
	}

	private double getLatestHMR(
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap,
			LocalDate date, List<TherapySession> latest3DaysTherapySessions) throws Exception{
		double hmr = 0;
		if(Objects.nonNull(receivedTherapySessionsMap.get(date))){
			List<TherapySession> currentTherapySessions = receivedTherapySessionsMap.get(date);
			if(Objects.nonNull(currentTherapySessions) && currentTherapySessions.size() > 0)
				hmr = currentTherapySessions.get(currentTherapySessions.size()-1).getHmr();
		}else if(existingTherapySessionMap.size() > 0){
			SortedMap<LocalDate, List<TherapySession>> previousTherapySessionMap = existingTherapySessionMap
					.headMap(date);
			if (previousTherapySessionMap.size() > 0) {
				List<TherapySession> mostRecentTherapySessions = previousTherapySessionMap
						.get(previousTherapySessionMap.lastKey());
				hmr = mostRecentTherapySessions.get(
						mostRecentTherapySessions.size() - 1).getHmr();
			}
		}
		return hmr;
	}

	private PatientCompliance buildPatientCompliance(LocalDate date,
			PatientCompliance latestcompliance,int missedTherapyCount) {
		PatientCompliance compliance = new PatientCompliance();
		compliance.setDate(date);
		compliance.setPatient(latestcompliance.getPatient());
		compliance.setPatientUser(latestcompliance.getPatientUser());
		compliance.setScore(latestcompliance.getScore());
		compliance.setHmr(latestcompliance.getHmr());
		compliance.setHmrRunRate(latestcompliance.getHmrRunRate());
		compliance.setSettingsDeviated(latestcompliance.isSettingsDeviated());
		compliance.setMissedTherapyCount(missedTherapyCount);
		compliance.setHmrCompliant(latestcompliance.isHmrCompliant());
		compliance.setLatestTherapyDate(latestcompliance.getLatestTherapyDate());
		compliance.setSettingsDeviatedDaysCount(latestcompliance.getSettingsDeviatedDaysCount());
		updateGlobalCounters(latestcompliance.getGlobalMissedTherapyCounter(), latestcompliance.getGlobalHMRNonAdherenceCounter(), latestcompliance.getGlobalSettingsDeviationCounter(), compliance);
		return compliance;
	}

	public void calculateAdherenceScoreForTheDay(PatientCompliance latestCompliance,int currentMissedTherapyCount,
			LocalDate firstTransmissionDate,
			SortedMap<LocalDate,PatientCompliance> complianceMap,
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap,
			ProtocolConstants protocolConstant) throws Exception{

		int currentScore = latestCompliance.getScore();
		String notificationType = "";
		User patientUser = latestCompliance.getPatientUser();
		Long patientUserId = patientUser.getId();
		PatientInfo patient = latestCompliance.getPatient();
		LocalDate today =LocalDate.now();

		// MISSED THERAPY
		if(currentMissedTherapyCount >= DEFAULT_MISSED_THERAPY_DAYS_COUNT){
			if(today.equals(latestCompliance.getDate())){
				currentScore = latestCompliance.getScore();
			}else{
				// deduct since therapy has been MISSED
				currentScore = currentScore > MISSED_THERAPY_POINTS  ? currentScore - MISSED_THERAPY_POINTS : 0;
				notificationType = MISSED_THERAPY;
			}
			// During missed therapy HMR compliance and settings deviated are false
			latestCompliance.setHmrCompliant(false);
			latestCompliance.setSettingsDeviated(false);
			// reset settingsDeviatedDays count if patient miss therapy
			latestCompliance.setSettingsDeviatedDaysCount(0);
			// increment global HMR Non Adherence Counter on Missed Therapy
			int globalHMRNonAdherenceCounter = latestCompliance.getGlobalHMRNonAdherenceCounter();
			latestCompliance.setGlobalHMRNonAdherenceCounter(++globalHMRNonAdherenceCounter);
		}else if(latestCompliance.getMissedTherapyCount() >= MISSED_THERAPY_DAYS_COUNT_THRESHOLD && currentMissedTherapyCount == 0){
			currentScore = DEFAULT_COMPLIANCE_SCORE;
			latestCompliance.setHmrCompliant(false);
			latestCompliance.setSettingsDeviated(false);
			latestCompliance.setSettingsDeviatedDaysCount(0);
			latestCompliance.setMissedTherapyCount(0);
			notificationType = ADHERENCE_SCORE_RESET; 
		}else{
			List<TherapySession> latest3DaysTherapySessions = prepareTherapySessionsForLast3days(latestCompliance.getDate(),
					existingTherapySessionMap,receivedTherapySessionsMap);
			Map<String,Double> therapyMetrics = calculateTherapyMetricsPer3Days(latest3DaysTherapySessions);
			
			boolean isHMRCompliant = isHMRCompliant(protocolConstant, therapyMetrics.get(TOTAL_DURATION));
			boolean isSettingsDeviated = false;
			
			// Settings deviated to be calculated only on Therapy done days
			if(currentMissedTherapyCount == 0){
				isSettingsDeviated = isSettingsDeviatedFor3Days(latest3DaysTherapySessions, protocolConstant);
				applySettingsDeviatedDaysCount(latestCompliance, complianceMap,
						isSettingsDeviated);
				if(isSettingsDeviated){
					currentScore -=  SETTING_DEVIATION_POINTS;
					notificationType =  SETTINGS_DEVIATION;
					// increment global settings Deviation counter
					int globalSettingsDeviationCounter = latestCompliance.getGlobalSettingsDeviationCounter();
					latestCompliance.setGlobalSettingsDeviationCounter(++globalSettingsDeviationCounter);
				}else {
					// reset settingsDeviatedDays count if patient is adhere to settings
					latestCompliance.setSettingsDeviatedDaysCount(0);
				}
			}else{
				// reset settingsDeviatedDays count if patient missed therapy
				latestCompliance.setSettingsDeviatedDaysCount(0);
			}

			latestCompliance.setSettingsDeviated(isSettingsDeviated);
			
			if(!isHMRCompliant){
				if(!today.equals(latestCompliance.getDate()) || currentMissedTherapyCount == 0){
					currentScore -=  HMR_NON_COMPLIANCE_POINTS;
					if(StringUtils.isBlank(notificationType))
						notificationType =  HMR_NON_COMPLIANCE;
					else
						notificationType =  HMR_AND_SETTINGS_DEVIATION;
					// increment global HMR Non Adherence Counter
					int globalHMRNonAdherenceCounter = latestCompliance.getGlobalHMRNonAdherenceCounter();
					latestCompliance.setGlobalHMRNonAdherenceCounter(++globalHMRNonAdherenceCounter);
				}
			}
			
			latestCompliance.setHmrCompliant(isHMRCompliant);
			// Delete existing notification if adherence to protocol
			notificationService.deleteNotificationIfExists(patientUserId,
					latestCompliance.getDate(), currentMissedTherapyCount,
					isHMRCompliant, isSettingsDeviated);
			
			// No Notification add +1
			if(StringUtils.isBlank(notificationType)){
				if(!today.equals(latestCompliance.getDate()) || currentMissedTherapyCount == 0){
					currentScore = currentScore <=  DEFAULT_COMPLIANCE_SCORE - BONUS_POINTS ? currentScore + BONUS_POINTS : DEFAULT_COMPLIANCE_SCORE;
				}
			}
		}
		
		// Patient did therapy but point has been deducted due to Protocol violation
		if(StringUtils.isNotBlank(notificationType)){
			notificationService.createOrUpdateNotification(patientUser, patient, patientUserId,
					latestCompliance.getDate(), notificationType,false);
		}

		// Compliance Score is non-negative
		currentScore = currentScore > 0? currentScore : 0;
		
		// Don't include today as missed Therapy day, This will be taken care by the job
		if(LocalDate.now().equals(latestCompliance.getDate())){
			latestCompliance.setMissedTherapyCount( currentMissedTherapyCount > 0 ? currentMissedTherapyCount-1:currentMissedTherapyCount);
		}else{
			latestCompliance.setMissedTherapyCount(currentMissedTherapyCount);
			if(currentMissedTherapyCount > 0){
				// increment global Missed Therapy counter
				int globalMissedTherapyCounter = latestCompliance.getGlobalMissedTherapyCounter();
				latestCompliance.setGlobalMissedTherapyCounter(++globalMissedTherapyCounter);
			}
		}
		
		latestCompliance.setScore(currentScore);
		complianceMap.put(latestCompliance.getDate(), latestCompliance);
	}

	private void applySettingsDeviatedDaysCount(
			PatientCompliance latestCompliance,
			SortedMap<LocalDate, PatientCompliance> complianceMap,
			boolean isSettingsDeviated) throws Exception{
		int settingsDeviatedDaysCount;
		if(isSettingsDeviated){
			int previousSettingsDeviatedDaysCount = 0;
			SortedMap<LocalDate,PatientCompliance> mostRecentComplianceMap = complianceMap.headMap(latestCompliance.getDate());
			if(mostRecentComplianceMap.size() > 0){
				PatientCompliance previousCompliance = mostRecentComplianceMap.get(mostRecentComplianceMap.lastKey());
				previousSettingsDeviatedDaysCount = previousCompliance.getSettingsDeviatedDaysCount();
			}
			// If settingsDeviationDaysCount is 0 for previous date, settingsDeviationDaysCount would be 3 by default. increments thereafter
			settingsDeviatedDaysCount =  previousSettingsDeviatedDaysCount == 0 ? DEFAULT_SETTINGS_DEVIATION_COUNT :++previousSettingsDeviatedDaysCount;
			latestCompliance.setSettingsDeviatedDaysCount(settingsDeviatedDaysCount);
		}
	} 
	
	private List<TherapySession> prepareTherapySessionsForLast3days(
			LocalDate currentTherapyDate,
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap) {
		List<TherapySession> therapySessions = new LinkedList<>();
		for(int i = 0;i < 3;i++){
			List<TherapySession> previousExistingTherapySessions = existingTherapySessionMap.get(currentTherapyDate.minusDays(i));
			if(Objects.nonNull(previousExistingTherapySessions))
				therapySessions.addAll(previousExistingTherapySessions);
		}
		return therapySessions;
	}
	
	private boolean isSettingsDeviatedFor3Days(List<TherapySession> last3daysTherapySessions,
			ProtocolConstants protocol){
		Map<LocalDate, List<TherapySession>> last3daysTherapySessionMap = last3daysTherapySessions
				.stream().collect(
						Collectors.groupingBy(TherapySession::getDate));
		boolean isSettingsDeviated = false;
		// This is for checking settings deviation, settings deviation should be calculated for consecutive 3 days
		//(exclusive missed therapy)
		if(last3daysTherapySessionMap.keySet().size() == 3){
			for(LocalDate d : last3daysTherapySessionMap.keySet()){
				List<TherapySession> therapySeesionsPerDay = last3daysTherapySessionMap.get(d);
				double weightedFrequency = calculateTherapyMetricsPer3Days(therapySeesionsPerDay).get(WEIGHTED_AVG_FREQUENCY);
				if(!isSettingsDeviated(protocol, weightedFrequency)){
					isSettingsDeviated = false;
					break;
				}else{
					isSettingsDeviated = true;
				}
			}
		}else{
			return false;
		}
		return isSettingsDeviated;
	}
}