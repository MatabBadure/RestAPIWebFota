package com.hillrom.vest.service.monarch;

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
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_VEST_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_MONARCH_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION_VEST;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_MONARCH_AND_SETTINGS_DEVIATION_VEST;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_VEST_AND_SETTINGS_DEVIATION_VEST;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION_MONARCH;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_MONARCH_AND_SETTINGS_DEVIATION_MONARCH;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_VEST_AND_SETTINGS_DEVIATION_MONARCH;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE_VEST;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE_MONARCH;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION_VEST;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION_MONARCH;
import static com.hillrom.vest.config.AdherenceScoreConstants.ADHERENCE_SETTING_DEFAULT_DAYS;
import static com.hillrom.vest.service.util.DateUtil.getPlusOrMinusTodayLocalDate;
import static com.hillrom.vest.service.util.DateUtil.getDateBeforeSpecificDays;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateCumulativeDuration;
import static com.hillrom.vest.service.util.monarch.PatientVestDeviceTherapyUtilMonarch.calculateCumulativeDuration;
import static com.hillrom.vest.service.util.monarch.PatientVestDeviceTherapyUtilMonarch.calculateHMRRunRatePerSession;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateHMRRunRatePerSession;
import static com.hillrom.vest.service.util.monarch.PatientVestDeviceTherapyUtilMonarch.calculateHMRRunRatePerSessionBoth;
import static com.hillrom.vest.service.util.monarch.PatientVestDeviceTherapyUtilMonarch.calculateWeightedAvg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.NotificationMonarch;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientComplianceMonarch;
import com.hillrom.vest.domain.PatientDevicesAssoc;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.ProtocolConstantsMonarch;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.TherapySessionMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientNoEventsRepository;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.repository.monarch.AdherenceResetMonarchRepository;
import com.hillrom.vest.repository.monarch.ClinicMonarchRepository;
import com.hillrom.vest.repository.monarch.NotificationMonarchRepository;
import com.hillrom.vest.repository.monarch.PatientComplianceMonarchRepository;
import com.hillrom.vest.repository.monarch.PatientNoEventsMonarchRepository;
import com.hillrom.vest.repository.monarch.TherapySessionMonarchRepository;
import com.hillrom.vest.service.AdherenceCalculationService;
import com.hillrom.vest.service.ClinicPatientService;
import com.hillrom.vest.service.MailService;
import com.hillrom.vest.service.PatientComplianceService;
import com.hillrom.vest.service.PatientNoEventService;
import com.hillrom.vest.service.PatientProtocolService;
import com.hillrom.vest.service.TherapySessionService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.CareGiverStatsNotificationVO;
import com.hillrom.vest.web.rest.dto.ClinicStatsNotificationVO;
import com.hillrom.vest.web.rest.dto.PatientStatsVO;

//hill-1956
import com.hillrom.vest.domain.AdherenceReset;
import com.hillrom.vest.domain.AdherenceResetMonarch;
import com.hillrom.vest.repository.AdherenceResetRepository;
//hill-1956


@Service
@Transactional
public class AdherenceCalculationServiceMonarch{

	private static final String TOTAL_DURATION = "totalDuration";

	private static final String WEIGHTED_AVG_INTESITY = "weightedAvgIntensity";

	private static final String WEIGHTED_AVG_FREQUENCY = "weightedAvgFrequency";

	@Inject
	private PatientProtocolMonarchService protocolMonarchService;
	
	@Inject
	private TherapySessionMonarchRepository therapySessionMonarchRepository;
	
	@Inject
	private PatientComplianceMonarchRepository patientComplianceMonarchRepository;
	
	@Inject
	private NotificationMonarchRepository notificationMonarchRepository;
	
	@Inject
	private MailService mailService;
	
	@Inject
	private NotificationMonarchService notificationMonarchService;
	
	@Inject
	private PatientComplianceMonarchService complianceMonarchService;
	
	@Inject
	private PatientNoEventMonarchService noEventMonarchService;
	
	@Inject
	private PatientNoEventService noEventMonarchServiceVest;
	
	@Inject
	private ClinicMonarchRepository clinicMonarchRepository;
	
	@Inject
	private ClinicPatientService clinicPatientService;
	
	@Inject
	private UserService userService;
	
	//hill-1956
	@Inject
	private AdherenceResetMonarchRepository adherenceResetMonarchRepository;
	//hill-1956
	
	@Inject
	@Lazy
	private AdherenceCalculationService adherenceCalculationService;
	
	@Inject
	private PatientComplianceService complianceService;
	
	@Inject
	private PatientNoEventsRepository patientNoEventRepository;

	@Inject
	private PatientProtocolService protocolVestService;
	
	@Inject
	@Lazy
	private TherapySessionService therapySessionService;
	
	@Inject
	private PatientDevicesAssocRepository patientDevicesAssocRepository;
	
	@Inject
	private PatientComplianceRepository patientComplianceRepository;
	
	@Inject
	private PatientInfoRepository patientInfoRepository;
	
	@Inject
	private NotificationRepository notificationRepository;
	
	@Inject
	private TherapySessionRepository therapySessionRepository;
	
	@Inject
	private TherapySessionServiceMonarch therapySessionMonarchService;
	
	@Inject
	private PatientNoEventsMonarchRepository noEventMonarchRepository;
	
	private final Logger log = LoggerFactory.getLogger(AdherenceCalculationServiceMonarch.class);
	
	/**
	 * Get Protocol Constants by loading Protocol data
	 * @param patientUserId
	 * @return
	 */
	public ProtocolConstantsMonarch getProtocolByPatientUserId(
			Long patientUserId) throws Exception{
		List<Long> patientUserIds = new LinkedList<>();
		patientUserIds.add(patientUserId);
		Map<Long,ProtocolConstantsMonarch> userIdProtolConstantsMap = protocolMonarchService.getProtocolByPatientUserIds(patientUserIds);
		return userIdProtolConstantsMap.get(patientUserId);
	}

	/**
	 * Checks whether HMR Compliance violated(minHMRReading < actual < maxHMRReading)
	 * @param protocolConstant
	 * @param actualMetrics
	 * @return
	 */
	public boolean isHMRCompliant(ProtocolConstantsMonarch protocolConstant,
			double actualTotalDurationSettingDays, 
			Integer adherenceSettingDay) {
		// Custom Protocol, Min/Max Duration calculation is done
		int minHMRReading = Objects.nonNull(protocolConstant
				.getMinDuration()) ? protocolConstant.getMinDuration()
				: protocolConstant.getTreatmentsPerDay()
						* protocolConstant.getMinMinutesPerTreatment();
		if( Math.round(minHMRReading * LOWER_BOUND_VALUE) > Math.round(actualTotalDurationSettingDays/adherenceSettingDay)){
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether HMR Compliance violated(minHMRReading < actual < maxHMRReading)
	 * @param protocolConstantMonarch
	 * @param actualMetrics
	 * @return
	 */
	public boolean isHMRCompliant(ProtocolConstantsMonarch protocolConstantMonarch,
			ProtocolConstants protocolConstantVest,
			double actualTotalDurationSettingDays, 
			Integer adherenceSettingDay) {
		// Custom Protocol, Min/Max Duration calculation is done
		int minHMRReading = Objects.nonNull(protocolConstantMonarch
				.getMinDuration()) ? protocolConstantMonarch.getMinDuration()
				: protocolConstantMonarch.getTreatmentsPerDay()
						* protocolConstantMonarch.getMinMinutesPerTreatment();
		
		// Getting the minimum reading from the vest protocol		
		int minHMRReadingVest = Objects.nonNull(protocolConstantVest
				.getMinDuration()) ? protocolConstantVest.getMinDuration()
				: protocolConstantVest.getTreatmentsPerDay()
						* protocolConstantVest.getMinMinutesPerTreatment();

		// Adding both the protocols minimum HMR from Vest and Monarch		
		minHMRReading = minHMRReading + minHMRReadingVest;
		
		if( Math.round(minHMRReading * LOWER_BOUND_VALUE) > Math.round(actualTotalDurationSettingDays/adherenceSettingDay)){
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
	public boolean isSettingsDeviated(ProtocolConstantsMonarch protocolConstant,
			double weightedAvgFrequency) {
		if((protocolConstant.getMinFrequency()* LOWER_BOUND_VALUE) > weightedAvgFrequency){
			return true;
		}
		return false;
	}

	/**
	 * Calculates Metrics such as weightedAvgFrequency,Intensity,treatmentsPerDay,duration for last adherence setting days
	 * @param therapySessionsPerDay
	 * @return
	 */
	public Map<String,Double> calculateTherapyMetricsPerSettingDays(
			List<TherapySessionMonarch> therapySessionsPerDay) {
		double totalDuration = calculateCumulativeDuration(therapySessionsPerDay);
		double weightedAvgFrequency = 0f;
		double weightedAvgIntensity = 0f;
		for(TherapySessionMonarch therapySession : therapySessionsPerDay){
			int durationInMinutes = therapySession.getDurationInMinutes(); 
			weightedAvgFrequency += calculateWeightedAvg(totalDuration,durationInMinutes,therapySession.getFrequency());
			weightedAvgIntensity += calculateWeightedAvg(totalDuration,durationInMinutes,therapySession.getIntensity());
		}
		Map<String,Double> actualMetrics = new HashMap<>();
		weightedAvgFrequency = Math.round(weightedAvgFrequency);
		weightedAvgIntensity = Math.round(weightedAvgIntensity);
		actualMetrics.put(WEIGHTED_AVG_FREQUENCY, weightedAvgFrequency);
		actualMetrics.put(WEIGHTED_AVG_INTESITY, weightedAvgIntensity);
		actualMetrics.put(TOTAL_DURATION, totalDuration);
		return actualMetrics;
	}

	/**
	 * Calculates Metrics such as weightedAvgFrequency,Intensity,treatmentsPerDay,duration for last adherence setting days
	 * @param therapySessionsPerDay
	 * @return
	 */
	public Map<String,Double> calculateTherapyMetricsPerSettingDaysBoth(
			Map<String, Object> combined) {
		
		List<TherapySessionMonarch> therapySessionsPerDay = (List<TherapySessionMonarch>) combined.get("Monarch");
		List<TherapySession> therapySessionsPerDayVest = (List<TherapySession>) combined.get("Vest");
		
		double totalDuration = calculateCumulativeDuration(therapySessionsPerDay) + calculateCumulativeDuration(therapySessionsPerDayVest);
		
		double weightedAvgFrequency = 0f;
		double weightedAvgIntensity = 0f;
		
		for(TherapySessionMonarch therapySession : therapySessionsPerDay){
			int durationInMinutes = therapySession.getDurationInMinutes(); 
			weightedAvgFrequency += calculateWeightedAvg(totalDuration,durationInMinutes,therapySession.getFrequency());
			weightedAvgIntensity += calculateWeightedAvg(totalDuration,durationInMinutes,therapySession.getIntensity());
		}
		
		for(TherapySession therapySession : therapySessionsPerDayVest){
			int durationInMinutes = therapySession.getDurationInMinutes(); 
			weightedAvgFrequency += calculateWeightedAvg(totalDuration,durationInMinutes,therapySession.getFrequency());
			weightedAvgIntensity += calculateWeightedAvg(totalDuration,durationInMinutes,therapySession.getPressure());
		}
		
		Map<String,Double> actualMetrics = new HashMap<>();
		weightedAvgFrequency = Math.round(weightedAvgFrequency);
		weightedAvgIntensity = Math.round(weightedAvgIntensity);
		actualMetrics.put(WEIGHTED_AVG_FREQUENCY, weightedAvgFrequency);
		actualMetrics.put(WEIGHTED_AVG_INTESITY, weightedAvgIntensity);
		actualMetrics.put(TOTAL_DURATION, totalDuration);
		
		return actualMetrics;
	}
	
	/**
	 * Runs every midnight deducts the compliance score by 5 if therapy hasn't been done for adherence setting day(s)
	 */
	@Scheduled(cron="0 55 23 * * * ")	
	public void processMissedTherapySessionsMonarch(){
		try{
			LocalDate today = LocalDate.now();
			log.debug("Started calculating missed therapy "+DateTime.now()+","+today);
			List<PatientComplianceMonarch> mstPatientComplianceList = patientComplianceMonarchRepository.findMissedTherapyPatientsRecords();
			Map<Long,PatientComplianceMonarch> mstNotificationMap = new HashMap<>();
			Map<Long,PatientComplianceMonarch> hmrNonComplianceMap = new HashMap<>();
			Map<Long,PatientComplianceMonarch> hmrNonComplianceMapBoth = new HashMap<>();
			Map<Long,ProtocolConstantsMonarch> userProtocolConstantsMap = new HashMap<>();
			Map<Long,ProtocolConstants> userProtocolConstantsVestMap = new HashMap<>();
			Map<Long,PatientComplianceMonarch> complianceMap = new HashMap<>();
			Map<Long,NotificationMonarch> notificationMap = new HashMap<>();
			Map<Long,PatientNoEventMonarch> userIdNoEventMap = noEventMonarchService.findAllGroupByPatientUserId();
			
			Map<Long,PatientNoEvent> userIdNoEventMapVest = noEventMonarchServiceVest.findAllGroupByPatientUserId();
			
			for(PatientComplianceMonarch compliance : mstPatientComplianceList){
				
				String deviceType = adherenceCalculationService.getDeviceTypeValue(compliance.getPatient().getId());
				
				if(deviceType.equals("MONARCH"))
					processForEachPatientMonarch(compliance, userIdNoEventMap, complianceMap, mstNotificationMap,hmrNonComplianceMap);
				else if(deviceType.equals("BOTH"))
					processForEachPatientBoth(compliance, userIdNoEventMap, userIdNoEventMapVest, complianceMap, mstNotificationMap,hmrNonComplianceMapBoth);
			}
			
			List<Long> patientUserIds =  new LinkedList<>(hmrNonComplianceMap.keySet());
			patientUserIds.addAll(new LinkedList<>(hmrNonComplianceMapBoth.keySet()));
			
			userProtocolConstantsMap = protocolMonarchService.getProtocolByPatientUserIds(patientUserIds);
			userProtocolConstantsVestMap = protocolVestService.getProtocolByPatientUserIds(patientUserIds);

			if(Objects.nonNull(hmrNonComplianceMapBoth)){
				// Update HMR for the Both device patients
				calculateHMRComplianceForMSTBoth(today, hmrNonComplianceMapBoth,
							userProtocolConstantsMap, userProtocolConstantsVestMap, complianceMap, notificationMap);
			}
			
			calculateHMRComplianceForMST(today, hmrNonComplianceMap,
						userProtocolConstantsMap, complianceMap, notificationMap);
			
			calculateMissedTherapy(today, mstNotificationMap,
					hmrNonComplianceMap, complianceMap, notificationMap);
			
			updateNotificationsOnMST(today, notificationMap);
			updateComplianceForMST(today, complianceMap);
			log.debug("Started calculating missed therapy "+DateTime.now()+","+today);
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotificationMonarch("processMissedTherapySessionsMonarch",writer.toString());
		}
	}
	
	private void processForEachPatientMonarch(PatientComplianceMonarch compliance, Map<Long,PatientNoEventMonarch> userIdNoEventMap, 
			Map<Long,PatientComplianceMonarch> complianceMap, Map<Long,PatientComplianceMonarch> mstNotificationMap,
			Map<Long,PatientComplianceMonarch> hmrNonComplianceMap){
		
		LocalDate today = LocalDate.now();
		Long userId = compliance.getPatientUser().getId();
		PatientInfo patientInfo = compliance.getPatient();
		
		Integer adherenceSettingDay = getAdherenceSettingForPatient(patientInfo);
		
		PatientNoEventMonarch noEvent = userIdNoEventMap.get(compliance.getPatientUser().getId());
		
		//global counters
		int globalMissedTherapyCounter = compliance.getGlobalMissedTherapyCounter();
		int globalHMRNonAdherenceCounter = compliance.getGlobalHMRNonAdherenceCounter();
		int globalSettingsDeviationCounter = compliance.getGlobalSettingsDeviationCounter();
		// For No transmission users , compliance shouldn't be updated until transmission happens
		
		if(Objects.nonNull(noEvent) && (Objects.isNull(noEvent.getFirstTransmissionDate()))){
			PatientComplianceMonarch newCompliance = new PatientComplianceMonarch(compliance.getScore(), today,
					compliance.getPatient(), compliance.getPatientUser(),compliance.getHmrRunRate(),true,
					false,0);
			newCompliance.setLatestTherapyDate(null);// since no transmission 
			complianceMap.put(userId, newCompliance);
			// HMR Compliance shouldn't be checked for Patients for initial adherence setting days of transmission date
		}else if(Objects.nonNull(noEvent)&& (Objects.nonNull(noEvent.getFirstTransmissionDate()) && 
				DateUtil.getDaysCountBetweenLocalDates(noEvent.getFirstTransmissionDate(), today) < (adherenceSettingDay-1) &&
				adherenceSettingDay > 1 )){
			// For Transmitted users no notification till earlier day of adherence Setting day(s)
			PatientComplianceMonarch newCompliance = new PatientComplianceMonarch(today,compliance.getPatient(),compliance.getPatientUser(),
					compliance.getHmrRunRate(),compliance.getMissedTherapyCount()+1,compliance.getLatestTherapyDate(),
					Objects.nonNull(compliance.getHmr())? compliance.getHmr():0.0d);
			newCompliance.setScore(compliance.getScore());
			updateGlobalCounters(++globalMissedTherapyCounter,
					globalHMRNonAdherenceCounter,
					globalSettingsDeviationCounter, newCompliance);
			complianceMap.put(userId, newCompliance);
		}else {
			PatientComplianceMonarch newCompliance = new PatientComplianceMonarch(
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
			if(newCompliance.getMissedTherapyCount() >= adherenceSettingDay){ // missed Therapy for adherenceSetting day(s) or more than adherenceSetting day(s) days
				mstNotificationMap.put(compliance.getPatientUser().getId(), newCompliance);
			}else{ // missed therapy for less than adherence setting day(s) , might fall under hmrNonCompliance
				hmrNonComplianceMap.put(compliance.getPatientUser().getId(), newCompliance);
			}
		}
	}
	
	private void processForEachPatientBoth(PatientComplianceMonarch compliance, Map<Long,PatientNoEventMonarch> userIdNoEventMap, 
			Map<Long,PatientNoEvent> userIdNoEventMapVest, Map<Long,PatientComplianceMonarch> complianceMap, 
			Map<Long,PatientComplianceMonarch> mstNotificationMap, Map<Long,PatientComplianceMonarch> hmrNonComplianceMap){
		
		LocalDate today = LocalDate.now();
		Long userId = compliance.getPatientUser().getId();
		PatientInfo patientInfo = compliance.getPatient();
		
		Integer adherenceSettingDay = getAdherenceSettingForPatient(patientInfo);
		
		PatientNoEventMonarch noEvent = userIdNoEventMap.get(compliance.getPatientUser().getId());
		PatientNoEvent noEventVest = userIdNoEventMapVest.get(compliance.getPatientUser().getId());
		
		//global counters
		int globalMissedTherapyCounter = compliance.getGlobalMissedTherapyCounter();
		int globalHMRNonAdherenceCounter = compliance.getGlobalHMRNonAdherenceCounter();
		int globalSettingsDeviationCounter = compliance.getGlobalSettingsDeviationCounter();
		
		LocalDate firstTransmissionDateMonarch = (Objects.nonNull(noEvent) && Objects.nonNull(noEvent.getFirstTransmissionDate())) ? 
														noEvent.getFirstTransmissionDate() : null; 
		
		LocalDate firstTransmissionDateVest = (Objects.nonNull(noEventVest) && Objects.nonNull(noEventVest.getFirstTransmissionDate())) ? 
														noEventVest.getFirstTransmissionDate() : null;

		LocalDate firstTransmissionVestOrMonarch = null; 
		if( Objects.nonNull(firstTransmissionDateVest) && Objects.nonNull(firstTransmissionDateMonarch)){
			firstTransmissionVestOrMonarch = firstTransmissionDateVest.isBefore(firstTransmissionDateMonarch) ? 
														firstTransmissionDateVest : firstTransmissionDateMonarch;  
		}else if(Objects.isNull(firstTransmissionDateVest)){
			firstTransmissionVestOrMonarch = firstTransmissionDateMonarch;
		}else{
			firstTransmissionVestOrMonarch = firstTransmissionDateVest;
		}
		
		// For No transmission users , compliance shouldn't be updated until transmission happens		
		if( Objects.isNull(firstTransmissionDateVest) && Objects.isNull(firstTransmissionDateMonarch)){
			PatientComplianceMonarch newCompliance = new PatientComplianceMonarch(compliance.getScore(), today,
					compliance.getPatient(), compliance.getPatientUser(),compliance.getHmrRunRate(),true,
					false,0);
			newCompliance.setLatestTherapyDate(null);// since no transmission 
			complianceMap.put(userId, newCompliance);
			// HMR Compliance shouldn't be checked for Patients for initial adherence setting days of transmission date
		}else if(Objects.nonNull(firstTransmissionVestOrMonarch) && 
				DateUtil.getDaysCountBetweenLocalDates(firstTransmissionVestOrMonarch, today) < (adherenceSettingDay-1) &&
				adherenceSettingDay > 1 ){
			// For Transmitted users no notification till earlier day of adherence Setting day(s)
			PatientComplianceMonarch newCompliance = new PatientComplianceMonarch(today,compliance.getPatient(),compliance.getPatientUser(),
					compliance.getHmrRunRate(),compliance.getMissedTherapyCount()+1,compliance.getLatestTherapyDate(),
					Objects.nonNull(compliance.getHmr())? compliance.getHmr():0.0d);
			newCompliance.setScore(compliance.getScore());
			updateGlobalCounters(++globalMissedTherapyCounter,
					globalHMRNonAdherenceCounter,
					globalSettingsDeviationCounter, newCompliance);
			complianceMap.put(userId, newCompliance);
		}else {
			PatientComplianceMonarch newCompliance = new PatientComplianceMonarch(
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
			if(newCompliance.getMissedTherapyCount() >= adherenceSettingDay){ // missed Therapy for adherenceSetting day(s) or more than adherenceSetting day(s) days
				mstNotificationMap.put(compliance.getPatientUser().getId(), newCompliance);
			}else{ // missed therapy for less than adherence setting day(s) , might fall under hmrNonCompliance
				hmrNonComplianceMap.put(compliance.getPatientUser().getId(), newCompliance);
			}
		}
	}
	
	// Resetting the adherence score for the specific user from the adherence reset start date
	/*@Async	
	public Future<String> adherenceSettingForClinic(String clinicId){
		try{
			long startTime = System.currentTimeMillis();
			
			//List<PatientInfo> patientList = clinicPatientService.getPatientListForClinic(clinicId);
			List<User> userList = clinicPatientService.getUserListForClinic(clinicId);
			List<Long> userIdList = clinicPatientService.getUserIdListFromUserList(userList);
			
			Map<Long,PatientNoEventMonarch> userIdNoEventMap = noEventMonarchService.findAllByPatientUserId(userIdList);
			
			if(userList.size()>0){
				for(User user : userList){
					//User user = userService.getUserObjFromPatientInfo(patient);
					LocalDate startDate = fineOneByPatientUserIdLatestResetStartDate(user.getId());
					
					if(Objects.isNull(startDate)){				
						PatientNoEventMonarch noEvent = userIdNoEventMap.get(user.getId());
						if(Objects.nonNull(noEvent) &&  Objects.nonNull(noEvent.getFirstTransmissionDate())){
							startDate = noEvent.getFirstTransmissionDate();
						}
					}
					if(Objects.nonNull(startDate)){
						PatientInfo patient = userService.getPatientInfoObjFromPatientUser(user);
						
						String deviceType = adherenceCalculationService.getDeviceTypeValue(patient.getId());
						
						if(deviceType == "MONARCH"){
							adherenceResetForPatient(user.getId(), patient.getId(), startDate, DEFAULT_COMPLIANCE_SCORE, 0);
						}else if(deviceType == "BOTH"){							
							PatientNoEventMonarch noEventMonarch = noEventMonarchService.findByPatientUserId(user.getId());
							adherenceCalculationBoth(user.getId(), patient.getId(), startDate, 
									noEventMonarch.getFirstTransmissionDate(), DEFAULT_COMPLIANCE_SCORE, user.getId(), 0);
						}
					}
				}
				long endTime   = System.currentTimeMillis();
				long totalTime = endTime - startTime;
				log.error("adherenceSettingForClinic method executed in :"+totalTime+" milliseconds");
				return new AsyncResult<>(MessageConstants.HR_314);
				//return new AsyncResult<>("Adherence Score recalculated initiated, will be completed soon");
			}else{
				long endTime   = System.currentTimeMillis();
				long totalTime = endTime - startTime;				
				log.error("adherenceSettingForClinic method executed in :"+totalTime+" milliseconds");
				return new AsyncResult<>(MessageConstants.HR_315);				
			}
		}catch(Exception ex){
			log.debug(ex.getMessage());
		}
		return new AsyncResult<>("Adherence score recalculated successfully for all patients under clinic");
	}*/
	
	public LocalDate fineOneByPatientUserIdLatestResetStartDate(Long userId){    	
    	List<AdherenceResetMonarch> adherenceReset = adherenceResetMonarchRepository.findOneByPatientUserIdLatestResetStartDate(userId);
    	if(adherenceReset.size() > 0)
    		return adherenceReset.get(0).getResetStartDate();
    	else
    		return null;
    }
	
	// Getting the therapy data for the requested date
	public List<TherapySessionMonarch> getTherapyForDay(SortedMap<LocalDate,List<TherapySessionMonarch>> sortedTherapySession,LocalDate curDate){		
		return sortedTherapySession.get(curDate);
	}
	
	// Getting the therapy data for the requested between period 
	public List<TherapySessionMonarch> getTherapyforBetweenDates(LocalDate fromDate, LocalDate toDate, SortedMap<LocalDate,List<TherapySessionMonarch>> sortedTherapySession){
		List<TherapySessionMonarch> session = new LinkedList<>();
		
		List<LocalDate> allDates = DateUtil.getAllLocalDatesBetweenDates(fromDate, toDate);
		
		for(LocalDate date : allDates){
			
			if(Objects.nonNull(sortedTherapySession.get(date)))
				session.addAll(sortedTherapySession.get(date));
		}
		
		return session;
	}
	
	// Getting the compliance object for the previous date / previous record 
	public PatientComplianceMonarch returnPrevDayCompli(List<PatientComplianceMonarch> complianceList, LocalDate currDate){
		
		int j = -1;
		for(int i = 0; i <= (complianceList.size()-1); i++){
			if(complianceList.get(i).getDate() == currDate){				
				j = i;
			}
		}
		return (j > 0 ? complianceList.get(j-1) : null);
	}
	
	// Grouping the therapy data by date 
	public SortedMap<LocalDate,List<TherapySessionMonarch>> groupTherapySessionsByDate(List<TherapySessionMonarch> therapySessions){
		return new TreeMap<>(therapySessions.stream().collect(Collectors.groupingBy(TherapySessionMonarch :: getDate)));
	}
	
	// Resetting the adherence score for the specific user from the adherence reset start date	
	public String adherenceResetForPatient(Long userId, String patientId, LocalDate adherenceStartDate, Integer adherenceScore, Integer resetFlag){
		try{
			
			// Adherence Start date in string for query
			String sAdherenceStDate = adherenceStartDate.toString();
			
			LocalDate todayDate = LocalDate.now();
			LocalDate prevDate = DateUtil.getPlusOrMinusTodayLocalDate(-1);
			
			LocalDate prevAdherenceStartDate = DateUtil.getPlusOrMinusDate(-1, adherenceStartDate);
			String prevAdherenceStartDateString = prevAdherenceStartDate.toString();
			
			// Get the list of rows for the user id from the adherence reset start date 
			List<PatientComplianceMonarch> patientComplianceList = patientComplianceMonarchRepository.returnComplianceForPatientIdDates((resetFlag == 3 ? prevAdherenceStartDateString : sAdherenceStDate), userId);
			
			List<PatientComplianceMonarch> complianceListToStore = new LinkedList<>();
			
			// Getting the protocol constants for the user
			ProtocolConstantsMonarch userProtocolConstant = protocolMonarchService.getProtocolForPatientUserId(userId);
			
			// Getting all the sessions of user from the repository 
			List<TherapySessionMonarch> therapySessionData = therapySessionMonarchRepository.findByPatientUserId(userId);
			// grouping all the therapy sessions to the date
			SortedMap<LocalDate,List<TherapySessionMonarch>> sortedTherapy = groupTherapySessionsByDate(therapySessionData);
			
			// Getting all the notification for the user 
			List<NotificationMonarch> userNotifications = notificationMonarchRepository.findByPatientUserId(userId);			
			
			int adherenceSettingDay = getAdherenceSettingForUserId(userId);
			
			for(PatientComplianceMonarch currentCompliance : patientComplianceList){
				
				if(resetFlag == 1 || resetFlag == 2 || (resetFlag == 3 && !currentCompliance.getDate().isBefore(adherenceStartDate))){
				
					PatientInfo patient = currentCompliance.getPatient();
					User patientUser = currentCompliance.getPatientUser();
					int initialPrevScoreFor1Day = 0;
					
					NotificationMonarch existingNotificationofTheDay = notificationMonarchService.getNotificationForDay(userNotifications, currentCompliance.getDate());
					
					if( ( adherenceStartDate.isBefore(currentCompliance.getDate()) || adherenceStartDate.equals(currentCompliance.getDate())) &&
							DateUtil.getDaysCountBetweenLocalDates(adherenceStartDate, currentCompliance.getDate()) < (adherenceSettingDay-1) && 
							adherenceSettingDay > 1 && resetFlag != 3){
								
						// Check whether the adherence start days is the compliance date
						if(adherenceStartDate.equals(currentCompliance.getDate())){
							if(resetFlag == 1 || (resetFlag == 2 && adherenceSettingDay != 1)){							 
								notificationMonarchService.createOrUpdateNotification(patientUser, patient, userId,
																			currentCompliance.getDate(), ADHERENCE_SCORE_RESET, false, existingNotificationofTheDay);
							}else{
								if(Objects.nonNull(existingNotificationofTheDay))
									notificationMonarchRepository.delete(existingNotificationofTheDay);
							}
							currentCompliance.setSettingsDeviatedDaysCount(0);
							currentCompliance.setMissedTherapyCount(0);
						} else {
							
							// Commenting the existing repository call and calling the the new method for getting the previous day compliance
							
							PatientComplianceMonarch prevCompliance = returnPrevDayCompli(patientComplianceList, currentCompliance.getDate());
							if(Objects.isNull(prevCompliance)){
								prevCompliance = new PatientComplianceMonarch();
								prevCompliance.setScore(adherenceScore);
								prevCompliance.setSettingsDeviatedDaysCount(0);
								prevCompliance.setMissedTherapyCount(0);
							}
							
							if(currentCompliance.getMissedTherapyCount() > 0){
								currentCompliance.setMissedTherapyCount(prevCompliance.getMissedTherapyCount()+1);
							}
							if(isSettingDeviatedForUserOnDay(userId, currentCompliance.getDate() ,adherenceSettingDay, userProtocolConstant)){
								currentCompliance.setSettingsDeviatedDaysCount(prevCompliance.getSettingsDeviatedDaysCount()+1);
							}
							
							// Commenting the existing repository call and calling the new method for getting the current day notification at beginning of for loop
							
							
							if(Objects.nonNull(existingNotificationofTheDay)){
								notificationMonarchRepository.delete(existingNotificationofTheDay);
							}
						}
						currentCompliance.setScore(adherenceScore);
						
						complianceListToStore.add(currentCompliance);
					}else{
						
						
						PatientComplianceMonarch prevCompliance = returnPrevDayCompli(patientComplianceList, currentCompliance.getDate());
						if(Objects.isNull(prevCompliance)){
							prevCompliance = new PatientComplianceMonarch();
							prevCompliance.setScore(adherenceScore);
							prevCompliance.setSettingsDeviatedDaysCount(0);
							prevCompliance.setMissedTherapyCount(0);
						}
						
						// Commenting the existing repository call and calling the the new method for getting the current day therapy details				
										
						List<TherapySessionMonarch> therapyData = getTherapyForDay(sortedTherapy, currentCompliance.getDate());
						
						if(adherenceSettingDay == 1 && adherenceStartDate.equals(currentCompliance.getDate())){
							initialPrevScoreFor1Day = adherenceScore;
						}						
						if(currentCompliance.getMissedTherapyCount() >= adherenceSettingDay && !currentCompliance.getDate().equals(todayDate)){
							// Adding the prevCompliance object for previous day compliance and existingNotificationofTheDay object for the current date Notification object
							// Missed therapy days
							complianceListToStore.add(calculateUserMissedTherapy(currentCompliance,currentCompliance.getDate(), userId, patient, patientUser, initialPrevScoreFor1Day, prevCompliance, existingNotificationofTheDay));
						}else if( ( Objects.isNull(therapyData) || (Objects.nonNull(therapyData) && therapyData.isEmpty()) ) && currentCompliance.getDate().equals(todayDate)){
							// Passing prevCompliance for avoiding the repository call to retrieve the previous day compliance
							// Setting the previous day compliance details for the no therapy done for today 
							complianceListToStore.add(setPrevDayCompliance(currentCompliance, userId, prevCompliance));
						}else{
							// Adding the sortedTherapy for all the therapies & prevCompliance object for previous day compliance and existingNotificationofTheDay object for the current date Notification object
							// HMR Non Compliance / Setting deviation & therapy data available
							complianceListToStore.add(calculateUserHMRComplianceForMST(currentCompliance, userProtocolConstant, currentCompliance.getDate(), userId, 
									patient, patientUser, adherenceSettingDay, initialPrevScoreFor1Day,sortedTherapy, prevCompliance, existingNotificationofTheDay, resetFlag));
						}
					}
				}
			}
			complianceMonarchService.saveAll(complianceListToStore);
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotificationMonarch("resetAdherenceCalculationPatientMonarch",writer.toString());
		}
		return "Adherence score reset successfully";
	}

	public String adherenceCalculationBoth(Long userId, String patientId, LocalDate adherenceStartDate, 
			LocalDate firstTransmissionOfOldDevice, Integer adherenceScore, Long oldUserId, Integer flag){
		// flag 1 - for adherence reset
		// flag 2 - for adherence setting / window
		// flag 3 - vest ingestion
		// flag 4 - monarch ingestion		
		
		try{			
			// Adherence Start date in string for query
			String sAdherenceStDate = adherenceStartDate.toString();
			
			LocalDate todayDate = LocalDate.now();
			
			LocalDate prevAdherenceStartDate = DateUtil.getPlusOrMinusDate(-1, adherenceStartDate);
			String prevAdherenceStartDateString = prevAdherenceStartDate.toString();
						
			// Get the list of rows for the user id from the adherence reset start date 
			List<PatientComplianceMonarch> patientComplianceList = patientComplianceMonarchRepository.returnComplianceForPatientIdDates((flag == 3 || flag == 4 ? prevAdherenceStartDateString : sAdherenceStDate), userId);
			
			List<PatientComplianceMonarch> complianceListToStore = new LinkedList<>();
			
			// Getting the protocol constants for the user
			ProtocolConstantsMonarch userProtocolConstant = protocolMonarchService.getProtocolForPatientUserId(oldUserId);
			
			// Getting all the sessions of user from the repository 
			List<TherapySessionMonarch> therapySessionData = therapySessionMonarchRepository.findByPatientUserId(oldUserId);
			// grouping all the therapy sessions to the date
			SortedMap<LocalDate,List<TherapySessionMonarch>> sortedTherapy = groupTherapySessionsByDate(therapySessionData);
			
			// Getting all the notification for the user 
			List<NotificationMonarch> userNotifications = notificationMonarchRepository.findByPatientUserId(userId);			
			
			
			// Getting the protocol constants for the user for vest
			ProtocolConstants userProtocolConstantVest = protocolVestService.getProtocolForPatientUserId(userId);
			
			// Getting all the sessions of user from the repository for vest 
			List<TherapySession> therapySessionDataVest = therapySessionRepository.findByPatientUserId(userId);
			// grouping all the therapy sessions to the date for vest
			SortedMap<LocalDate,List<TherapySession>> sortedTherapyVest = adherenceCalculationService.groupTherapySessionsByDate(therapySessionDataVest);
			
			
			int adherenceSettingDay = getAdherenceSettingForUserId(userId);
			
			for(PatientComplianceMonarch currentCompliance : patientComplianceList){
				
				if(!currentCompliance.getDate().isBefore(adherenceStartDate)){
				
					PatientInfo patient = currentCompliance.getPatient();
					User patientUser = currentCompliance.getPatientUser();
					int initialPrevScoreFor1Day = 0;
					
					NotificationMonarch existingNotificationofTheDay = notificationMonarchService.getNotificationForDay(userNotifications, currentCompliance.getDate());						
					
					if( ( adherenceStartDate.isBefore(currentCompliance.getDate()) || adherenceStartDate.equals(currentCompliance.getDate())) &&
								DateUtil.getDaysCountBetweenLocalDates(adherenceStartDate, currentCompliance.getDate()) < (adherenceSettingDay-1) && 
								adherenceSettingDay > 1 && (flag != 3 && flag != 4)){		
						
						if(adherenceStartDate.equals(currentCompliance.getDate())){
							
							if(flag == 1 || (flag == 2 && adherenceSettingDay != 1)){
								notificationMonarchService.createOrUpdateNotification(patientUser, patient, userId,
										currentCompliance.getDate(), ADHERENCE_SCORE_RESET, false, existingNotificationofTheDay);
							}else{
								if(Objects.nonNull(existingNotificationofTheDay))
									notificationMonarchRepository.delete(existingNotificationofTheDay);
							}
							
							currentCompliance.setSettingsDeviatedDaysCount(0);
							currentCompliance.setMissedTherapyCount(0);
						} else {							
							PatientComplianceMonarch prevCompliance = returnPrevDayCompli(patientComplianceList, currentCompliance.getDate());
							if(Objects.isNull(prevCompliance)){
								prevCompliance = new PatientComplianceMonarch();
								prevCompliance.setScore(adherenceScore);
								prevCompliance.setSettingsDeviatedDaysCount(0);
								prevCompliance.setMissedTherapyCount(0);
							}
							
							if(currentCompliance.getMissedTherapyCount() > 0){
								currentCompliance.setMissedTherapyCount(prevCompliance.getMissedTherapyCount()+1);
							}
							if(isSettingDeviatedForUserOnDay(userId, currentCompliance.getDate() ,adherenceSettingDay, userProtocolConstant)){
								currentCompliance.setSettingsDeviatedDaysCount(prevCompliance.getSettingsDeviatedDaysCount()+1);
							}
							
							// Commenting the existing repository call and calling the new method for getting the current day notification at beginning of for loop
							
							
							if(Objects.nonNull(existingNotificationofTheDay)){
								notificationMonarchRepository.delete(existingNotificationofTheDay);
							}
						}
						currentCompliance.setScore(adherenceScore);						
						complianceListToStore.add(currentCompliance);
					}else{
				
						if((flag != 3 && flag != 4) || ((flag == 3 || flag == 4) && 
								(DateUtil.getDaysCountBetweenLocalDates(adherenceStartDate, currentCompliance.getDate()) > (adherenceSettingDay-1) || 
								DateUtil.getDaysCountBetweenLocalDates(firstTransmissionOfOldDevice, currentCompliance.getDate()) > (adherenceSettingDay-1)))){
							
							PatientComplianceMonarch prevCompliance = returnPrevDayCompli(patientComplianceList, currentCompliance.getDate());
							if(Objects.isNull(prevCompliance)){
								prevCompliance = new PatientComplianceMonarch();
								prevCompliance.setScore(adherenceScore);
								prevCompliance.setSettingsDeviatedDaysCount(0);
								prevCompliance.setMissedTherapyCount(0);
							}
							
							List<TherapySessionMonarch> therapyData = new LinkedList<>();
							List<TherapySession> therapyDataVest = new LinkedList<>();
							
							therapyData = getTherapyForDay(sortedTherapy, currentCompliance.getDate());
							therapyDataVest = adherenceCalculationService.getTherapyForDay(sortedTherapyVest, currentCompliance.getDate());
							
							if((flag != 3 && flag != 4) && adherenceSettingDay == 1 && adherenceStartDate.equals(currentCompliance.getDate())){
								initialPrevScoreFor1Day = adherenceScore;
							}
							if(prevCompliance.getMissedTherapyCount() >= (adherenceSettingDay-1) && 
									currentCompliance.getMissedTherapyCount() >= adherenceSettingDay && 
									!currentCompliance.getDate().equals(todayDate) && 
									(Objects.isNull(therapyDataVest) || therapyDataVest.isEmpty()) && 
									(Objects.isNull(therapyData) || therapyData.isEmpty()) ){							
								// Adding the prevCompliance object for previous day compliance and existingNotificationofTheDay object for the current date Notification object
								// Missed therapy days
								complianceListToStore.add(calculateUserMissedTherapy(currentCompliance,currentCompliance.getDate(), userId, patient, 
										patientUser, initialPrevScoreFor1Day, prevCompliance, existingNotificationofTheDay));
							}else if( (Objects.isNull(therapyData) || therapyData.isEmpty()) && 
									(Objects.isNull(therapyDataVest) || therapyDataVest.isEmpty()) && 
									currentCompliance.getDate().equals(todayDate)){						
								// Passing prevCompliance for avoiding the repository call to retrieve the previous day compliance
								// Setting the previous day compliance details for the no therapy done for today 
								complianceListToStore.add(setPrevDayCompliance(currentCompliance, userId, prevCompliance));
							}else{
								// Adding the sortedTherapy for all the therapies & prevCompliance object for previous day compliance and existingNotificationofTheDay object for the current date Notification object
								// HMR Non Compliance / Setting deviation & therapy data available
								complianceListToStore.add(calculateUserHMRComplianceForMSTBoth(currentCompliance, userProtocolConstant, 
										userProtocolConstantVest, currentCompliance.getDate(), userId,patient, patientUser, adherenceSettingDay, 
										initialPrevScoreFor1Day,sortedTherapy, sortedTherapyVest, prevCompliance, existingNotificationofTheDay, flag));
							}
						
						}
					}
				
				}
			}
			complianceMonarchService.saveAll(complianceListToStore);
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotificationMonarch("adherenceCalculationBoth",writer.toString());
		}
		return "Adherence score reset successfully";
		
	}
	
	// Setting the previous day compliance
	
	private PatientComplianceMonarch setPrevDayCompliance(PatientComplianceMonarch currentCompliance, Long userId, PatientComplianceMonarch preDayCompliance)
	{
		// Commented the repository call and getting the previous day compliance from the method parameter
		
		
		currentCompliance.setGlobalHMRNonAdherenceCounter(preDayCompliance.getGlobalHMRNonAdherenceCounter());
		currentCompliance.setGlobalMissedTherapyCounter(preDayCompliance.getGlobalMissedTherapyCounter());
		currentCompliance.setGlobalSettingsDeviationCounter(preDayCompliance.getGlobalSettingsDeviationCounter());
		currentCompliance.setHmr(preDayCompliance.getHmr());
		currentCompliance.setHmrCompliant(preDayCompliance.isHmrCompliant());
		currentCompliance.setHmrRunRate(preDayCompliance.getHmrRunRate());
		currentCompliance.setLatestTherapyDate(preDayCompliance.getLatestTherapyDate());
		currentCompliance.setMissedTherapyCount(preDayCompliance.getMissedTherapyCount());
		currentCompliance.setScore(preDayCompliance.getScore());
		currentCompliance.setSettingsDeviated(preDayCompliance.isSettingsDeviated());
		currentCompliance.setSettingsDeviatedDaysCount(preDayCompliance.getSettingsDeviatedDaysCount());
		
		
		return currentCompliance;
		
	}
	
	private void updateGlobalCounters(int globalMissedTherapyCounter,
			int globalHMRNonAdherenceCounter,
			int globalSettingsDeviationCounter, PatientComplianceMonarch newCompliance) {
		newCompliance.setGlobalMissedTherapyCounter(globalMissedTherapyCounter);
		newCompliance.setGlobalHMRNonAdherenceCounter(globalHMRNonAdherenceCounter);
		newCompliance.setGlobalSettingsDeviationCounter(globalSettingsDeviationCounter);
	}

	// Create or Update Adherence Score on Missed Therapy day
	private void updateComplianceForMST(LocalDate today,
			Map<Long, PatientComplianceMonarch> complianceMap) {
		Map<Long,List<PatientComplianceMonarch>> existingCompliances = complianceMonarchService.getPatientComplainceMapByPatientUserId(new LinkedList<>(complianceMap.keySet()),today,today);
		if(existingCompliances.isEmpty()){
			complianceMonarchService.saveAll(complianceMap.values());
		}else{
			for(Long puId : existingCompliances.keySet()){
				List<PatientComplianceMonarch> complianceForDay = existingCompliances.get(puId);
				if(complianceForDay.size() > 0){
					PatientComplianceMonarch existingCompliance = complianceForDay.get(0);
					PatientComplianceMonarch currentCompliance = complianceMap.get(puId);
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
			complianceMonarchService.saveAll(complianceMap.values());
		}
	}

	// Create or Update notifications on Missed Therapy
	private void updateNotificationsOnMST(LocalDate today,
			Map<Long, NotificationMonarch> notificationMap) {
		Map<Long,List<NotificationMonarch>> existingNotifications = notificationMonarchService.getNotificationMapByPatientIdsAndDate(new LinkedList<>(notificationMap.keySet()), today, today);
		if(existingNotifications.isEmpty()){
			notificationMonarchService.saveAll(notificationMap.values());
		}else{
			for(Long puId : existingNotifications.keySet()){
				List<NotificationMonarch> notificationsforDay = existingNotifications.get(puId);
				if(notificationsforDay.size() > 0){
					NotificationMonarch existingNotification = notificationsforDay.get(0);
					NotificationMonarch currentNotification = notificationMap.get(puId);
					existingNotification.setNotificationType(currentNotification.getNotificationType());
					notificationMap.put(puId, existingNotification);
				} 
			}
			notificationMonarchService.saveAll(notificationMap.values());
		}
	}

	// calculate missed therapies and points
	private void calculateMissedTherapy(LocalDate today,
			Map<Long, PatientComplianceMonarch> mstNotificationMap,
			Map<Long, PatientComplianceMonarch> hmrNonComplianceMap,
			Map<Long, PatientComplianceMonarch> complianceMap,
			Map<Long, NotificationMonarch> notificationMap) {
		for(Long patientUserId : mstNotificationMap.keySet()){
			PatientComplianceMonarch newCompliance = mstNotificationMap.get(patientUserId);
			int score = newCompliance.getScore();
			score = score < MISSED_THERAPY_POINTS ? 0 :  score - MISSED_THERAPY_POINTS ;
			notificationMap.put(patientUserId, new NotificationMonarch(MISSED_THERAPY,today,newCompliance.getPatientUser(), newCompliance.getPatient(),false));
			newCompliance.setHmrCompliant(false);
			newCompliance.setScore(score);
			newCompliance.setHmrRunRate(0);
			complianceMap.put(patientUserId, newCompliance);
		}
	}

	// calculate HMRCompliance on Missed Therapy Date
	private void calculateHMRComplianceForMST(LocalDate today,
			Map<Long, PatientComplianceMonarch> hmrNonComplianceMap,
			Map<Long, ProtocolConstantsMonarch> userProtocolConstantsMap,
			Map<Long, PatientComplianceMonarch> complianceMap,
			Map<Long, NotificationMonarch> notificationMap) {
		for(Long patientUserId : hmrNonComplianceMap.keySet()){
			PatientComplianceMonarch newCompliance = hmrNonComplianceMap.get(patientUserId);
			
			int score = newCompliance.getScore();
			int adherenceSettingDay = getAdherenceSettingForUserId(patientUserId);
			List<TherapySessionMonarch> therapySessions = getLastSettingDaysTherapiesForUserId(patientUserId,getPlusOrMinusTodayLocalDate(-(adherenceSettingDay-1)),today); 
			
			if(Objects.isNull(therapySessions)){
				therapySessions = new LinkedList<>();
			}
			
			int hmrRunRate = calculateHMRRunRatePerSession(therapySessions);
			newCompliance.setHmrRunRate(hmrRunRate);
			double durationForSettingDays = hmrRunRate*therapySessions.size(); // runrate*totalsessions = total duration
			ProtocolConstantsMonarch protocolConstant = userProtocolConstantsMap.get(patientUserId);

			if(!isHMRCompliant(protocolConstant, durationForSettingDays, adherenceSettingDay)){
				score = score < HMR_NON_COMPLIANCE_POINTS ? 0 : score - HMR_NON_COMPLIANCE_POINTS;
				newCompliance.setHmrCompliant(false);
				// increment HMR Non Adherence Counter
				int globalHMRNonAdherenceCounter = newCompliance.getGlobalHMRNonAdherenceCounter();
				newCompliance.setGlobalHMRNonAdherenceCounter(++globalHMRNonAdherenceCounter);
				notificationMap.put(patientUserId, new NotificationMonarch(HMR_NON_COMPLIANCE,today,newCompliance.getPatientUser(), newCompliance.getPatient(),false));
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
	
	// calculate HMRCompliance on Missed Therapy Date
		private void calculateHMRComplianceForMSTBoth(LocalDate today,
				Map<Long, PatientComplianceMonarch> hmrNonComplianceMap,
				Map<Long, ProtocolConstantsMonarch> userProtocolConstantsMap,
				Map<Long, ProtocolConstants> userProtocolConstantsVestMap,
				Map<Long, PatientComplianceMonarch> complianceMap,
				Map<Long, NotificationMonarch> notificationMap) {
			
			for(Long patientUserId : hmrNonComplianceMap.keySet()){
				
				PatientComplianceMonarch newCompliance = hmrNonComplianceMap.get(patientUserId);
				
				int score = newCompliance.getScore();
				int adherenceSettingDay = getAdherenceSettingForUserId(patientUserId);
				List<TherapySessionMonarch> therapySessions = getLastSettingDaysTherapiesForUserId(patientUserId,getPlusOrMinusTodayLocalDate(-(adherenceSettingDay-1)),today); 
				
				List<TherapySession> therapySessionsVest = adherenceCalculationService.getLastSettingDaysTherapiesForUserId(patientUserId,getPlusOrMinusTodayLocalDate(-(adherenceSettingDay-1)),today);
				
				if(Objects.isNull(therapySessions)){
					therapySessions = new LinkedList<>();
				}
				
				if(Objects.isNull(therapySessionsVest)){
					therapySessionsVest = new LinkedList<>();
				}
				
				int hmrRunRateVest = calculateHMRRunRatePerSession(therapySessionsVest);
				int hmrRunRateMonarch = calculateHMRRunRatePerSession(therapySessions);
				
				int hmrRunRateBoth = calculateHMRRunRatePerSessionBoth(therapySessions, therapySessionsVest);
				newCompliance.setHmrRunRate(hmrRunRateBoth);
				
				double durationForSettingDaysMonarch = hmrRunRateMonarch*therapySessions.size(); // runrate*totalsessions = total duration
				double durationForSettingDaysVest = hmrRunRateVest*therapySessions.size(); // runrate*totalsessions = total duration
				
				ProtocolConstantsMonarch protocolConstant = userProtocolConstantsMap.get(patientUserId);
				ProtocolConstants protocolConstantVest = userProtocolConstantsVestMap.get(patientUserId);

				boolean isHmrCompliantVest = true; 
				boolean isHmrCompliantMonarch = true; 
				if(!therapySessionsVest.isEmpty()){
					isHmrCompliantVest = adherenceCalculationService.isHMRCompliant(protocolConstantVest, durationForSettingDaysVest, adherenceSettingDay);
				}
				if(!therapySessions.isEmpty()){
					isHmrCompliantMonarch = isHMRCompliant(protocolConstant, durationForSettingDaysMonarch, adherenceSettingDay);
				}
				
				if(!isHmrCompliantVest || !isHmrCompliantMonarch){
					score = score < HMR_NON_COMPLIANCE_POINTS ? 0 : score - HMR_NON_COMPLIANCE_POINTS;
					newCompliance.setHmrCompliant(false);
					// increment HMR Non Adherence Counter
					int globalHMRNonAdherenceCounter = newCompliance.getGlobalHMRNonAdherenceCounter();
					newCompliance.setGlobalHMRNonAdherenceCounter(++globalHMRNonAdherenceCounter);
					
					String notificationType = HMR_NON_COMPLIANCE_MONARCH;
					if(!isHmrCompliantVest){
						if(!isHmrCompliantMonarch)
							notificationType = HMR_NON_COMPLIANCE;
						else 
							notificationType = HMR_NON_COMPLIANCE_VEST;
					}
					notificationMap.put(patientUserId, new NotificationMonarch(notificationType,today,newCompliance.getPatientUser(), newCompliance.getPatient(),false));
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
	//private void calculateUserHMRComplianceForMST(
	private PatientComplianceMonarch calculateUserHMRComplianceForMST(
			PatientComplianceMonarch newCompliance,
			ProtocolConstantsMonarch userProtocolConstants,
			LocalDate complianceDate,
			Long userId,
			PatientInfo patient,
			User patientUser,
			Integer adherenceSettingDay,
			int initialPrevScoreFor1Day,
			SortedMap<LocalDate,List<TherapySessionMonarch>> sortedTherapy,
			PatientComplianceMonarch prevCompliance,
			NotificationMonarch existingNotificationofTheDay,
			Integer resetFlag) {
		
		// Commented the repository call for previous day compliance and getting the prevCompliance from the method parameter
		// Getting previous day score or adherence reset score for the adherence setting value as 1
		
		int score = initialPrevScoreFor1Day == 0 ? prevCompliance.getScore() : initialPrevScoreFor1Day;
		
		// Get earlier third day to finding therapy session
		LocalDate adherenceSettingDaysEarlyDate = getDateBeforeSpecificDays(complianceDate,(adherenceSettingDay-1));
		
		//Commented the repository call for current day therapy session and getting the same from the method parameter and the calling method
		// Get therapy session for last adherence Setting days
		
		List<TherapySessionMonarch> therapySessions = getTherapyforBetweenDates(adherenceSettingDaysEarlyDate, complianceDate, sortedTherapy);
				
		if(Objects.isNull(therapySessions)){
			therapySessions = new LinkedList<>();
		}
		
		int hmrRunRate = calculateHMRRunRatePerSession(therapySessions);
		newCompliance.setHmrRunRate(hmrRunRate);
		double durationForSettingDays = hmrRunRate*therapySessions.size(); // runrate*totalsessions = total duration
		
		String notification_type = null;
		boolean isSettingsDeviated = isSettingsDeviatedForSettingDays(therapySessions, userProtocolConstants, adherenceSettingDay);
		
		// validating the last adherence setting days therapies with respect to the user protocol
		if(!isHMRCompliant(userProtocolConstants, durationForSettingDays, adherenceSettingDay)){
			score = score < HMR_NON_COMPLIANCE_POINTS ? 0 : score - HMR_NON_COMPLIANCE_POINTS;
			
			int globalHMRNonAdhrenceCounter = prevCompliance.getGlobalHMRNonAdherenceCounter();
			newCompliance.setGlobalHMRNonAdherenceCounter(++globalHMRNonAdhrenceCounter);
			
			notification_type = HMR_NON_COMPLIANCE;
			newCompliance.setHmrCompliant(false);
			
			if(isSettingsDeviated){
				notification_type = HMR_AND_SETTINGS_DEVIATION;
				score = score < SETTING_DEVIATION_POINTS ? 0 : score - SETTING_DEVIATION_POINTS;
			}

		}else if(isSettingsDeviated){
			score = score < SETTING_DEVIATION_POINTS ? 0 : score - SETTING_DEVIATION_POINTS;
			notification_type = SETTINGS_DEVIATION;
			
			// Previous day setting deviation count / 0 for 1 day adherence setting 
			int setDeviationCount = initialPrevScoreFor1Day == 0 ? prevCompliance.getSettingsDeviatedDaysCount() : 0;			
			newCompliance.setSettingsDeviatedDaysCount(setDeviationCount+1);
			
			int globalSettingsDeviationCounter = prevCompliance.getGlobalSettingsDeviationCounter();
			newCompliance.setGlobalSettingsDeviationCounter(++globalSettingsDeviationCounter);
		}else{
			score = score <=  DEFAULT_COMPLIANCE_SCORE - BONUS_POINTS ? score + BONUS_POINTS : DEFAULT_COMPLIANCE_SCORE;
			//notification_type = ADHERENCE_SCORE_RESET;
			
			if(Objects.nonNull(existingNotificationofTheDay))
				notificationMonarchRepository.delete(existingNotificationofTheDay);
			newCompliance.setScore(score);
			return newCompliance;
		}
		
		if(resetFlag == 1 || (resetFlag == 2 && adherenceSettingDay != 1)){
			notification_type = initialPrevScoreFor1Day == 0 ? notification_type : ADHERENCE_SCORE_RESET;
		}			
		
		// Added the existingNotificationofTheDay param for passing the notification object for the current date
		notificationMonarchService.createOrUpdateNotification(patientUser, patient, userId,
				complianceDate, notification_type, false, existingNotificationofTheDay);
		
		// Setting the new score with respect to the compliance deduction
		newCompliance.setScore(score);
		
		// Saving the updated score for the specific date of compliance
		
		return newCompliance;
	}
		
	
	
	// calculate score with respective to missed therapies
	//private void calculateUserMissedTherapy(
	private PatientComplianceMonarch calculateUserMissedTherapy(
			PatientComplianceMonarch newCompliance,
			LocalDate complianceDate,
			Long userId,
			PatientInfo patient,
			User patientUser,
			int initialPrevScoreFor1Day,
			PatientComplianceMonarch prevCompliance,
			NotificationMonarch existingNotificationofTheDay) {
				
		// existingNotificationofTheDay object of Notification is sent to avoid repository call
		// userNotifications list object is sent to the method for getting the current day object
		notificationMonarchService.createOrUpdateNotification(patientUser, patient, userId,
				complianceDate, (initialPrevScoreFor1Day == 0 ? MISSED_THERAPY : ADHERENCE_SCORE_RESET) , false, existingNotificationofTheDay);
		
		// Commenting the repository call for previous day compliance by passing prevCompliance in the parameter
		// Getting previous day score or adherence reset score for the adherence setting value as 1
		
		
		int score = initialPrevScoreFor1Day == 0 ? prevCompliance.getScore() : initialPrevScoreFor1Day; 
		
		// Calculating the score on basis of missed therapy
		score = score < MISSED_THERAPY_POINTS ? 0 :  score - MISSED_THERAPY_POINTS ;
		
		// Previous day missed therapy count / 0 for 1 day adherence setting 
		int missedTherapyCount = initialPrevScoreFor1Day == 0 ? prevCompliance.getMissedTherapyCount() : 0;
		
		// Setting the score
		newCompliance.setScore(score);
		
		newCompliance.setMissedTherapyCount(++missedTherapyCount);
		
		int globalMissedTherapyCounter = newCompliance.getGlobalMissedTherapyCounter();
		newCompliance.setGlobalMissedTherapyCounter(++globalMissedTherapyCounter);
		
		return newCompliance;
		// Saving the score values for the specific date of compliance		
	
	}
		
	
	// calculate HMRCompliance on Missed Therapy Date for both devices		
	private PatientComplianceMonarch calculateUserHMRComplianceForMSTBoth(
			PatientComplianceMonarch newCompliance,
			ProtocolConstantsMonarch userProtocolConstants,
			ProtocolConstants userProtocolConstantsVest,
			LocalDate complianceDate,
			Long userId,
			PatientInfo patient,
			User patientUser,
			Integer adherenceSettingDay,
			int initialPrevScoreFor1Day,
			SortedMap<LocalDate,List<TherapySessionMonarch>> sortedTherapy,
			SortedMap<LocalDate,List<TherapySession>> sortedTherapyVest,
			PatientComplianceMonarch prevCompliance,
			NotificationMonarch existingNotificationofTheDay,
			Integer resetFlag) {
		
		// Commented the repository call for previous day compliance and getting the prevCompliance from the method parameter
		// Getting previous day score or adherence reset score for the adherence setting value as 1
		
		int score = initialPrevScoreFor1Day == 0 ? prevCompliance.getScore() : initialPrevScoreFor1Day;
		
		// Get earlier third day to finding therapy session
		LocalDate adherenceSettingDaysEarlyDate = getDateBeforeSpecificDays(complianceDate,(adherenceSettingDay-1));
		
		//Commented the repository call for current day therapy session and getting the same from the method parameter and the calling method
		// Get therapy session for last adherence Setting days
		List<TherapySessionMonarch> therapySessions = new LinkedList<>();
		therapySessions = getTherapyforBetweenDates(adherenceSettingDaysEarlyDate, complianceDate, sortedTherapy);
		
		// Get therapy session for last adherence Setting days
		List<TherapySession> therapySessionsVest = new LinkedList<>(); 
		therapySessionsVest = adherenceCalculationService.getTherapyforBetweenDates(adherenceSettingDaysEarlyDate, complianceDate, sortedTherapyVest);
		
		int hmrRunRate = calculateHMRRunRatePerSession(therapySessions);
		
		int hmrRunRateBoth = calculateHMRRunRatePerSessionBoth(therapySessions, therapySessionsVest);
		newCompliance.setHmrRunRate(hmrRunRateBoth);
		
		double durationForSettingDays = hmrRunRate*therapySessions.size(); // runrate*totalsessions = total duration
		
		int hmrRunRateVest = calculateHMRRunRatePerSession(therapySessionsVest);
		double durationForSettingDaysVest = hmrRunRateVest*therapySessionsVest.size(); // runrate*totalsessions = total duration
		
		String notification_type = null;
		boolean isSettingsDeviatedMonarch = isSettingsDeviatedForSettingDays(therapySessions, userProtocolConstants, adherenceSettingDay);
		
		boolean isSettingsDeviatedVest = adherenceCalculationService.isSettingsDeviatedForSettingDays(therapySessionsVest, userProtocolConstantsVest, adherenceSettingDay);
		
		
		boolean isHMRCompliantMonarch = isHMRCompliant(userProtocolConstants, durationForSettingDays, adherenceSettingDay);
		boolean isHMRCompliantVest = adherenceCalculationService.isHMRCompliant(userProtocolConstantsVest, durationForSettingDaysVest, adherenceSettingDay);
		
		// validating the last adherence setting days therapies with respect to the user protocol
		if(!isHMRCompliantMonarch || !isHMRCompliantVest){
			score = score < HMR_NON_COMPLIANCE_POINTS ? 0 : score - HMR_NON_COMPLIANCE_POINTS;
			
			int globalHMRNonAdhrenceCounter = prevCompliance.getGlobalHMRNonAdherenceCounter();
			newCompliance.setGlobalHMRNonAdherenceCounter(++globalHMRNonAdhrenceCounter);
			
			newCompliance.setHmrCompliant(false);
			
			if(isSettingsDeviatedMonarch || isSettingsDeviatedVest){				
				score = score < SETTING_DEVIATION_POINTS ? 0 : score - SETTING_DEVIATION_POINTS;
			}
			
	
		}else if(isSettingsDeviatedMonarch){
			score = score < SETTING_DEVIATION_POINTS ? 0 : score - SETTING_DEVIATION_POINTS;
			
			// Previous day setting deviation count / 0 for 1 day adherence setting 
			int setDeviationCount = initialPrevScoreFor1Day == 0 ? prevCompliance.getSettingsDeviatedDaysCount() : 0;			
			newCompliance.setSettingsDeviatedDaysCount(setDeviationCount+1);
			
			int globalSettingsDeviationCounter = prevCompliance.getGlobalSettingsDeviationCounter();
			newCompliance.setGlobalSettingsDeviationCounter(++globalSettingsDeviationCounter);
		}else{
			score = score <=  DEFAULT_COMPLIANCE_SCORE - BONUS_POINTS ? score + BONUS_POINTS : DEFAULT_COMPLIANCE_SCORE;
			
			if(Objects.nonNull(existingNotificationofTheDay))
				notificationMonarchRepository.delete(existingNotificationofTheDay);
			newCompliance.setScore(score);
			return newCompliance;
		}
		
		String notificationType = (isSettingsDeviatedMonarch && isSettingsDeviatedVest) ? SETTINGS_DEVIATION : 
			( (!isSettingsDeviatedMonarch && isSettingsDeviatedVest) ? SETTINGS_DEVIATION_VEST : 
				( (isSettingsDeviatedMonarch && !isSettingsDeviatedVest) ? SETTINGS_DEVIATION_MONARCH : "" ) );
		
		notification_type = getNotificationString(notificationType,isHMRCompliantMonarch,isHMRCompliantVest);
		
		
		/*if(resetFlag == 1 || (resetFlag == 2 && adherenceSettingDay != 1)){
			notification_type = initialPrevScoreFor1Day == 0 ? notification_type : ADHERENCE_SCORE_RESET;
		}*/			
		
		// Added the existingNotificationofTheDay param for passing the notification object for the current date
		notificationMonarchService.createOrUpdateNotification(patientUser, patient, userId,
				complianceDate, notification_type, false, existingNotificationofTheDay);
		
		// Setting the new score with respect to the compliance deduction
		newCompliance.setScore(score);
		
		// Setting to missed therapy count as 0 for any of the device is having therapy
		if( (resetFlag == 3 || resetFlag == 4) ){
			if(( Objects.nonNull(sortedTherapy.get(complianceDate)) || Objects.nonNull(sortedTherapyVest.get(complianceDate))))
				newCompliance.setMissedTherapyCount(0);
			else
				newCompliance.setMissedTherapyCount(prevCompliance.getMissedTherapyCount()+1);
		}
		
		// Saving the updated score for the specific date of compliance
		
		return newCompliance;
	}
	
	/**
	 * Get the therapy data between days and user ids
	 * @param patientUserId, from date and to date
	 * @return
	 */
	public List<TherapySessionMonarch> getLastSettingDaysTherapiesForUserId(Long patientUserId,LocalDate from,LocalDate to){
		List<TherapySessionMonarch> therapySessions = therapySessionMonarchRepository.findByDateBetweenAndPatientUserId(from, to, patientUserId);
		return therapySessions;
	}	
	
	/**
	 * Calculate HMRRunRate For PatientUsers
	 * @param patientUserIds
	 * @return
	 */
	public Map<Long,List<TherapySessionMonarch>> getLastSettingDaysTherapiesGroupByUserId(List<Long> patientUserIds,LocalDate from,LocalDate to){
		Map<Long,List<TherapySessionMonarch>> patientUserTherapyMap = new HashMap<>();
		List<TherapySessionMonarch> therapySessions = therapySessionMonarchRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<User,List<TherapySessionMonarch>> therapySessionsPerPatient = therapySessions.stream().collect(Collectors.groupingBy(TherapySessionMonarch::getPatientUser));
		for(User patientUser : therapySessionsPerPatient.keySet()){
			List<TherapySessionMonarch> sessions = therapySessionsPerPatient.get(patientUser);
			patientUserTherapyMap.put(patientUser.getId(), sessions);
		}
		return patientUserTherapyMap;
	}
	
	/**
	 * Runs every midnight , sends the notifications to Patient User.
	 */
	@Async
	@Scheduled(cron="0 25 0 * * *")	
	public void processPatientNotificationsMonarch(){
		LocalDate yesterday = LocalDate.now().minusDays(1);
		List<NotificationMonarch> notifications = notificationMonarchRepository.findByDate(yesterday);
		if(notifications.size() > 0){
			List<Long> patientUserIds = new LinkedList<>();
			for(NotificationMonarch notification : notifications){
				patientUserIds.add(notification.getPatientUser().getId());
			}
			List<PatientComplianceMonarch> complianceList = patientComplianceMonarchRepository.findByDateBetweenAndPatientUserIdIn(yesterday,
					yesterday,patientUserIds);
			Map<User,Integer> complianceMap = new HashMap<>();
			for(PatientComplianceMonarch compliance : complianceList){
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
							mailService.sendNotificationMailToPatientMonarch(patientUser,notificationType,missedTherapyCount);
					}
				});
			}catch(Exception ex){
				StringWriter writer = new StringWriter();
				PrintWriter printWriter = new PrintWriter( writer );
				ex.printStackTrace( printWriter );
				mailService.sendJobFailureNotificationMonarch("processPatientNotificationsMonarch",writer.toString());
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
	@Scheduled(cron="0 25 0 * * * ")	
	public void processHcpClinicAdminNotificationsMonarch() throws HillromException{
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
				mailService.sendNotificationMailToHCPAndClinicAdminMonarch(hcpUser, hcpClinicStatsMap.get(hcpUser));
			}
			for(User adminUser : adminClinicStatsMap.keySet()){
				mailService.sendNotificationMailToHCPAndClinicAdminMonarch(adminUser, adminClinicStatsMap.get(adminUser));
			}
			
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotificationMonarch("processHcpClinicAdminNotificationsMonarch",writer.toString());
		}
	}
	
	@Scheduled(cron="0 25 0 * * *")	
	public void processCareGiverNotificationsMonarch() throws HillromException{
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
					mailService.sendNotificationCareGiverMonarch(cgIdNameMap.get(cgEmail), cgIdPatientStatsMap.get(cgEmail));
			}
			
			
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotificationMonarch("processHcpClinicAdminNotificationsMonarch",writer.toString());
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
		List<Object[]> results =  clinicMonarchRepository.findPatientStatisticsClinicForActiveClinics();
		List<ClinicStatsNotificationVO> statsNotificationVOs = new LinkedList<>();
		for(Object[] result : results){			
			Integer adherenceSetting = DEFAULT_SETTINGS_DEVIATION_COUNT;
			if(Objects.nonNull(result[20])){
				adherenceSetting = (Integer)result[20];
			}			
			statsNotificationVOs.add(new ClinicStatsNotificationVO((BigInteger)result[0], (String)result[1], (String)result[2],
					(BigInteger)result[3],(BigInteger)result[4], (String)result[5], (String)result[6],(String)result[7],
					(BigInteger)result[8],(Integer)result[9], (Boolean)result[10],
					(Boolean)result[11], (Boolean)result[12], (Boolean)result[13], (Boolean)result[14],
					(String)result[15],(String)result[16],(Integer)result[17], (Integer)result[18], (Integer)result[19],
					(String)result[20],adherenceSetting));
		}
		return statsNotificationVOs;
	}
	private List<CareGiverStatsNotificationVO> findPatientStatisticsCareGiver() {
		List<Object[]> results =  clinicMonarchRepository.findPatientStatisticsCareGiver();
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

	public void processAdherenceScore(PatientNoEventMonarch patientNoEventMonarch,
			SortedMap<LocalDate,List<TherapySessionMonarch>> existingTherapySessionMap,
			SortedMap<LocalDate,List<TherapySessionMonarch>> receivedTherapySessionsMap,
			SortedMap<LocalDate,PatientComplianceMonarch> existingComplianceMap,
			ProtocolConstantsMonarch protocolConstant) throws Exception{
		for(LocalDate currentTherapySessionDate : receivedTherapySessionsMap.keySet()){
			List<TherapySessionMonarch> receivedTherapySessions = receivedTherapySessionsMap.get(currentTherapySessionDate);
			LocalDate firstTransmittedDate = null;
			LocalDate latestTherapyDate = null;
			PatientInfo patient = null;
			User patientUser = null;
			
			if(receivedTherapySessions.size() > 0){
				patient = receivedTherapySessions.get(0).getPatientInfo();
				patientUser = receivedTherapySessions.get(0).getPatientUser();						
				
				if(Objects.nonNull(patientNoEventMonarch) && Objects.nonNull(patientNoEventMonarch.getFirstTransmissionDate()))
					firstTransmittedDate = patientNoEventMonarch.getFirstTransmissionDate();
				else
					firstTransmittedDate = currentTherapySessionDate;
			}
			
			int adherenceSettingDay = getAdherenceSettingForPatient(patient);
			
			int totalDuration = calculateCumulativeDuration(receivedTherapySessions);		
			// Existing User First Time Transmission Data OR New User First Time Transmission Data
			if(existingTherapySessionMap.isEmpty()){
				handleFirstTimeTransmit(existingTherapySessionMap,
						receivedTherapySessionsMap, existingComplianceMap,
						protocolConstant, currentTherapySessionDate,
						firstTransmittedDate, patient, patientUser,
						totalDuration, adherenceSettingDay);
			}else{ // User Transmitting data in Subsequent requests
				// data is sent in sorted order
				latestTherapyDate = existingTherapySessionMap.lastKey();
				if (Objects.nonNull(firstTransmittedDate) && Objects.nonNull(currentTherapySessionDate)
						&& firstTransmittedDate.isBefore(currentTherapySessionDate)){
					// Data sent in sorted order
					calculateAdherenceScoreForTheDuration(patientUser,patient,firstTransmittedDate,
							currentTherapySessionDate,protocolConstant,existingComplianceMap,
							existingTherapySessionMap,receivedTherapySessionsMap, adherenceSettingDay);
				}else{
					// Older data sent
					firstTransmittedDate = currentTherapySessionDate;
					handleFirstTimeTransmit(existingTherapySessionMap,
							receivedTherapySessionsMap, existingComplianceMap,
							protocolConstant, currentTherapySessionDate,
							firstTransmittedDate, patient, patientUser,
							totalDuration, adherenceSettingDay);
				}
			}

 		}
		saveOrUpdateComplianceMap(existingComplianceMap);
		saveOrUpdateTherapySessions(receivedTherapySessionsMap);
	}

	public void processAdherenceScore(PatientNoEventMonarch patientNoEventMonarch,
			SortedMap<LocalDate,List<TherapySessionMonarch>> existingTherapySessionMap,
			SortedMap<LocalDate,List<TherapySessionMonarch>> receivedTherapySessionsMap,
			SortedMap<LocalDate,PatientComplianceMonarch> existingComplianceMap,
			ProtocolConstantsMonarch protocolConstant, Long patientUserId) throws Exception{
					
		SortedMap<LocalDate,List<TherapySession>> existingTherapySessionMapVest = therapySessionService.getAllTherapySessionsMapByPatientUserId(patientUserId);		
		
		for(LocalDate currentTherapySessionDate : receivedTherapySessionsMap.keySet()){
			List<TherapySessionMonarch> receivedTherapySessions = receivedTherapySessionsMap.get(currentTherapySessionDate);
			LocalDate firstTransmittedDate = null;
			LocalDate latestTherapyDate = null;
			PatientInfo patient = null;
			User patientUser = null;
			
			if(receivedTherapySessions.size() > 0){
				patient = receivedTherapySessions.get(0).getPatientInfo();
				patientUser = receivedTherapySessions.get(0).getPatientUser();						
				
				PatientNoEvent patientNoEventVest = patientNoEventRepository.findByPatientUserId(patientUser.getId());
				
				if(Objects.nonNull(patientNoEventMonarch) && Objects.nonNull(patientNoEventMonarch.getFirstTransmissionDate()) 
						&& Objects.nonNull(patientNoEventVest) && Objects.nonNull(patientNoEventVest.getFirstTransmissionDate()))
					firstTransmittedDate = patientNoEventVest.getFirstTransmissionDate().isBefore(patientNoEventMonarch.getFirstTransmissionDate()) ? 
							patientNoEventVest.getFirstTransmissionDate() : patientNoEventMonarch.getFirstTransmissionDate();
				else if(Objects.nonNull(patientNoEventMonarch) && Objects.nonNull(patientNoEventMonarch.getFirstTransmissionDate()))
					firstTransmittedDate = patientNoEventMonarch.getFirstTransmissionDate();
				else if(Objects.nonNull(patientNoEventVest) && Objects.nonNull(patientNoEventVest.getFirstTransmissionDate()))
					firstTransmittedDate = patientNoEventVest.getFirstTransmissionDate();
				else
					firstTransmittedDate = currentTherapySessionDate;
			}
			
			int adherenceSettingDay = getAdherenceSettingForPatient(patient);
			
			int totalDuration = calculateCumulativeDuration(receivedTherapySessions);		
			// Existing User First Time Transmission Data OR New User First Time Transmission Data
			if(existingTherapySessionMap.isEmpty() && existingTherapySessionMapVest.isEmpty() ){
				handleFirstTimeTransmit(existingTherapySessionMap,
						receivedTherapySessionsMap, existingComplianceMap,
						protocolConstant, currentTherapySessionDate,
						firstTransmittedDate, patient, patientUser,
						totalDuration, adherenceSettingDay);
			}else { // User Transmitting data in Subsequent requests
				// data is sent in sorted order
				if (Objects.nonNull(firstTransmittedDate) && Objects.nonNull(currentTherapySessionDate)
						&& firstTransmittedDate.isBefore(currentTherapySessionDate)){
					// Data sent in sorted order
					calculateAdherenceScoreForTheDuration(patientUser,patient,firstTransmittedDate,
							currentTherapySessionDate,protocolConstant,existingComplianceMap,
							existingTherapySessionMap,receivedTherapySessionsMap, adherenceSettingDay);
				}else{
					// Older data sent
					firstTransmittedDate = currentTherapySessionDate;
					handleFirstTimeTransmit(existingTherapySessionMap,
							receivedTherapySessionsMap, existingComplianceMap,
							protocolConstant, currentTherapySessionDate,
							firstTransmittedDate, patient, patientUser,
							totalDuration, adherenceSettingDay);
				}
			}
 		}
		saveOrUpdateComplianceMap(existingComplianceMap);
		saveOrUpdateTherapySessions(receivedTherapySessionsMap);
	}
	
	private synchronized void saveOrUpdateTherapySessions(
			SortedMap<LocalDate, List<TherapySessionMonarch>> receivedTherapySessionsMap) {
		Map<LocalDate, List<TherapySessionMonarch>> allTherapySessionMap = eleminateDuplicateTherapySessions(receivedTherapySessionsMap);
		
		List<TherapySessionMonarch> newTherapySessions = new LinkedList<>();
		for(LocalDate date : allTherapySessionMap.keySet()){
			List<TherapySessionMonarch> sessionsTobeSaved = allTherapySessionMap.get(date);
			newTherapySessions.addAll(sessionsTobeSaved);
		}
		therapySessionMonarchRepository.save(newTherapySessions);
	}

	private Map<LocalDate, List<TherapySessionMonarch>> eleminateDuplicateTherapySessions(
			SortedMap<LocalDate, List<TherapySessionMonarch>> receivedTherapySessionsMap) {
		List<List<TherapySessionMonarch>> therapySessionsList = new LinkedList<>(receivedTherapySessionsMap.values());
		Long patientUserId = therapySessionsList.get(0).get(0).getPatientUser().getId();
		LocalDate from = receivedTherapySessionsMap.firstKey();
		LocalDate to = receivedTherapySessionsMap.lastKey();
		List<TherapySessionMonarch> existingTherapySessions = therapySessionMonarchRepository.findByPatientUserIdAndDateRange(patientUserId, from, to);
		Map<LocalDate,List<TherapySessionMonarch>> existingTherapySessionMap = existingTherapySessions.stream().collect(Collectors.groupingBy(TherapySessionMonarch::getDate));
		Map<LocalDate,List<TherapySessionMonarch>> allTherapySessionMap = new HashMap<>();
		for(LocalDate date : receivedTherapySessionsMap.keySet()){
			List<TherapySessionMonarch> therapySessionsPerDate = existingTherapySessionMap.get(date);
			if(Objects.nonNull(therapySessionsPerDate)){
				List<TherapySessionMonarch> receivedTherapySessions = receivedTherapySessionsMap.get(date);
				for(TherapySessionMonarch existingSession : therapySessionsPerDate){
					Iterator<TherapySessionMonarch> itr = receivedTherapySessions.iterator();
					while(itr.hasNext()){
						TherapySessionMonarch receivedSession = itr.next();
						if(existingSession.getDate().equals(receivedSession.getDate()) &&
								existingSession.getStartTime().equals(receivedSession.getStartTime()) &&
								existingSession.getEndTime().equals(receivedSession.getEndTime()) &&
								existingSession.getFrequency().equals(receivedSession.getFrequency()) && 
								existingSession.getIntensity().equals(receivedSession.getIntensity()) &&
								existingSession.getHmr().equals(receivedSession.getHmr())){
							itr.remove();
						}
					}
				}
				therapySessionsPerDate.addAll(receivedTherapySessionsMap.get(date));
				Collections.sort(therapySessionsPerDate);
				int sessionNo = 0;
				for(TherapySessionMonarch session : therapySessionsPerDate){
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

	public synchronized void saveOrUpdateComplianceMap(
			SortedMap<LocalDate, PatientComplianceMonarch> existingComplianceMap) {
		// Save or update all compliance
		List<PatientComplianceMonarch> compliances = new LinkedList<>(existingComplianceMap.values());
		Long patientUserId = compliances.get(0).getPatientUser().getId();
		SortedMap<LocalDate, PatientComplianceMonarch>  complainceMapFromDB = complianceMonarchService.getPatientComplainceMapByPatientUserId(patientUserId);
		for(LocalDate date: existingComplianceMap.keySet()){
			//	complianceService.createOrUpdate(existingComplianceMap.get(date));
			PatientComplianceMonarch existingCompliance = complainceMapFromDB.get(date);
			PatientComplianceMonarch newCompliance = existingComplianceMap.get(date);
			if(Objects.nonNull(existingCompliance)){
				newCompliance.setId(existingCompliance.getId());
				existingComplianceMap.put(date,newCompliance);
			}	
		}
		complianceMonarchService.saveAll(existingComplianceMap.values());
	}

	private void handleFirstTimeTransmit(
			SortedMap<LocalDate, List<TherapySessionMonarch>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySessionMonarch>> receivedTherapySessionsMap,
			SortedMap<LocalDate, PatientComplianceMonarch> existingComplianceMap,
			ProtocolConstantsMonarch protocolConstant,
			LocalDate currentTherapySessionDate,
			LocalDate firstTransmittedDate, PatientInfo patient,
			User patientUser, int totalDuration, int adherenceSettingDay) throws Exception{
		noEventMonarchService.updatePatientFirstTransmittedDate(patientUser.getId(), currentTherapySessionDate);
		PatientComplianceMonarch currentCompliance = new PatientComplianceMonarch(DEFAULT_COMPLIANCE_SCORE, currentTherapySessionDate,
				patient, patientUser,totalDuration/adherenceSettingDay,true,false,0d);
		existingComplianceMap.put(currentTherapySessionDate, currentCompliance);
		calculateAdherenceScoreForTheDuration(patientUser,patient,firstTransmittedDate,
				currentTherapySessionDate,protocolConstant,existingComplianceMap,
				existingTherapySessionMap,receivedTherapySessionsMap,adherenceSettingDay);
	}

	private void calculateAdherenceScoreForTheDuration(
			User patientUser,
			PatientInfo patient,
			LocalDate firstTransmittedDate,
			LocalDate currentTherapyDate,
			ProtocolConstantsMonarch protocolConstant,
			SortedMap<LocalDate, PatientComplianceMonarch> existingComplianceMap,
			SortedMap<LocalDate, List<TherapySessionMonarch>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySessionMonarch>> receivedTherapySessionsMap,
			Integer adherenceSettingDay) throws Exception{
		
		LocalDate latestComplianceDate;
		if(existingComplianceMap.size()>0){
			latestComplianceDate = existingComplianceMap.lastKey();
		}else{
			SortedMap<LocalDate,PatientCompliance> existingComplianceVestMap = complianceService.getPatientComplainceMapByPatientUserId(patientUser.getId());
			latestComplianceDate = existingComplianceVestMap.lastKey();
		}
		
		List<TherapySessionMonarch> sessionsTobeSaved = receivedTherapySessionsMap.get(currentTherapyDate);
		// Get the therapy sessions for currentTherapyDate from existing therapies
		List<TherapySessionMonarch> existingTherapies = existingTherapySessionMap.get(currentTherapyDate);
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
		
		//hill-1956
		LocalDate firstresetDate = null;
		LocalDate lastresetDate = allDates.get(allDates.size()-1);
		//hill-1956
				
				
		for(LocalDate therapyDate : allDates){
			
			//hill-1956
			// query to find the adherence reset for the corresponding therapydate
			List<AdherenceResetMonarch> adherenceResetList = adherenceResetMonarchRepository.findOneByPatientUserIdAndResetStartDate(patientUser.getId(),therapyDate);
			
			//if any adherence reset is found stop the adherence  calculation and set the therapy date as first resetdate
			if(Objects.nonNull(adherenceResetList) && adherenceResetList.size() > 0)
			{
				firstresetDate = therapyDate;
				break;
			}
			//hill-1956
			
			
			// First Transmission Date to be updated
			if(firstTransmittedDate.isAfter(therapyDate)){
				noEventMonarchService.updatePatientFirstTransmittedDate(patientUser.getId(),therapyDate);
				firstTransmittedDate = therapyDate;
			}
			
			int daysBetween = DateUtil.getDaysCountBetweenLocalDates(firstTransmittedDate, therapyDate);
			List<TherapySessionMonarch> latestSettingDaysTherapySessions = prepareTherapySessionsForLastSettingdays(therapyDate,
					existingTherapySessionMap,adherenceSettingDay);
			
			double hmr = getLatestHMR(existingTherapySessionMap, receivedTherapySessionsMap,therapyDate,
					latestSettingDaysTherapySessions);
			
			int hmrRunrate = calculateHMRRunRatePerSession(latestSettingDaysTherapySessions);
			
			
			String deviceType = adherenceCalculationService.getDeviceTypeValue(patient.getId());
			
			if(deviceType.equals("BOTH")){				
				SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMapVest = 
						therapySessionService.getAllTherapySessionsMapByPatientUserId(patientUser.getId());
				
				List<TherapySession> latestSettingDaysTherapySessionsVest = adherenceCalculationService.prepareTherapySessionsForLastSettingdays(therapyDate,
						existingTherapySessionMapVest,adherenceSettingDay);
				
				hmrRunrate = calculateHMRRunRatePerSessionBoth(latestSettingDaysTherapySessions, latestSettingDaysTherapySessionsVest);				
			}
			
			LocalDate lastTransmissionDate = getLatestTransmissionDate(
					existingTherapySessionMap, therapyDate);
						
			if(deviceType.equals("BOTH")){
				SortedMap<LocalDate,List<TherapySession>> existingTherapySessionMapVest = 
						therapySessionService.getAllTherapySessionsMapByPatientUserId(patientUser.getId());
				lastTransmissionDate = getLatestTransmissionDate(
						existingTherapySessionMapVest, existingTherapySessionMap, therapyDate);
			}
			
			int missedTherapyCount = 0;
			if( (daysBetween <= 1 && adherenceSettingDay > 1 ) || (daysBetween == 0 && adherenceSettingDay == 1) ){ // first transmit
				PatientComplianceMonarch compliance = existingComplianceMap.get(therapyDate);
				if(Objects.nonNull(compliance)){
					compliance.setScore(DEFAULT_COMPLIANCE_SCORE);
					compliance.setHmr(hmr);
					compliance.setHmrRunRate(hmrRunrate);
					compliance.setHmrCompliant(true);
					compliance.setSettingsDeviated(false);
					compliance.setMissedTherapyCount(0);
				}else{
					compliance = new PatientComplianceMonarch(DEFAULT_COMPLIANCE_SCORE, therapyDate,
							patient, patientUser,hmrRunrate,true,false,missedTherapyCount,lastTransmissionDate,hmr);
				}
				if(daysBetween >= 1 && daysBetween < adherenceSettingDay && adherenceSettingDay > 1){ // second day of the transmission to earlier day of adherence setting day
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
				PatientComplianceMonarch compliance = getLatestCompliance(patientUser, patient,
						existingComplianceMap, therapyDate);
				compliance.setLatestTherapyDate(lastTransmissionDate);
				compliance.setHmr(hmr);
				compliance.setHmrRunRate(hmrRunrate);
				calculateAdherenceScoreForTheDay(compliance, missedTherapyCount,firstTransmittedDate,
						existingComplianceMap,existingTherapySessionMap,
						receivedTherapySessionsMap, protocolConstant,adherenceSettingDay);
			}
		}
		
		//hill-1956
		if(Objects.nonNull(firstresetDate))
		{
			/* find the list of adherence reset for the specific duration
			 * firstresetDate is the first reset date found for the user
			 * lastresetDate is the last date in the request
			 */
			List<AdherenceResetMonarch> adherenceResetList = adherenceResetMonarchRepository.findOneByPatientUserIdAndResetStartDates(patientUser.getId(),firstresetDate,lastresetDate);
			
			if(Objects.nonNull(adherenceResetList) && adherenceResetList.size() > 0)
			{
				for(int i = 0; i < adherenceResetList.size(); i++)
				{
					adherenceResetForPatient(patientUser.getId(), patient.getId().toString(), adherenceResetList.get(i).getResetStartDate(), DEFAULT_COMPLIANCE_SCORE, 1);
				}
			}
		}
		//hill-1956
				
	}

	public PatientComplianceMonarch getLatestCompliance(User patientUser,
			PatientInfo patient,
			SortedMap<LocalDate, PatientComplianceMonarch> existingComplianceMap,
			LocalDate therapyDate) throws Exception {
			SortedMap<LocalDate,PatientComplianceMonarch> mostRecentComplianceMap = existingComplianceMap.headMap(therapyDate);
			PatientComplianceMonarch latestCompliance = null;
			if(mostRecentComplianceMap.size() > 0){
				latestCompliance = mostRecentComplianceMap.get(mostRecentComplianceMap.lastKey());
				return buildPatientCompliance(therapyDate, latestCompliance,latestCompliance.getMissedTherapyCount());
			}else{
				return new PatientComplianceMonarch(DEFAULT_COMPLIANCE_SCORE, therapyDate,
						patient, patientUser,0,true,false,0d);
			}
	}

	private LocalDate getLatestTransmissionDate(
			SortedMap<LocalDate, List<TherapySessionMonarch>> existingTherapySessionMap,
			LocalDate date) throws Exception{
		LocalDate lastTransmissionDate = date;
		// Get Latest TransmissionDate, if data has not been transmitted for the day get mostRecent date
		if(Objects.isNull(existingTherapySessionMap.get(date))){
			SortedMap<LocalDate,List<TherapySessionMonarch>> mostRecentTherapySessionMap = existingTherapySessionMap.headMap(date);
			if(mostRecentTherapySessionMap.size()>0)
				lastTransmissionDate = mostRecentTherapySessionMap.lastKey();
		}
		return lastTransmissionDate;
	}
	
	public LocalDate getLatestTransmissionDate(
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMapVest,
			SortedMap<LocalDate, List<TherapySessionMonarch>> existingTherapySessionMapMonarch,
			LocalDate date) throws Exception{
		LocalDate lastTransmissionDateVest = date;
		LocalDate lastTransmissionDateMonarch = date;
		// Get Latest TransmissionDate for Monarch, if data has not been transmitted for the day get mostRecent date
		if(Objects.isNull(existingTherapySessionMapMonarch.get(date))){
			SortedMap<LocalDate,List<TherapySessionMonarch>> mostRecentTherapySessionMap = existingTherapySessionMapMonarch.headMap(date);
			if(mostRecentTherapySessionMap.size()>0)
				lastTransmissionDateMonarch = mostRecentTherapySessionMap.lastKey();
		}
		
		// Get Latest TransmissionDate for Vest, if data has not been transmitted for the day get mostRecent date
		if(Objects.isNull(existingTherapySessionMapVest.get(date))){
			SortedMap<LocalDate,List<TherapySession>> mostRecentTherapySessionMap = existingTherapySessionMapVest.headMap(date);
			if(mostRecentTherapySessionMap.size()>0)
				lastTransmissionDateVest = mostRecentTherapySessionMap.lastKey();
		}
		
		return (lastTransmissionDateMonarch.isAfter(lastTransmissionDateVest) ? lastTransmissionDateMonarch : lastTransmissionDateVest);
	}

	private double getLatestHMR(
			SortedMap<LocalDate, List<TherapySessionMonarch>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySessionMonarch>> receivedTherapySessionsMap,
			LocalDate date, List<TherapySessionMonarch> latestSettingDaysTherapySessions) throws Exception{
		double hmr = 0;
		if(Objects.nonNull(receivedTherapySessionsMap.get(date))){
			List<TherapySessionMonarch> currentTherapySessions = receivedTherapySessionsMap.get(date);
			if(Objects.nonNull(currentTherapySessions) && currentTherapySessions.size() > 0)
				hmr = currentTherapySessions.get(currentTherapySessions.size()-1).getHmr();
		}else if(existingTherapySessionMap.size() > 0){
			SortedMap<LocalDate, List<TherapySessionMonarch>> previousTherapySessionMap = existingTherapySessionMap
					.headMap(date);
			if (previousTherapySessionMap.size() > 0) {
				List<TherapySessionMonarch> mostRecentTherapySessions = previousTherapySessionMap
						.get(previousTherapySessionMap.lastKey());
				hmr = mostRecentTherapySessions.get(
						mostRecentTherapySessions.size() - 1).getHmr();
			}
		}
		return hmr;
	}

	private PatientComplianceMonarch buildPatientCompliance(LocalDate date,
			PatientComplianceMonarch latestcompliance,int missedTherapyCount) {
		PatientComplianceMonarch compliance = new PatientComplianceMonarch();
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

	public void calculateAdherenceScoreForTheDay(PatientComplianceMonarch latestCompliance,int currentMissedTherapyCount,
			LocalDate firstTransmissionDate,
			SortedMap<LocalDate,PatientComplianceMonarch> complianceMap,
			SortedMap<LocalDate, List<TherapySessionMonarch>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySessionMonarch>> receivedTherapySessionsMap,
			ProtocolConstantsMonarch protocolConstant,
			Integer adherenceSettingDay) throws Exception{

		int currentScore = latestCompliance.getScore();
		String notificationType = "";
		User patientUser = latestCompliance.getPatientUser();
		Long patientUserId = patientUser.getId();
		PatientInfo patient = latestCompliance.getPatient();
		LocalDate today =LocalDate.now();

		// MISSED THERAPY
		if(currentMissedTherapyCount >= adherenceSettingDay){
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

			SortedMap<LocalDate,List<TherapySession>> existingTherapySessionMapVest = null; 
					
			Map<String,Double> therapyMetrics;
			Map<String,Double> therapyMetricsVest;
			boolean isHMRCompliant = true;
			boolean isHMRCompliantVest = true;
			
			List<TherapySessionMonarch> latestSettingDaysTherapySessions = prepareTherapySessionsForLastSettingdays(latestCompliance.getDate(),
					existingTherapySessionMap,adherenceSettingDay);
			
			
			String deviceType = adherenceCalculationService.getDeviceTypeValue(patient.getId());
			
			therapyMetrics = calculateTherapyMetricsPerSettingDays(latestSettingDaysTherapySessions);				
			isHMRCompliant = isHMRCompliant(protocolConstant, therapyMetrics.get(TOTAL_DURATION),adherenceSettingDay);
			
			if(deviceType.equals("BOTH")){
				existingTherapySessionMapVest = 
						therapySessionService.getAllTherapySessionsMapByPatientUserId(patientUser.getId());
				
				ProtocolConstants protocolConstantVest = adherenceCalculationService.getProtocolByPatientUserId(patientUser.getId()); 
			
				List<TherapySession> latestSettingDaysTherapySessionsVest = adherenceCalculationService.prepareTherapySessionsForLastSettingdays(latestCompliance.getDate(),
						existingTherapySessionMapVest,adherenceSettingDay);
				
				therapyMetricsVest = adherenceCalculationService.calculateTherapyMetricsPerSettingDays(latestSettingDaysTherapySessionsVest);
				isHMRCompliantVest = adherenceCalculationService.isHMRCompliant(protocolConstantVest, therapyMetricsVest.get(TOTAL_DURATION),adherenceSettingDay);
				
				/*Map<String, Object> latestSettingDaysTherapySessionsBoth = prepareTherapySessionsForLastSettingdays(latestCompliance.getDate(),
						existingTherapySessionMap,existingTherapySessionMapVest,adherenceSettingDay);
				
				therapyMetrics = calculateTherapyMetricsPerSettingDaysBoth(latestSettingDaysTherapySessionsBoth);
				isHMRCompliant = isHMRCompliant(protocolConstant, protocolConstantVest, therapyMetrics.get(TOTAL_DURATION),adherenceSettingDay);*/
			}
			
			boolean isSettingsDeviated = false;
			boolean isSettingsDeviatedVest = false;
					
			// Settings deviated to be calculated only on Therapy done days
			if(currentMissedTherapyCount == 0){
				
				isSettingsDeviated = isSettingsDeviatedForSettingDays(latestSettingDaysTherapySessions, protocolConstant, adherenceSettingDay);
				
				if(deviceType.equals("BOTH")){
					List<TherapySession> latestSettingDaysTherapySessionsVest = adherenceCalculationService.prepareTherapySessionsForLastSettingdays(latestCompliance.getDate(),
							existingTherapySessionMapVest,adherenceSettingDay);
					
					ProtocolConstants protocolConstantVest = adherenceCalculationService.getProtocolByPatientUserId(patientUser.getId());
					
					isSettingsDeviatedVest = adherenceCalculationService.isSettingsDeviatedForSettingDays(latestSettingDaysTherapySessionsVest, protocolConstantVest, adherenceSettingDay);					
				}
				
				applySettingsDeviatedDaysCount(latestCompliance, complianceMap,
						(isSettingsDeviated || isSettingsDeviatedVest), adherenceSettingDay);

				if(isSettingsDeviated || isSettingsDeviatedVest){
					currentScore -=  SETTING_DEVIATION_POINTS;
					
					if(isSettingsDeviated && (deviceType.equals("MONARCH") ||  isSettingsDeviatedVest)){
						notificationType =  SETTINGS_DEVIATION;
					}else if(isSettingsDeviated && !isSettingsDeviatedVest){
						notificationType =  SETTINGS_DEVIATION_MONARCH;
					}else if(!isSettingsDeviated && isSettingsDeviatedVest){
						notificationType =  SETTINGS_DEVIATION_VEST;
					}
					
					// increment global settings Deviation counter
					int globalSettingsDeviationCounter = latestCompliance.getGlobalSettingsDeviationCounter();
					latestCompliance.setGlobalSettingsDeviationCounter(++globalSettingsDeviationCounter);
				}else{
					// reset settingsDeviatedDays count if patient is adhere to settings
					latestCompliance.setSettingsDeviatedDaysCount(0);
				}
			}else{
				// reset settingsDeviatedDays count if patient missed therapy
				latestCompliance.setSettingsDeviatedDaysCount(0);
			}

			latestCompliance.setSettingsDeviated(isSettingsDeviated);
			
			if(!isHMRCompliant || !isHMRCompliantVest){
				if(!today.equals(latestCompliance.getDate()) || currentMissedTherapyCount == 0){
					currentScore -=  HMR_NON_COMPLIANCE_POINTS;
					
					if(deviceType.equals("MONARCH")){
						if(StringUtils.isBlank(notificationType))
							notificationType =  HMR_NON_COMPLIANCE;
						else
							notificationType =  HMR_AND_SETTINGS_DEVIATION;
					}else{
						notificationType = getNotificationString(notificationType, isHMRCompliant, isHMRCompliantVest);
					}
					
					// increment global HMR Non Adherence Counter
					int globalHMRNonAdherenceCounter = latestCompliance.getGlobalHMRNonAdherenceCounter();
					latestCompliance.setGlobalHMRNonAdherenceCounter(++globalHMRNonAdherenceCounter);
				}
			}
			
			latestCompliance.setHmrCompliant(isHMRCompliant);
			// Delete existing notification if adherence to protocol
			notificationMonarchService.deleteNotificationIfExists(patientUserId,
					latestCompliance.getDate(), currentMissedTherapyCount,
					isHMRCompliant, isSettingsDeviated, adherenceSettingDay);
			
			// No Notification add +1
			if(StringUtils.isBlank(notificationType)){
				if(!today.equals(latestCompliance.getDate()) || currentMissedTherapyCount == 0){
					currentScore = currentScore <=  DEFAULT_COMPLIANCE_SCORE - BONUS_POINTS ? currentScore + BONUS_POINTS : DEFAULT_COMPLIANCE_SCORE;
				}
			}
		}
		
		// Patient did therapy but point has been deducted due to Protocol violation
		if(StringUtils.isNotBlank(notificationType)){
			notificationMonarchService.createOrUpdateNotification(patientUser, patient, patientUserId,
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

	public String getNotificationString(String notificationType,boolean isHMRCompliantMonarch,boolean isHMRCompliantVest)
	{	
		String retNotificationType = "";
		
		notificationType = StringUtils.isBlank(notificationType) ? "" : notificationType;  
		
		switch(notificationType){
		
			case "":
				// When there is no setting is deviated and HMR compliant / non compliant in either of the device combinations
				if(!isHMRCompliantMonarch){
					if(!isHMRCompliantVest)
						retNotificationType =  HMR_NON_COMPLIANCE;	
					else
						retNotificationType =  HMR_NON_COMPLIANCE_MONARCH;					
				}else{
					if(!isHMRCompliantVest)
						retNotificationType =  HMR_NON_COMPLIANCE_VEST;
				}
				break;
				
			case SETTINGS_DEVIATION:				
				// When both the device setting is deviated and HMR compliant / non compliant in either of the device combinations			
				if(!isHMRCompliantMonarch){
					if(!isHMRCompliantVest)
						retNotificationType =  HMR_AND_SETTINGS_DEVIATION;
					else
						retNotificationType =  HMR_MONARCH_AND_SETTINGS_DEVIATION;					
				}else{
					if(!isHMRCompliantVest)
						retNotificationType =  HMR_VEST_AND_SETTINGS_DEVIATION;
				}
				break;
			
			case SETTINGS_DEVIATION_VEST:				
				// When Vest device is setting deviated and HMR compliant / non compliant in either of the device combinations
				if(!isHMRCompliantMonarch){
					if(!isHMRCompliantVest)
						retNotificationType =  HMR_AND_SETTINGS_DEVIATION_VEST;
					else
						retNotificationType =  HMR_MONARCH_AND_SETTINGS_DEVIATION_VEST;	
				}else{
					if(!isHMRCompliantVest)
						retNotificationType =  HMR_VEST_AND_SETTINGS_DEVIATION_VEST;
				}
				break;
				
			case SETTINGS_DEVIATION_MONARCH:				
				// When Monarch device is setting deviated and HMR compliant / non compliant in either of the device combinations
				if(!isHMRCompliantMonarch)
					if(!isHMRCompliantVest)
						retNotificationType =  HMR_AND_SETTINGS_DEVIATION_MONARCH;
					else 
						retNotificationType =  HMR_MONARCH_AND_SETTINGS_DEVIATION_MONARCH;
				else{
					if(!isHMRCompliantVest)
						retNotificationType =  HMR_VEST_AND_SETTINGS_DEVIATION_MONARCH;
				}
				break;
		}
		
		return retNotificationType;
	}
	public void applySettingsDeviatedDaysCount(
			PatientComplianceMonarch latestCompliance,
			SortedMap<LocalDate, PatientComplianceMonarch> complianceMap,
			boolean isSettingsDeviated, Integer adherenceSettingDay) throws Exception{
		int settingsDeviatedDaysCount;
		if(isSettingsDeviated){
			int previousSettingsDeviatedDaysCount = 0;
			SortedMap<LocalDate,PatientComplianceMonarch> mostRecentComplianceMap = complianceMap.headMap(latestCompliance.getDate());
			if(mostRecentComplianceMap.size() > 0){
				PatientComplianceMonarch previousCompliance = mostRecentComplianceMap.get(mostRecentComplianceMap.lastKey());
				previousSettingsDeviatedDaysCount = previousCompliance.getSettingsDeviatedDaysCount();
			}
			// If settingsDeviationDaysCount is 0 for previous date, settingsDeviationDaysCount would be default value. increments thereafter
			//settingsDeviatedDaysCount =  previousSettingsDeviatedDaysCount == 0 ? adherenceSettingDay :++previousSettingsDeviatedDaysCount;
			settingsDeviatedDaysCount =  ++previousSettingsDeviatedDaysCount;
			latestCompliance.setSettingsDeviatedDaysCount(settingsDeviatedDaysCount);
		}
	} 
	
	public List<TherapySessionMonarch> prepareTherapySessionsForLastSettingdays(
			LocalDate currentTherapyDate,
			SortedMap<LocalDate, List<TherapySessionMonarch>> existingTherapySessionMap,
			Integer adherenceSettingDay) {
		List<TherapySessionMonarch> therapySessions = new LinkedList<>();
		for(int i = 0;i < adherenceSettingDay;i++){
			List<TherapySessionMonarch> previousExistingTherapySessions = existingTherapySessionMap.get(currentTherapyDate.minusDays(i));
			if(Objects.nonNull(previousExistingTherapySessions))
				therapySessions.addAll(previousExistingTherapySessions);
		}
		return therapySessions;
	}
	
	public Map<String,Object> prepareTherapySessionsForLastSettingdays(
			LocalDate currentTherapyDate,
			SortedMap<LocalDate, List<TherapySessionMonarch>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMapVest,			
			Integer adherenceSettingDay) {
		
		List<Object> therapySessions = new LinkedList<>();
		Map<String,Object> combined = new HashMap<String,Object>();
		
		for(int i = 0;i < adherenceSettingDay;i++){
			List<TherapySessionMonarch> previousExistingTherapySessions = existingTherapySessionMap.get(currentTherapyDate.minusDays(i));
			if(Objects.nonNull(previousExistingTherapySessions))
				therapySessions.addAll(previousExistingTherapySessions);
			combined.put("Monarch", previousExistingTherapySessions);
				
			
			List<TherapySession> previousExistingTherapySessionsVest = existingTherapySessionMapVest.get(currentTherapyDate.minusDays(i));
			if(Objects.nonNull(previousExistingTherapySessionsVest))
				therapySessions.addAll( previousExistingTherapySessionsVest);
			combined.put("Vest", previousExistingTherapySessionsVest);
			
		}
		return combined;
	}
	
	public boolean isSettingsDeviatedForSettingDays(List<TherapySessionMonarch> lastSettingDaysTherapySessions,
			ProtocolConstantsMonarch protocol, Integer adherenceSettingDay){
		Map<LocalDate, List<TherapySessionMonarch>> lastSettingDaysTherapySessionMap = lastSettingDaysTherapySessions
				.stream().collect(
						Collectors.groupingBy(TherapySessionMonarch::getDate));
		boolean isSettingsDeviated = false;
		// This is for checking settings deviation, settings deviation should be calculated for consecutive adherence setting days
		//(exclusive missed therapy)
		if(lastSettingDaysTherapySessionMap.keySet().size() == adherenceSettingDay){
			for(LocalDate d : lastSettingDaysTherapySessionMap.keySet()){
				List<TherapySessionMonarch> therapySeesionsPerDay = lastSettingDaysTherapySessionMap.get(d);
				double weightedFrequency = calculateTherapyMetricsPerSettingDays(therapySeesionsPerDay).get(WEIGHTED_AVG_FREQUENCY);
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
	
	private boolean isSettingsDeviatedForSettingDays(List<TherapySessionMonarch> lastSettingDaysTherapySessions,
			List<TherapySession> lastSettingDaysTherapySessionsVest,
			ProtocolConstantsMonarch protocol, Integer adherenceSettingDay){
		Map<LocalDate, List<TherapySessionMonarch>> lastSettingDaysTherapySessionMap = lastSettingDaysTherapySessions
				.stream().collect(
						Collectors.groupingBy(TherapySessionMonarch::getDate));
		boolean isSettingsDeviated = false;
		// This is for checking settings deviation, settings deviation should be calculated for consecutive adherence setting days
		//(exclusive missed therapy)
		if(lastSettingDaysTherapySessionMap.keySet().size() == adherenceSettingDay){
			for(LocalDate d : lastSettingDaysTherapySessionMap.keySet()){
				List<TherapySessionMonarch> therapySeesionsPerDay = lastSettingDaysTherapySessionMap.get(d);
				double weightedFrequency = calculateTherapyMetricsPerSettingDays(therapySeesionsPerDay).get(WEIGHTED_AVG_FREQUENCY);
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
	
	private boolean isSettingDeviatedForUserOnDay(Long userId, LocalDate complianceDate,Integer adherenceSettingDay, ProtocolConstantsMonarch userProtocolConstant){
		// Get earlier third day to finding therapy session
		LocalDate adherenceSettingDaysEarlyDate = getDateBeforeSpecificDays(complianceDate,(adherenceSettingDay-1));
		
		// Get therapy session for last adherence Setting days
		List<TherapySessionMonarch> therapySessions = therapySessionMonarchRepository.findByDateBetweenAndPatientUserId(adherenceSettingDaysEarlyDate, complianceDate, userId);
				
		if(Objects.isNull(therapySessions)){
			therapySessions = new LinkedList<>();
		}
				
		return isSettingsDeviatedForSettingDays(therapySessions, userProtocolConstant, adherenceSettingDay);
	}
	
	private Integer getAdherenceSettingForPatient(PatientInfo patient){		
		Clinic clinic = clinicPatientService.getAssociatedClinic(patient);
		if(Objects.nonNull(clinic))
			return clinic.getAdherenceSetting();
		else
			return ADHERENCE_SETTING_DEFAULT_DAYS;
	}
	
	private Integer getAdherenceSettingForUserId(Long patientUserId){
		PatientInfo patient = userService.getPatientInfoObjFromPatientUserId(patientUserId);		
		return getAdherenceSettingForPatient(patient);		
	}
	
	
	/**
	 * Runs every midnight to integrate the patient who is using both devices after identified
	 */
	//@Scheduled(cron="0 55 23 * * * ")	
	public void processDeviceDetails(){
		try{
			LocalDate today = LocalDate.now();
			log.debug("Started Device details "+DateTime.now()+","+today);
			
			List<PatientDevicesAssoc> patDevAssList = patientDevicesAssocRepository.findByCreatedDate(today.toString());			
			
			executeMergingProcess(patDevAssList, 2);
			
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotificationMonarch("processDeviceDetails",writer.toString());
		}
	}

	public void executeMergingProcess(List<PatientDevicesAssoc> patDevAssList, int flag){
		
		Map<Long,PatientNoEventMonarch> userIdNoEventMap = noEventMonarchService.findAllGroupByPatientUserId();			
		Map<Long,PatientNoEvent> userIdNoEventMapVest = noEventMonarchServiceVest.findAllGroupByPatientUserId();
	
		for(PatientDevicesAssoc patDevice : patDevAssList){
			executeMergingProcessLoop(userIdNoEventMap, userIdNoEventMapVest, patDevice, flag);	
		}
	}

	public void executeMergingProcessLoop(Map<Long,PatientNoEventMonarch> userIdNoEventMap,
			Map<Long,PatientNoEvent> userIdNoEventMapVest,
			PatientDevicesAssoc patDevice, int flag){
		List<PatientDevicesAssoc> devAssForPatientList = patientDevicesAssocRepository.findByPatientId(patDevice.getPatientId());
		
		if(devAssForPatientList.size()>1){
			LocalDate vestCreatedDate = null;
			LocalDate monarchCreatedDate = null;
			
			for(PatientDevicesAssoc device : devAssForPatientList){
				if(device.getDeviceType().equals("VEST")){
					vestCreatedDate = device.getCreatedDate();
				}else if(device.getDeviceType().equals("MONARCH")){
					monarchCreatedDate = device.getCreatedDate();
				}							
			}
			
			PatientInfo patientInfo = patientInfoRepository.findOneById(patDevice.getPatientId());
			User user = userService.getUserObjFromPatientInfo(patientInfo);
			
			LocalDate firstTransmissionDateMonarch = null;
			User userOld = null;
			
			if(flag == 2){
				PatientInfo patientInfoOld = patientInfoRepository.findOneById(patDevice.getOldPatientId());
				userOld = userService.getUserObjFromPatientInfo(patientInfoOld);
				
				PatientNoEventMonarch noEventMonarch = userIdNoEventMap.get(userOld.getId());
				
				if(Objects.nonNull(noEventMonarch) && (Objects.nonNull(noEventMonarch.getFirstTransmissionDate()))){
					firstTransmissionDateMonarch = noEventMonarch.getFirstTransmissionDate();
				}
			}
			
			LocalDate firstTransmissionDateVest = null;
			
			PatientNoEvent noEventVest = userIdNoEventMapVest.get(user.getId());
			
			if(Objects.nonNull(noEventVest) && (Objects.nonNull(noEventVest.getFirstTransmissionDate()))){
				firstTransmissionDateVest = noEventVest.getFirstTransmissionDate();
			}
			
			
			if(Objects.nonNull(vestCreatedDate) && vestCreatedDate.isBefore(monarchCreatedDate)){
				
				List<PatientCompliance> patientComplianceList = patientComplianceRepository.findByPatientUserId(user.getId());
				List <PatientComplianceMonarch> complianceListToSave = new LinkedList<>();
				
				for(PatientCompliance patientCompliance : patientComplianceList){
					PatientComplianceMonarch compliance = new PatientComplianceMonarch(patientCompliance.getScore(),
							patientCompliance.getDate(),
							patientCompliance.getPatient(),
							patientCompliance.getPatientUser(),
							patientCompliance.getHmrRunRate(),
							patientCompliance.getHmr(),
							patientCompliance.isHmrCompliant(),
							patientCompliance.isSettingsDeviated(),
							patientCompliance.getMissedTherapyCount(),
							patientCompliance.getLatestTherapyDate(),
							patientCompliance.getSettingsDeviatedDaysCount(),
							patientCompliance.getGlobalHMRNonAdherenceCounter(),
							patientCompliance.getGlobalSettingsDeviationCounter(),
							patientCompliance.getGlobalMissedTherapyCounter());
					 		
					complianceListToSave.add(compliance);						 
				}
				complianceMonarchService.saveAll(complianceListToSave);
				
				List<Notification> notificationList = notificationRepository.findByPatientUserId(user.getId());
				
				List <NotificationMonarch> notificationListToSave = new LinkedList<>();
				
				for(Notification patientNotification : notificationList){
					NotificationMonarch notification = new NotificationMonarch(
							patientNotification.getNotificationType(),
							patientNotification.getDate(),
							patientNotification.getPatientUser(),
							patientNotification.getPatient(),
							patientNotification.isAcknowledged());
							
					notificationListToSave.add(notification);
				}
				notificationMonarchService.saveAll(notificationListToSave);
				
				if(flag == 2){
					List<TherapySessionMonarch> therapySessionMonarchList = therapySessionMonarchRepository.findByPatientUserId(userOld.getId());
					
					List <TherapySessionMonarch> therapySessionListToSave = new LinkedList<>();
					
					for(TherapySessionMonarch patientTherapySession : therapySessionMonarchList){
						TherapySessionMonarch therapySession = new TherapySessionMonarch(patientInfo, user, 
								patientTherapySession.getDate(), patientTherapySession.getSessionNo(),
								patientTherapySession.getSessionType(), patientTherapySession.getStartTime(), patientTherapySession.getEndTime(),
								patientTherapySession.getFrequency(), patientTherapySession.getIntensity(), patientTherapySession.getDurationInMinutes(),
								patientTherapySession.getProgrammedCaughPauses(), patientTherapySession.getNormalCaughPauses(),
								patientTherapySession.getCaughPauseDuration(), patientTherapySession.getHmr(), patientTherapySession.getSerialNumber(),
								patientTherapySession.getBluetoothId(), patientTherapySession.getTherapyIndex(),
								patientTherapySession.getStartBatteryLevel(), patientTherapySession.getEndBatteryLevel(),
								patientTherapySession.getNumberOfEvents(), patientTherapySession.getNumberOfPods(), patientTherapySession.getDevWifi(),
								patientTherapySession.getDevLte(),patientTherapySession.getDevBt(),
								patientTherapySession.getDevVersion());
							
						therapySessionListToSave.add(therapySession);
					}
					therapySessionMonarchService.saveAll(therapySessionListToSave);
					
					PatientNoEventMonarch patientNoEventMonarch = noEventMonarchRepository.findByPatientUserId(userOld.getId());
					
					PatientNoEventMonarch noEventMonarchToSave = new PatientNoEventMonarch(patientNoEventMonarch.getUserCreatedDate(),
							patientNoEventMonarch.getFirstTransmissionDate(), patientInfo, user);						
					
					noEventMonarchService.save(noEventMonarchToSave);
					
					adherenceCalculationBoth(user.getId(), null, firstTransmissionDateMonarch, firstTransmissionDateVest, DEFAULT_COMPLIANCE_SCORE, userOld.getId(), 4);
				}
			}else{
				if(flag == 2)
					adherenceCalculationBoth(user.getId(), null, firstTransmissionDateVest, firstTransmissionDateMonarch, DEFAULT_COMPLIANCE_SCORE, userOld.getId(), 3);
			}
		}
	}
	
}