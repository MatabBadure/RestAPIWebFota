package com.hillrom.vest.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.config.NotificationTypeConstants;
import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.ProtocolConstantsRepository;
import com.hillrom.vest.repository.TherapySessionRepository;

@Service
@Transactional
public class AdherenceCalculationService {

	private static final int DEFAULT_COMPLIANCE_SCORE = 100;

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
	
	public List<PatientCompliance> getComplainceScoreByTherapySessions(List<TherapySession> therapySessions) throws HillromException{
		Long patientUserId = therapySessions.get(0).getPatientUser().getId();
		
		ProtocolConstants protocolConstants = getProtocolByPatientUserId(patientUserId);
		
		Map<Integer,List<TherapySession>> groupByTherapyDate = therapySessions.stream()
		        .collect(Collectors.groupingBy(TherapySession::getTherapyDayOfTheYear));
		
		Map<Integer,PatientCompliance> patientCompliance = new HashMap<>();
		
		Iterator<Integer> days = groupByTherapyDate.keySet().iterator();
		while(days.hasNext()){
			Integer day = days.next();
			List<TherapySession> therapySessionsPerDay = groupByTherapyDate.get(day);
			PatientCompliance calculatedPatientCompliance = calculateCompliancePerDay(therapySessionsPerDay,protocolConstants);
			patientCompliance.put(day, calculatedPatientCompliance);
		}
		return null;
	}

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
		LocalDate current = therapySessionsPerDay.get(0).getDate();
		List<TherapySession> existingTherapySessions = therapySessionRepository.findByPatientUserIdAndDateRange(patientUserId, current.minusDays(3),current);
		PatientCompliance existingCompliance = patientComplianceRepository.findTop1ByPatientUserIdOrderByDateDesc(patientUserId);
		Integer currentScore = Objects.nonNull(existingCompliance) ? existingCompliance.getScore() : DEFAULT_COMPLIANCE_SCORE;
		Integer previousScore = currentScore;
		String notificationType = "";
		Map<String,Double> actualMetrics = actualTherapyMetricsPerDay(existingTherapySessions);
		
		// First Time received Data,hence compliance will be 100.
		if(existingTherapySessions.isEmpty() || Objects.isNull(existingCompliance)){
			return new PatientCompliance(currentScore, LocalDate.now(), patient, patientUser,actualMetrics.get("totalDuration").intValue());
		}else{ 
			// Default 2 points get deducted by assuming data not received for the day, hence add 2 points
			currentScore = currentScore.equals(DEFAULT_COMPLIANCE_SCORE) ? DEFAULT_COMPLIANCE_SCORE : currentScore + 2;
			TherapySession firstTherapySessionToPatient = therapySessionRepository.findTop1ByPatientUserIdOrderByDateAsc(patientUserId);
			// First 3 days No Notifications, hence compliance doesn't change
			if(current.minusDays(3).isBefore(firstTherapySessionToPatient.getDate()) || current.minusDays(3).equals((firstTherapySessionToPatient.getDate()))){
				existingCompliance.setScore(currentScore);
				return existingCompliance;
			}
		
			if(isSettingsDeviated(protocolConstant, actualMetrics)){
				currentScore -= 1;
				notificationType = NotificationTypeConstants.SETTINGS_DEVIATION;				
			}				
			if(isHMRComplianceViolated(protocolConstant, actualMetrics)){
				currentScore -= 2;
				if(StringUtils.isBlank(notificationType))
					notificationType = NotificationTypeConstants.HMR_NON_COMPLIANCE;
				else
					notificationType = NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION;
			}
			if(previousScore == currentScore && currentScore != 100)
					currentScore += 1;
			
			existingCompliance.setScore(currentScore);
			existingCompliance.setHmrRunRate(actualMetrics.get("totalDuration").intValue());
			
			// Point has been deducted due to Protocol violation
			if(previousScore > currentScore){
				Notification notification = new Notification(notificationType,LocalDate.now(),patientUser,patient,false);
				notificationRepository.save(notification);
			}
			
			return existingCompliance;
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
