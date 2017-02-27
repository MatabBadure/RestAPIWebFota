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
import static com.hillrom.vest.config.AdherenceScoreConstants.ADHERENCE_SETTING_DEFAULT_DAYS;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.service.monarch.AdherenceCalculationServiceMonarch;
import com.hillrom.vest.service.monarch.PatientNoEventMonarchService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.CareGiverStatsNotificationVO;
import com.hillrom.vest.web.rest.dto.ClinicStatsNotificationVO;
import com.hillrom.vest.web.rest.dto.PatientStatsVO;

//hill-1956
import com.hillrom.vest.domain.AdherenceReset;
import com.hillrom.vest.repository.AdherenceResetRepository;
//hill-1956


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
	private PatientNoEventMonarchService noEventMonarchService;
	
	@Inject
	private ClinicRepository clinicRepository;
	
	@Inject
	private ClinicPatientService clinicPatientService;
	
	@Inject
	private UserService userService;
	
	//hill-1956
	@Inject
	private AdherenceResetRepository adherenceResetRepository;
	//hill-1956

	@Inject
	private AdherenceCalculationServiceMonarch adherenceCalculationServiceMonarch;
	
	@Inject
    private PatientVestDeviceService patientVestDeviceService;
	
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
	 * Calculates Metrics such as weightedAvgFrequency,Pressure,treatmentsPerDay,duration for last adherence setting days
	 * @param therapySessionsPerDay
	 * @return
	 */
	public Map<String,Double> calculateTherapyMetricsPerSettingDays(
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
	 * Runs every midnight deducts the compliance score by 5 if therapy hasn't been done for adherence setting day(s)
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
				PatientInfo patientInfo = compliance.getPatient();
				
				Integer adherenceSettingDay = getAdherenceSettingForPatient(patientInfo);
				
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
					// HMR Compliance shouldn't be checked for Patients for initial adherence setting days of transmission date
				}else if(Objects.nonNull(noEvent)&& (Objects.nonNull(noEvent.getFirstTransmissionDate()) && 
						DateUtil.getDaysCountBetweenLocalDates(noEvent.getFirstTransmissionDate(), today) < (adherenceSettingDay-1) &&
						adherenceSettingDay > 1 )){
					// For Transmitted users no notification till earlier day of adherence Setting day(s)
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
					if(newCompliance.getMissedTherapyCount() >= adherenceSettingDay){ // missed Therapy for adherenceSetting day(s) or more than adherenceSetting day(s) days
						mstNotificationMap.put(compliance.getPatientUser().getId(), newCompliance);
					}else{ // missed therapy for less than adherence setting day(s) , might fall under hmrNonCompliance
						hmrNonComplianceMap.put(compliance.getPatientUser().getId(), newCompliance);
					}
				}
			}
			
			userProtocolConstantsMap = protocolService.getProtocolByPatientUserIds(new LinkedList<>(hmrNonComplianceMap.keySet()));
			
			//int maxAdherenceSetting = getMaxAdherenceSettingDay();			
			//Map<Long,List<TherapySession>> userIdLastSettingDaysTherapiesMap = getLastSettingDaysTherapiesGroupByUserId(new LinkedList<>(hmrNonComplianceMap.keySet()),getPlusOrMinusTodayLocalDate(-(maxAdherenceSetting-1)),today);

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
			mailService.sendJobFailureNotification("processMissedTherapySessions",writer.toString());
		}
	}
	
	// Resetting the adherence score for the specific user from the adherence reset start date
	@Async	
	public Future<String> adherenceSettingForClinic(String clinicId){
		try{
			long startTime = System.currentTimeMillis();
			
			//List<PatientInfo> patientList = clinicPatientService.getPatientListForClinic(clinicId);
			List<User> userList = clinicPatientService.getUserListForClinic(clinicId);
			List<Long> userIdList = clinicPatientService.getUserIdListFromUserList(userList);
			
			Map<Long,PatientNoEvent> userIdNoEventMap = noEventService.findAllByPatientUserId(userIdList);
			Map<Long,PatientNoEventMonarch> userIdNoEventMapMonarch = noEventMonarchService.findAllByPatientUserId(userIdList);
			String successMessage = "";
			if(userList.size()>0){
				for(User user : userList){
					//User user = userService.getUserObjFromPatientInfo(patient);
					
					LocalDate startDate = fineOneByPatientUserIdLatestResetStartDate(user.getId());
					LocalDate startDateMonarch = adherenceCalculationServiceMonarch.fineOneByPatientUserIdLatestResetStartDate(user.getId());
					
					String deviceType = patientVestDeviceService.getDeviceType(user);
					
					// flag for adherence setting / existing reset of score for adherence setting  
					int resetFlagForSetting = 0;
					
					if( ( deviceType.equals("VEST") && Objects.nonNull(startDate) ) || 
							( deviceType.equals("MONARCH") && Objects.nonNull(startDateMonarch) ) ||
							( deviceType.equals("ALL") && Objects.nonNull(startDate) && Objects.nonNull(startDateMonarch) ) ){
						// To identify unique for existing reset for the patient
						resetFlagForSetting = 2;
					}
					
					
					if((deviceType.equals("VEST") || deviceType.equals("ALL")) && Objects.isNull(startDate)){
						PatientNoEvent noEvent = userIdNoEventMap.get(user.getId());
						
						if(Objects.nonNull(noEvent) &&  Objects.nonNull(noEvent.getFirstTransmissionDate())){
							startDate = noEvent.getFirstTransmissionDate();
						}
						
					}else if( (deviceType.equals("MONARCH") || deviceType.equals("ALL")) && Objects.isNull(startDateMonarch)){
						PatientNoEventMonarch noEventMonarch = userIdNoEventMapMonarch.get(user.getId());						
						
						if(Objects.nonNull(noEventMonarch) &&  Objects.nonNull(noEventMonarch.getFirstTransmissionDate())){
							startDateMonarch = noEventMonarch.getFirstTransmissionDate();
						}
						
					}
					
					if(Objects.nonNull(startDate) || Objects.nonNull(startDateMonarch)){
						PatientInfo patient = userService.getPatientInfoObjFromPatientUser(user);
						if(deviceType.equals("VEST")){
							adherenceResetForPatient(user.getId(), patient.getId(), startDate, DEFAULT_COMPLIANCE_SCORE, resetFlagForSetting);
						}else if(deviceType.equals("MONARCH")){
							adherenceCalculationServiceMonarch.adherenceResetForPatient(user.getId(), patient.getId(), startDateMonarch, DEFAULT_COMPLIANCE_SCORE, resetFlagForSetting);
						}else if(deviceType.equals("ALL")){
							adherenceResetForPatient(user.getId(), patient.getId(), startDate, DEFAULT_COMPLIANCE_SCORE, resetFlagForSetting);
							adherenceCalculationServiceMonarch.adherenceResetForPatient(user.getId(), patient.getId(), startDateMonarch, DEFAULT_COMPLIANCE_SCORE, resetFlagForSetting);
						}
					}
				}
				successMessage = MessageConstants.HR_314;
			}else{
				successMessage = MessageConstants.HR_315;
			}
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;				
			log.error("adherenceSettingForClinic method executed in :"+totalTime+" milliseconds");
			return new AsyncResult<>(successMessage);			
		}catch(Exception ex){
			log.debug(ex.getMessage());
		}
		return new AsyncResult<>("Adherence score recalculated successfully for all patients under clinic");
	}
	
	public LocalDate fineOneByPatientUserIdLatestResetStartDate(Long userId){    	
    	List<AdherenceReset> adherenceReset = adherenceResetRepository.findOneByPatientUserIdLatestResetStartDate(userId);
    	if(adherenceReset.size() > 0)
    		return adherenceReset.get(0).getResetStartDate();
    	else
    		return null;
    }
	
	// Getting the therapy data for the requested date
	public List<TherapySession> getTherapyForDay(SortedMap<LocalDate,List<TherapySession>> sortedTherapySession,LocalDate curDate){		
		return sortedTherapySession.get(curDate);
	}
	
	// Getting the therapy data for the requested between period 
	public List<TherapySession> getTherapyforBetweenDates(LocalDate fromDate, LocalDate toDate, SortedMap<LocalDate,List<TherapySession>> sortedTherapySession){
		List<TherapySession> session = new LinkedList<>();
		
		List<LocalDate> allDates = DateUtil.getAllLocalDatesBetweenDates(fromDate, toDate);
		
		for(LocalDate date : allDates){
			
			if(Objects.nonNull(sortedTherapySession.get(date)))
				session.addAll(sortedTherapySession.get(date));
		}
		
		return session;
	}
	
	// Getting the compliance object for the previous date / previous record 
	public PatientCompliance returnPrevDayCompli(List<PatientCompliance> complianceList, LocalDate currDate){
		
		int j = -1;
		for(int i = 0; i <= (complianceList.size()-1); i++){
			if(complianceList.get(i).getDate() == currDate){				
				j = i;
			}
		}
		return (j > 0 ? complianceList.get(j-1) : null);
	}
	
	// Grouping the therapy data by date 
	public SortedMap<LocalDate,List<TherapySession>> groupTherapySessionsByDate(List<TherapySession> therapySessions){
		return new TreeMap<>(therapySessions.stream().collect(Collectors.groupingBy(TherapySession :: getDate)));
	}
	
	// Resetting the adherence score for the specific user from the adherence reset start date	
	public String adherenceResetForPatient(Long userId, String patientId, LocalDate adherenceStartDate, Integer adherenceScore, Integer resetFlag){
		try{
			
			// Adherence Start date in string for query
			String sAdherenceStDate = adherenceStartDate.toString();
			
			LocalDate todayDate = LocalDate.now();
			LocalDate prevDate = DateUtil.getPlusOrMinusTodayLocalDate(-1);
			
			// Get the list of rows for the user id from the adherence reset start date 
			List<PatientCompliance> patientComplianceList = patientComplianceRepository.returnComplianceForPatientIdDates(sAdherenceStDate, userId);
			
			List<PatientCompliance> complianceListToStore = new LinkedList<>();
			
			// Getting the protocol constants for the user
			ProtocolConstants userProtocolConstant = protocolService.getProtocolForPatientUserId(userId);
			
			// Getting all the sessions of user from the repository 
			List<TherapySession> therapySessionData = therapySessionRepository.findByPatientUserId(userId);
			// grouping all the therapy sessions to the date
			SortedMap<LocalDate,List<TherapySession>> sortedTherapy = groupTherapySessionsByDate(therapySessionData);
			
			// Getting all the notification for the user 
			List<Notification> userNotifications = notificationRepository.findByPatientUserId(userId);			
			
			int adherenceSettingDay = getAdherenceSettingForUserId(userId);
			
			for(PatientCompliance currentCompliance : patientComplianceList){
				
				//PatientCompliance currentCompliance = patientComplianceRepository.findById(compliance.getId());
				
				PatientInfo patient = currentCompliance.getPatient();
				User patientUser = currentCompliance.getPatientUser();
				int initialPrevScoreFor1Day = 0;
				
				Notification existingNotificationofTheDay = notificationService.getNotificationForDay(userNotifications, currentCompliance.getDate());
				
				if( ( adherenceStartDate.isBefore(currentCompliance.getDate()) || adherenceStartDate.equals(currentCompliance.getDate())) &&
						DateUtil.getDaysCountBetweenLocalDates(adherenceStartDate, currentCompliance.getDate()) < (adherenceSettingDay-1) && 
						adherenceSettingDay > 1){
							
					// Check whether the adherence start days is the compliance date
					if(adherenceStartDate.equals(currentCompliance.getDate())){
						if(resetFlag == 1 || (resetFlag == 2 && adherenceSettingDay != 1)){
							notificationService.createOrUpdateNotification(patientUser, patient, userId,
																		currentCompliance.getDate(), ADHERENCE_SCORE_RESET, false, existingNotificationofTheDay);
						}else{
							if(Objects.nonNull(existingNotificationofTheDay))
								notificationRepository.delete(existingNotificationofTheDay);
						}
						currentCompliance.setSettingsDeviatedDaysCount(0);
						currentCompliance.setMissedTherapyCount(0);
					} else {
						
						// Commenting the existing repository call and calling the the new method for getting the previous day compliance
						//PatientCompliance prevCompliance = patientComplianceRepository.returnPrevDayScore(currentCompliance.getDate().toString(),userId);
						PatientCompliance prevCompliance = returnPrevDayCompli(patientComplianceList, currentCompliance.getDate());
						if(Objects.isNull(prevCompliance)){
							prevCompliance = new PatientCompliance();
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
						//Notification existingNotificationofTheDay = notificationRepository.findByPatientUserIdAndDate(userId, currentCompliance.getDate());
						
						if(Objects.nonNull(existingNotificationofTheDay)){
							notificationRepository.delete(existingNotificationofTheDay);
						}
					}
					currentCompliance.setScore(adherenceScore);
					//patientComplianceRepository.save(currentCompliance);
					complianceListToStore.add(currentCompliance);
				}else{
					
					PatientCompliance prevCompliance = returnPrevDayCompli(patientComplianceList, currentCompliance.getDate());
					if(Objects.isNull(prevCompliance)){
						prevCompliance = new PatientCompliance();
						prevCompliance.setScore(adherenceScore);
						prevCompliance.setSettingsDeviatedDaysCount(0);
						prevCompliance.setMissedTherapyCount(0);
					}
					
					// Commenting the existing repository call and calling the the new method for getting the current day therapy details				
					//List<TherapySession> therapyData = therapySessionRepository.findByPatientUserIdAndDate(userId, currentCompliance.getDate());					
					List<TherapySession> therapyData = getTherapyForDay(sortedTherapy, currentCompliance.getDate());
					
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
			complianceService.saveAll(complianceListToStore);
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotification("resetAdherenceCalculationPatient",writer.toString());
		}
		return "Adherence score reset successfully";
	}
	
	// Setting the previous day compliance
	//private void setPrevDayCompliance(PatientCompliance currentCompliance, Long userId, PatientCompliance preDayCompliance)
	private PatientCompliance setPrevDayCompliance(PatientCompliance currentCompliance, Long userId, PatientCompliance preDayCompliance)
	{
		// Commented the repository call and getting the previous day compliance from the method parameter
		//PatientCompliance preDayCompliance = patientComplianceRepository.returnPrevDayScore(currentCompliance.getDate().toString(),userId);
		
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
		
		//patientComplianceRepository.save(currentCompliance);
		return currentCompliance;
		
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
			Map<Long, Notification> notificationMap) {
		for(Long patientUserId : hmrNonComplianceMap.keySet()){
			PatientCompliance newCompliance = hmrNonComplianceMap.get(patientUserId);
			
			int score = newCompliance.getScore();
			int adherenceSettingDay = getAdherenceSettingForUserId(patientUserId);
			List<TherapySession> therapySessions = getLastSettingDaysTherapiesForUserId(patientUserId,getPlusOrMinusTodayLocalDate(-(adherenceSettingDay-1)),today); 
			
			if(Objects.isNull(therapySessions)){
				therapySessions = new LinkedList<>();
			}
			
			int hmrRunRate = calculateHMRRunRatePerSession(therapySessions);
			newCompliance.setHmrRunRate(hmrRunRate);
			double durationForSettingDays = hmrRunRate*therapySessions.size(); // runrate*totalsessions = total duration
			ProtocolConstants protocolConstant = userProtocolConstantsMap.get(patientUserId);

			if(!isHMRCompliant(protocolConstant, durationForSettingDays, adherenceSettingDay)){
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
	//private void calculateUserHMRComplianceForMST(
	private PatientCompliance calculateUserHMRComplianceForMST(
			PatientCompliance newCompliance,
			ProtocolConstants userProtocolConstants,
			LocalDate complianceDate,
			Long userId,
			PatientInfo patient,
			User patientUser,
			Integer adherenceSettingDay,
			int initialPrevScoreFor1Day,
			SortedMap<LocalDate,List<TherapySession>> sortedTherapy,
			PatientCompliance prevCompliance,
			Notification existingNotificationofTheDay,
			Integer resetFlag) {
		
		// Commented the repository call for previous day compliance and getting the prevCompliance from the method parameter
		// Getting previous day score or adherence reset score for the adherence setting value as 1
		// PatientCompliance prevCompliance = patientComplianceRepository.returnPrevDayScore(complianceDate.toString(),userId);
		int score = initialPrevScoreFor1Day == 0 ? prevCompliance.getScore() : initialPrevScoreFor1Day;
		
		// Get earlier third day to finding therapy session
		LocalDate adherenceSettingDaysEarlyDate = getDateBeforeSpecificDays(complianceDate,(adherenceSettingDay-1));
		
		//Commented the repository call for current day therapy session and getting the same from the method parameter and the calling method
		// Get therapy session for last adherence Setting days
		// List<TherapySession> therapySessions = therapySessionRepository.findByDateBetweenAndPatientUserId(adherenceSettingDaysEarlyDate, complianceDate, userId);
		List<TherapySession> therapySessions = getTherapyforBetweenDates(adherenceSettingDaysEarlyDate, complianceDate, sortedTherapy);
				
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
				notificationRepository.delete(existingNotificationofTheDay);
			newCompliance.setScore(score);
			return newCompliance;
		}
		
		if(resetFlag == 1 || (resetFlag == 2 && adherenceSettingDay != 1)){
			notification_type = initialPrevScoreFor1Day == 0 ? notification_type : ADHERENCE_SCORE_RESET;
		}			
		
		// Added the existingNotificationofTheDay param for passing the notification object for the current date
		notificationService.createOrUpdateNotification(patientUser, patient, userId,
				complianceDate, notification_type, false, existingNotificationofTheDay);
		
		// Setting the new score with respect to the compliance deduction
		newCompliance.setScore(score);
		
		// Saving the updated score for the specific date of compliance
		//patientComplianceRepository.save(newCompliance);
		return newCompliance;
	}
		
	
	
	// calculate score with respective to missed therapies
	//private void calculateUserMissedTherapy(
	private PatientCompliance calculateUserMissedTherapy(
			PatientCompliance newCompliance,
			LocalDate complianceDate,
			Long userId,
			PatientInfo patient,
			User patientUser,
			int initialPrevScoreFor1Day,
			PatientCompliance prevCompliance,
			Notification existingNotificationofTheDay) {
				
		// existingNotificationofTheDay object of Notification is sent to avoid repository call
		// userNotifications list object is sent to the method for getting the current day object
		notificationService.createOrUpdateNotification(patientUser, patient, userId,
				complianceDate, (initialPrevScoreFor1Day == 0 ? MISSED_THERAPY : ADHERENCE_SCORE_RESET) , false, existingNotificationofTheDay);
		
		// Commenting the repository call for previous day compliance by passing prevCompliance in the parameter
		// Getting previous day score or adherence reset score for the adherence setting value as 1
		//PatientCompliance prevCompliance = patientComplianceRepository.returnPrevDayScore(complianceDate.toString(),userId);
		
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
		//patientComplianceRepository.save(newCompliance);
	
	}
		
	/**
	 * Get the therapy data between days and user ids
	 * @param patientUserId, from date and to date
	 * @return
	 */
	public List<TherapySession> getLastSettingDaysTherapiesForUserId(Long patientUserId,LocalDate from,LocalDate to){
		List<TherapySession> therapySessions = therapySessionRepository.findByDateBetweenAndPatientUserId(from, to, patientUserId);
		return therapySessions;
	}	
	
	/**
	 * Calculate HMRRunRate For PatientUsers
	 * @param patientUserIds
	 * @return
	 */
	public Map<Long,List<TherapySession>> getLastSettingDaysTherapiesGroupByUserId(List<Long> patientUserIds,LocalDate from,LocalDate to){
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
			User patientUser, int totalDuration, int adherenceSettingDay) throws Exception{
		noEventService.updatePatientFirstTransmittedDate(patientUser.getId(), currentTherapySessionDate);
		PatientCompliance currentCompliance = new PatientCompliance(DEFAULT_COMPLIANCE_SCORE, currentTherapySessionDate,
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
			ProtocolConstants protocolConstant,
			SortedMap<LocalDate, PatientCompliance> existingComplianceMap,
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap,
			Integer adherenceSettingDay) throws Exception{
		
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
		
		//hill-1956
		LocalDate firstresetDate = null;
		LocalDate lastresetDate = allDates.get(allDates.size()-1);
		//hill-1956
				
				
		for(LocalDate therapyDate : allDates){
			
			//hill-1956
			// query to find the adherence reset for the corresponding therapydate
			List<AdherenceReset> adherenceResetList = adherenceResetRepository.findOneByPatientUserIdAndResetStartDate(patientUser.getId(),therapyDate);
			
			//if any adherence reset is found stop the adherence  calculation and set the therapy date as first resetdate
			if(Objects.nonNull(adherenceResetList) && adherenceResetList.size() > 0)
			{
				firstresetDate = therapyDate;
				break;
			}
			//hill-1956
			
			
			// First Transmission Date to be updated
			if(firstTransmittedDate.isAfter(therapyDate)){
				noEventService.updatePatientFirstTransmittedDate(patientUser.getId(),therapyDate);
				firstTransmittedDate = therapyDate;
			}
			
			int daysBetween = DateUtil.getDaysCountBetweenLocalDates(firstTransmittedDate, therapyDate);
			List<TherapySession> latestSettingDaysTherapySessions = prepareTherapySessionsForLastSettingdays(therapyDate,
					existingTherapySessionMap,receivedTherapySessionsMap,adherenceSettingDay);
			
			double hmr = getLatestHMR(existingTherapySessionMap, receivedTherapySessionsMap,therapyDate,
					latestSettingDaysTherapySessions);
			
			int hmrRunrate = calculateHMRRunRatePerSession(latestSettingDaysTherapySessions);
			
			LocalDate lastTransmissionDate = getLatestTransmissionDate(
					existingTherapySessionMap,receivedTherapySessionsMap, therapyDate);
			int missedTherapyCount = 0;
			if( (daysBetween <= 1 && adherenceSettingDay > 1 ) || (daysBetween == 0 && adherenceSettingDay == 1) ){ // first transmit
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
				PatientCompliance compliance = getLatestCompliance(patientUser, patient,
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
			List<AdherenceReset> adherenceResetList = adherenceResetRepository.findOneByPatientUserIdAndResetStartDates(patientUser.getId(),firstresetDate,lastresetDate);
			
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
			LocalDate date, List<TherapySession> latestSettingDaysTherapySessions) throws Exception{
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
			ProtocolConstants protocolConstant,
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
			List<TherapySession> latestSettingDaysTherapySessions = prepareTherapySessionsForLastSettingdays(latestCompliance.getDate(),
					existingTherapySessionMap,receivedTherapySessionsMap,adherenceSettingDay);
			Map<String,Double> therapyMetrics = calculateTherapyMetricsPerSettingDays(latestSettingDaysTherapySessions);
			
			boolean isHMRCompliant = isHMRCompliant(protocolConstant, therapyMetrics.get(TOTAL_DURATION),adherenceSettingDay);
			boolean isSettingsDeviated = false;
			
			// Settings deviated to be calculated only on Therapy done days
			if(currentMissedTherapyCount == 0){
				isSettingsDeviated = isSettingsDeviatedForSettingDays(latestSettingDaysTherapySessions, protocolConstant, adherenceSettingDay);
				applySettingsDeviatedDaysCount(latestCompliance, complianceMap,
						isSettingsDeviated, adherenceSettingDay);
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
			boolean isSettingsDeviated, Integer adherenceSettingDay) throws Exception{
		int settingsDeviatedDaysCount;
		if(isSettingsDeviated){
			int previousSettingsDeviatedDaysCount = 0;
			SortedMap<LocalDate,PatientCompliance> mostRecentComplianceMap = complianceMap.headMap(latestCompliance.getDate());
			if(mostRecentComplianceMap.size() > 0){
				PatientCompliance previousCompliance = mostRecentComplianceMap.get(mostRecentComplianceMap.lastKey());
				previousSettingsDeviatedDaysCount = previousCompliance.getSettingsDeviatedDaysCount();
			}
			// If settingsDeviationDaysCount is 0 for previous date, settingsDeviationDaysCount would be default value. increments thereafter
			settingsDeviatedDaysCount =  previousSettingsDeviatedDaysCount == 0 ? adherenceSettingDay :++previousSettingsDeviatedDaysCount;
			latestCompliance.setSettingsDeviatedDaysCount(settingsDeviatedDaysCount);
		}
	} 
	
	private List<TherapySession> prepareTherapySessionsForLastSettingdays(
			LocalDate currentTherapyDate,
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap,
			Integer adherenceSettingDay) {
		List<TherapySession> therapySessions = new LinkedList<>();
		for(int i = 0;i < adherenceSettingDay;i++){
			List<TherapySession> previousExistingTherapySessions = existingTherapySessionMap.get(currentTherapyDate.minusDays(i));
			if(Objects.nonNull(previousExistingTherapySessions))
				therapySessions.addAll(previousExistingTherapySessions);
		}
		return therapySessions;
	}
	
	private boolean isSettingsDeviatedForSettingDays(List<TherapySession> lastSettingDaysTherapySessions,
			ProtocolConstants protocol, Integer adherenceSettingDay){
		Map<LocalDate, List<TherapySession>> lastSettingDaysTherapySessionMap = lastSettingDaysTherapySessions
				.stream().collect(
						Collectors.groupingBy(TherapySession::getDate));
		boolean isSettingsDeviated = false;
		// This is for checking settings deviation, settings deviation should be calculated for consecutive adherence setting days
		//(exclusive missed therapy)
		if(lastSettingDaysTherapySessionMap.keySet().size() == adherenceSettingDay){
			for(LocalDate d : lastSettingDaysTherapySessionMap.keySet()){
				List<TherapySession> therapySeesionsPerDay = lastSettingDaysTherapySessionMap.get(d);
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
	
	private boolean isSettingDeviatedForUserOnDay(Long userId, LocalDate complianceDate,Integer adherenceSettingDay, ProtocolConstants userProtocolConstant){
		// Get earlier third day to finding therapy session
		LocalDate adherenceSettingDaysEarlyDate = getDateBeforeSpecificDays(complianceDate,(adherenceSettingDay-1));
		
		// Get therapy session for last adherence Setting days
		List<TherapySession> therapySessions = therapySessionRepository.findByDateBetweenAndPatientUserId(adherenceSettingDaysEarlyDate, complianceDate, userId);
				
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
}