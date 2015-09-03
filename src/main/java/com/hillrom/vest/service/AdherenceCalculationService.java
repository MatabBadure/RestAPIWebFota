package com.hillrom.vest.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.hillrom.vest.config.AdherenceScoreConstants.*;
import static com.hillrom.vest.config.NotificationTypeConstants.*;
import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientInfo;
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
			return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,actualMetrics.get("totalDuration").intValue());
		}else{ 
			// Default 2 points get deducted by assuming data not received for the day, hence add 2 points
			currentScore = currentScore ==  DEFAULT_COMPLIANCE_SCORE ?  DEFAULT_COMPLIANCE_SCORE : currentScore + 2;
			TherapySession firstTherapySessionToPatient = therapySessionRepository.findTop1ByPatientUserIdOrderByDateAsc(patientUserId);
			// First 3 days No Notifications, hence compliance doesn't change
			if(threeDaysAgo.isBefore(firstTherapySessionToPatient.getDate())){
				if(latestCompliance.getDate().isBefore(currentTherapyDate)){
					return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,actualMetrics.get("totalDuration").intValue());
				}
				latestCompliance.setScore(currentScore);
				return latestCompliance;
			}
		
			if(isSettingsDeviated(protocolConstant, actualMetrics)){
				currentScore -=  SETTING_DEVIATION_POINTS;
				notificationType =  SETTINGS_DEVIATION;				
			}				
			if(isHMRComplianceViolated(protocolConstant, actualMetrics)){
				currentScore -=  HMR_NON_COMPLIANCE_POINTS;
				if(StringUtils.isBlank(notificationType))
					notificationType =  HMR_NON_COMPLIANCE;
				else
					notificationType =  SETTINGS_DEVIATION;
			}
			
			if(previousScore == currentScore){
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
				return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,actualMetrics.get("totalDuration").intValue());
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
		}else if(protocolConstant.getMinPressure() < actualMetrics.get("weightedAvgPressure") ||
				(protocolConstant.getMaxPressure()*0.85) > actualMetrics.get("weightedAvgPressure")){
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
		double totalDuration = (double) therapySessionsPerDay.stream().collect(Collectors.summingLong(TherapySession::getDurationInMinutes));
		double weightedAvgFrequency = 0.0;
		double weightedAvgPressure = 0.0;
		double treatmentsPerDay = 0.0;
		for(TherapySession therapySession : therapySessionsPerDay){
			weightedAvgFrequency += (double)(therapySession.getDurationInMinutes()*therapySession.getFrequency()/totalDuration);
			weightedAvgPressure += (double)(therapySession.getDurationInMinutes()*therapySession.getPressure()/totalDuration);
			++treatmentsPerDay;
		}
		Map<String,Double> actualMetrics = new HashMap<>();
		actualMetrics.put("weightedAvgFrequency", weightedAvgFrequency);
		actualMetrics.put("weightedAvgPressure", weightedAvgPressure);
		actualMetrics.put("totalDuration", totalDuration);
		actualMetrics.put("treatmentsPerDay", treatmentsPerDay);
		processMissedTherapySessions();
		return actualMetrics;
	}

	/**
	 * Runs every midnight deducts the compliance score by 2 assuming therapy hasn't been done for today
	 */
	@Scheduled(cron="0 0 * * * *")
	public void processMissedTherapySessions(){
		try{
			List<PatientCompliance> patientComplianceList = patientComplianceRepository.findAllGroupByPatientUserIdOrderByDateDesc();
			List<Long> patientUserIds = new LinkedList<>();
			DateTime today = DateTime.now();
			patientComplianceList.forEach(compliance -> {			
				patientUserIds.add(compliance.getPatientUser().getId());
				compliance.setDate(today.toLocalDate());
				compliance.setId(null);
				compliance.setHmrRunRate(compliance.getHmrRunRate());
				if(compliance.getScore() > 0)
					compliance.setScore(compliance.getScore()- MISSED_THERAPY_POINTS);
			});
			List<TherapySession> latestTherapySessions = therapySessionRepository.findTop1ByPatientUserIdOrderByEndTimeDesc(patientUserIds);
			List<Notification> notifications = new LinkedList<>();
			latestTherapySessions.forEach(latestTherapySession -> {
				DateTime therapySessionDateTime = new DateTime(latestTherapySession.getDate().toDateTime(org.joda.time.LocalTime.MIDNIGHT));
				int missedTherapyDays = Days.daysBetween(therapySessionDateTime, today).getDays();
				if( missedTherapyDays > 0 && missedTherapyDays %3 == 0){
					notifications.add(new Notification(MISSED_THERAPY, today.toLocalDate(), latestTherapySession.getPatientUser(), latestTherapySession.getPatientInfo(), false));
				}
			});
			notificationRepository.save(notifications);
			patientComplianceRepository.save(patientComplianceList);
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotification("processMissedTherapySessions",writer.toString());
		}
	}
	
	/**
	 * Runs every 3pm , sends the notifications to Patient User.
	 */
	@Scheduled(cron="0 15 * * * *")
	public void sendNotificationMail(){
		LocalDate today = LocalDate.now();
		List<Notification> notifications = notificationRepository.findByDate(today);
		try{
			notifications.forEach(notification -> {
				User patientUser = notification.getPatientUser();
				if(Objects.nonNull(patientUser.getEmail())){
					mailService.sendNotificationMail(patientUser,notification.getNotificationType());
				}
			});
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotification("sendNotificationMail",writer.toString());
		}
		
	}
	
}