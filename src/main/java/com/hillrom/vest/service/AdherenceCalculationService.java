package com.hillrom.vest.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.config.AdherenceScoreConstants;
import com.hillrom.vest.config.NotificationTypeConstants;
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

	public ProtocolConstants getProtocolByPatientUserId(
			Long patientUserId) {
		List<PatientProtocolData> protocolData =  protocolService.findOneByPatientUserIdAndStatus(patientUserId, false);
		if(protocolData.size() > 0){			
			ProtocolConstants protocolConstant = new ProtocolConstants();
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
			protocolConstant.setMaxFrequency(maxFrequency);
			protocolConstant.setMinFrequency(minFrequency);
			protocolConstant.setMaxPressure(maxPressure);
			protocolConstant.setMinPressure(minPressure);
			protocolConstant.setTreatmentsPerDay(treatmentsPerDay);
			protocolConstant.setMinDuration(minDuration);
			protocolConstant.setMaxDuration(maxDuration);
			return protocolConstant;
		}else{
			return protocolConstantsRepository.findOne(1L);
		}
	}

	public PatientCompliance calculateCompliancePerDay(
			List<TherapySession> therapySessionsPerDay,ProtocolConstants protocolConstant) {
		User patientUser = therapySessionsPerDay.get(0).getPatientUser();
		PatientInfo patient = therapySessionsPerDay.get(0).getPatientInfo();
		Long patientUserId = patientUser.getId();
		LocalDate currentTherapyDate = therapySessionsPerDay.get(0).getDate();
		List<TherapySession> latest3TherapySessions = therapySessionRepository.findByPatientUserIdAndDateRange(patientUserId, currentTherapyDate.minusDays(3),currentTherapyDate);
		PatientCompliance latestCompliance = patientComplianceRepository.findTop1ByPatientUserIdOrderByDateDesc(patientUserId);
		int currentScore = Objects.nonNull(latestCompliance) ? latestCompliance.getScore() : AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;
		int previousScore = currentScore;
		String notificationType = "";
		Map<String,Double> actualMetrics = actualTherapyMetricsPerDay(latest3TherapySessions);
		
		// First Time received Data,hence compliance will be 100.
		if(latest3TherapySessions.isEmpty() || Objects.isNull(latestCompliance)){
			return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,actualMetrics.get("totalDuration").intValue());
		}else{ 
			// Default 2 points get deducted by assuming data not received for the day, hence add 2 points
			currentScore = currentScore == AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE ? AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE : currentScore + 2;
			TherapySession firstTherapySessionToPatient = therapySessionRepository.findTop1ByPatientUserIdOrderByDateAsc(patientUserId);
			// First 3 days No Notifications, hence compliance doesn't change
			if(currentTherapyDate.minusDays(3).isBefore(firstTherapySessionToPatient.getDate())){
				if(latestCompliance.getDate().isBefore(currentTherapyDate)){
					return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,actualMetrics.get("totalDuration").intValue());
				}
				latestCompliance.setScore(currentScore);
				return latestCompliance;
			}
		
			if(isSettingsDeviated(protocolConstant, actualMetrics)){
				currentScore -= AdherenceScoreConstants.SETTING_DEVIATION_POINTS;
				notificationType = NotificationTypeConstants.SETTINGS_DEVIATION;				
			}				
			if(isHMRComplianceViolated(protocolConstant, actualMetrics)){
				currentScore -= AdherenceScoreConstants.HMR_NON_COMPLIANCE_POINTS;
				if(StringUtils.isBlank(notificationType))
					notificationType = NotificationTypeConstants.HMR_NON_COMPLIANCE;
				else
					notificationType = NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION;
			}
			
			if(previousScore == currentScore && currentScore != AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE)
					currentScore += 1;
			
			// Point has been deducted due to Protocol violation
			if(previousScore > currentScore){
				Notification notification = new Notification(notificationType,currentTherapyDate,patientUser,patient,false);
				notificationRepository.save(notification);
			}
			
			if(latestCompliance.getDate().isBefore(currentTherapyDate)){
				return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,actualMetrics.get("totalDuration").intValue());
			}
			
			latestCompliance.setScore(currentScore);
			latestCompliance.setHmrRunRate(actualMetrics.get("totalDuration").intValue());
			return latestCompliance;
		}
	}

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
		return actualMetrics;
	}
	
}
