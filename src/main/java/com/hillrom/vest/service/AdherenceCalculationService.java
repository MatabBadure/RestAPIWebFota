package com.hillrom.vest.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;
import static com.hillrom.vest.config.AdherenceScoreConstants.HMR_NON_COMPLIANCE_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.MISSED_THERAPY_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.SETTING_DEVIATION_POINTS;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;
import static com.hillrom.vest.service.util.DateUtil.convertLocalDateToDateTime;
import static com.hillrom.vest.service.util.DateUtil.getDaysCountBetweenLocalDates;
import static com.hillrom.vest.service.util.DateUtil.getPlusOrMinusTodayLocalDate;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateCumulativeDuration;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateHMRRunRatePerDays;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateWeightedAvg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.ProtocolConstantsRepository;
import com.hillrom.vest.repository.TherapySessionRepository;


@Service
@Transactional
public class AdherenceCalculationService {

	@Inject
	private PatientProtocolService protocolService;
	
	@Inject
	private TherapySessionRepository therapySessionRepository;
	
	@Inject
	private ProtocolConstantsRepository  protocolConstantsRepository;
	
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
	private TherapySessionService therapySessionService;
	
	@Inject
	private PatientNoEventService noEventService;

	/**
	 * Get Protocol Constants by loading Protocol data
	 * @param patientUserId
	 * @return
	 */
	public ProtocolConstants getProtocolByPatientUserId(
			Long patientUserId) {
		List<PatientProtocolData> protocolData =  protocolService.findOneByPatientUserIdAndStatus(patientUserId, false);
		if(protocolData.size() > 0){			
			int maxFrequency = 0, minFrequency = 0, minPressure = 0, maxPressure = 0, minDuration = 0, maxDuration = 0, treatmentsPerDay = 0;
			for(PatientProtocolData protocol : protocolData){
				maxFrequency += protocol.getMaxFrequency();
				minFrequency += protocol.getMinFrequency();
				minPressure += protocol.getMinPressure();
				maxPressure += protocol.getMaxPressure();
				minDuration += protocol.getMinMinutesPerTreatment() * protocol.getTreatmentsPerDay();
				maxDuration += protocol.getMaxMinutesPerTreatment() * protocol.getTreatmentsPerDay();
				treatmentsPerDay += protocol.getTreatmentsPerDay();
			}
			return new ProtocolConstants(maxFrequency,minFrequency,maxPressure,minPressure,treatmentsPerDay,minDuration,maxDuration);
		}else{
			return protocolConstantsRepository.findOne(1L);
		}
	}

	/**
	 * Calculate Compliance per day (Get latest 3 days therapy Sessions and calculate WeightedAvg and add/substract points)
	 * @param therapySessionsPerDay
	 * @param protocolConstant
	 * @return
	 */
	// TODO : to be re-factored, will be taken care while integrating the Protocol changes
	public PatientCompliance calculateCompliancePerDay(
			List<TherapySession> therapySessionsPerDay,ProtocolConstants protocolConstant) {
		User patientUser = therapySessionsPerDay.get(0).getPatientUser();
		PatientInfo patient = therapySessionsPerDay.get(0).getPatientInfo();
		Long patientUserId = patientUser.getId();
		LocalDate currentTherapyDate = therapySessionsPerDay.get(0).getDate();
		LocalDate threeDaysAgo = currentTherapyDate.minusDays(3);
		List<TherapySession> latest3TherapySessions = therapySessionRepository.findByPatientUserIdAndDateRange(patientUserId, threeDaysAgo,currentTherapyDate);
		PatientCompliance latestCompliance = patientComplianceRepository.findTop1ByPatientUserIdOrderByDateDesc(patientUserId);
		int currentScore = Objects.nonNull(latestCompliance) ? latestCompliance.getScore() : DEFAULT_COMPLIANCE_SCORE;
		int previousScore = currentScore;
		String notificationType = "";
		Map<String,Double> actualMetrics = actualTherapyMetricsPerDay(latest3TherapySessions);
		
		// First Time received Data,hence compliance will be 100.
		if(latest3TherapySessions.isEmpty() || Objects.isNull(latestCompliance)){
			noEventService.updatePatientFirstTransmittedDate(patientUserId,currentTherapyDate);
			return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,actualMetrics.get("totalDuration").intValue(),false,false);
		}else{ 
			// Default 2 points get deducted by assuming data not received for the day, hence add 2 points
			currentScore = currentScore ==  DEFAULT_COMPLIANCE_SCORE ?  DEFAULT_COMPLIANCE_SCORE : currentScore + 2;
			TherapySession firstTherapySessionToPatient = therapySessionRepository.findTop1ByPatientUserIdOrderByDateAsc(patientUserId);
			// First 3 days No Notifications, hence compliance doesn't change
			if(threeDaysAgo.isBefore(firstTherapySessionToPatient.getDate())){
				if(latestCompliance.getDate().isBefore(currentTherapyDate)){
					return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,actualMetrics.get("totalDuration").intValue(),false,false);
				}
				latestCompliance.setScore(currentScore);
				return latestCompliance;
			}
		
			boolean isHMRCompliant = isHMRComplianceViolated(protocolConstant, actualMetrics);
			boolean isSettingsDeviated = isSettingsDeviated(protocolConstant, actualMetrics);
			
			if(isSettingsDeviated(protocolConstant, actualMetrics)){
				currentScore -=  SETTING_DEVIATION_POINTS;
				notificationType =  SETTINGS_DEVIATION;				
			}				
			if(isHMRCompliant){
				currentScore -=  HMR_NON_COMPLIANCE_POINTS;
				if(StringUtils.isBlank(notificationType))
					notificationType =  HMR_NON_COMPLIANCE;
				else
					notificationType =  SETTINGS_DEVIATION;
			}
			
			if(previousScore < currentScore){
				notificationService.deleteNotification(patientUserId,currentTherapyDate);
				currentScore = currentScore !=  DEFAULT_COMPLIANCE_SCORE ? currentScore + 1 : DEFAULT_COMPLIANCE_SCORE;
			}
			
			// Point has been deducted due to Protocol violation
			if(previousScore > currentScore){
				notificationService.createOrUpdateNotification(patientUser, patient, patientUserId,
						currentTherapyDate, notificationType,false);
			}

			// Compliance Score is non-negative
			currentScore = currentScore > 0? currentScore : 0; 
			if(latestCompliance.getDate().isBefore(currentTherapyDate)){
				return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,actualMetrics.get("totalDuration").intValue(),isHMRCompliant,isSettingsDeviated);
			}
			
			latestCompliance.setScore(currentScore);
			latestCompliance.setHmrRunRate(actualMetrics.get("totalDuration").intValue());
			return latestCompliance;
		}
	}

	/**
	 * Checks whether HMR Compliance violated(minHMRReading < actual < maxHMRReading)
	 * @param protocolConstant
	 * @param actualMetrics
	 * @return
	 */
	public boolean isHMRComplianceViolated(ProtocolConstants protocolConstant,
			Map<String, Double> actualMetrics) {
		// Custom Protocol, Min/Max Duration calculation is done
		int minHMRReading = Objects.nonNull(protocolConstant
				.getMinDuration()) ? protocolConstant.getMinDuration()
				: protocolConstant.getTreatmentsPerDay()
						* protocolConstant.getMinMinutesPerTreatment();
				
		int maxHMRReading = Objects.nonNull(protocolConstant
				.getMaxDuration()) ? protocolConstant.getMaxDuration()
				:protocolConstant.getTreatmentsPerDay() * protocolConstant.getMaxMinutesPerTreatment();
		if(minHMRReading > actualMetrics.get("totalDuration") ||
				maxHMRReading < actualMetrics.get("totalDuration")){
			return true;
		}else if(protocolConstant.getTreatmentsPerDay() < actualMetrics.get("treatmentsPerDay")){
			return true;
		}
		return false;
	}

	/**
	 * Checks Whether Settings deviated(protocol.minFrequency < actualWeightedAvgFreq < protocol.maxFrequency)
	 * @param protocolConstant
	 * @param actualMetrics
	 * @return
	 */
	public boolean isSettingsDeviated(ProtocolConstants protocolConstant,
			Map<String, Double> actualMetrics) {
		if(protocolConstant.getMinFrequency() > actualMetrics.get("weightedAvgFrequency") 
				|| (protocolConstant.getMaxFrequency()*0.85) > actualMetrics.get("weightedAvgFrequency")){
			return true;
		}
		return false;
	}

	/**
	 * Calculates Metrics such as weightedAvgFrequency,Pressure,treatmentsPerDay,duration
	 * @param therapySessionsPerDay
	 * @return
	 */
	public Map<String,Double> actualTherapyMetricsPerDay(
			List<TherapySession> therapySessionsPerDay) {
		double totalDuration = calculateCumulativeDuration(therapySessionsPerDay);
		double weightedAvgFrequency = 0.0;
		double weightedAvgPressure = 0.0;
		double treatmentsPerDay = 0.0;
		for(TherapySession therapySession : therapySessionsPerDay){
			Long durationInMinutes = therapySession.getDurationInMinutes();
			weightedAvgFrequency += calculateWeightedAvg(totalDuration,durationInMinutes,therapySession.getFrequency());
			weightedAvgPressure += calculateWeightedAvg(totalDuration,durationInMinutes,therapySession.getPressure());
			++treatmentsPerDay;
		}
		Map<String,Double> actualMetrics = new HashMap<>();
		actualMetrics.put("weightedAvgFrequency", weightedAvgFrequency);
		actualMetrics.put("weightedAvgPressure", weightedAvgPressure);
		actualMetrics.put("totalDuration", totalDuration);
		actualMetrics.put("treatmentsPerDay", treatmentsPerDay);
		return actualMetrics;
	}

	/**
	 * Runs every midnight deducts the compliance score by 2 assuming therapy hasn't been done for today
	 */
	@Scheduled(cron="0 0 0 * * * ")
	public void processMissedTherapySessions(){
		try{
			List<PatientCompliance> patientComplianceList = patientComplianceRepository.findAllGroupByPatientUserIdOrderByDateDesc();
			List<PatientCompliance> newComplianceList = new LinkedList<>();
			List<Long> patientUserIds = new LinkedList<>();
			DateTime today = DateTime.now();
			patientComplianceList.forEach(compliance -> {			
				patientUserIds.add(compliance.getPatientUser().getId());
				PatientCompliance newCompliance = new PatientCompliance(today.toLocalDate(),compliance.getPatient(),compliance.getPatientUser(),compliance.getHmrRunRate(),compliance.getMissedTherapyCount()+1,compliance.getDate());
				int score = Objects.isNull(compliance.getScore()) ? 0 : compliance.getScore(); 
				if(score > 0)
					newCompliance.setScore(score- MISSED_THERAPY_POINTS);
				int missedTherapyCount = compliance.getMissedTherapyCount();
				if(missedTherapyCount > 0 && missedTherapyCount % 3 == 0){
					notificationService.createOrUpdateNotification(compliance.getPatientUser(), compliance.getPatient(), compliance.getPatientUser().getId(),
							today.toLocalDate(), MISSED_THERAPY,false);
				}
				newComplianceList.add(newCompliance);
			});
			Map<Long,Integer> hmrRunRateMap = calculateHMRRunRateForPatientUsers(patientUserIds,getPlusOrMinusTodayLocalDate(-3),getPlusOrMinusTodayLocalDate(-1));
			newComplianceList.parallelStream().forEach(patientCompliance -> {
				Integer hmrRunRate = hmrRunRateMap.get(patientCompliance.getPatientUser().getId());
				hmrRunRate = Objects.nonNull(hmrRunRate)? hmrRunRate : 0;
				patientCompliance.setHmrRunRate(hmrRunRate);
				complianceService.createOrUpdate(patientCompliance);
			});
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotification("processMissedTherapySessions",writer.toString());
		}
	}
	
	/**
	 * Calculate HMRRunRate For PatientUsers
	 * @param patientUserIds
	 * @return
	 */
	public Map<Long,Integer> calculateHMRRunRateForPatientUsers(List<Long> patientUserIds,LocalDate from,LocalDate to){
		Map<Long,Integer> patientUserHMRRunrateMap = new HashMap<>();
		List<TherapySession> therapySessions = therapySessionRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<User,List<TherapySession>> therapySessionsPerPatient = therapySessions.stream().collect(Collectors.groupingBy(TherapySession::getPatientUser));
		int days = getDaysCountBetweenLocalDates(from, to);
		for(User patientUser : therapySessionsPerPatient.keySet()){
			List<TherapySession> sessions = therapySessionsPerPatient.get(patientUser);
			patientUserHMRRunrateMap.put(patientUser.getId(), calculateHMRRunRatePerDays(sessions,days));
		}
		return patientUserHMRRunrateMap;
	}
	
	/**
	 * Runs every 3pm , sends the notifications to Patient User.
	 */
	@Scheduled(cron="0 0 15 * * *")
	public void sendNotificationMail(){
		LocalDate today = LocalDate.now();
		List<Notification> notifications = notificationRepository.findByDate(today);
		try{
			notifications.forEach(notification -> {
				User patientUser = notification.getPatientUser();
				if(Objects.nonNull(patientUser.getEmail())){
					// integrated Accepting mail notifications
					String notificationType = notification.getNotificationType();
					if(isPatientUserAcceptNotification(patientUser,
							notificationType))
					mailService.sendNotificationMail(patientUser,notificationType);
				}
			});
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotification("sendNotificationMail",writer.toString());
		}
		
	}

	private boolean isPatientUserAcceptNotification(User patientUser,
			String notificationType) {
		// TODO: rename the columns and member variables next sprint
		return (patientUser.isAcceptHMRNotification() && HMR_NON_COMPLIANCE.equalsIgnoreCase(notificationType)) || 
				(patientUser.isAcceptHMRSetting() && SETTINGS_DEVIATION.equalsIgnoreCase(notificationType)) ||
				(patientUser.isHMRNotification() && MISSED_THERAPY.equalsIgnoreCase(notificationType));
	}
	
}