package com.hillrom.vest.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;
import static com.hillrom.vest.config.AdherenceScoreConstants.HMR_NON_COMPLIANCE_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.MISSED_THERAPY_POINTS;
import static com.hillrom.vest.config.AdherenceScoreConstants.SETTING_DEVIATION_POINTS;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;
import static com.hillrom.vest.service.util.DateUtil.getDaysCountBetweenLocalDates;
import static com.hillrom.vest.service.util.DateUtil.getPlusOrMinusTodayLocalDate;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateCumulativeDuration;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateHMRRunRatePerDays;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateWeightedAvg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
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
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.ProtocolConstantsRepository;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.ClinicStatsNotificationVO;


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
	
	@Inject
	private ClinicService clinicService;
	
	@Inject
	private PatientHCPService patientHCPService;

	@Inject
	private UserRepository userRepository;
	
	@Inject
	private ClinicRepository clinicRepository;
	
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
		Map<String,Double> actualMetrics = calculateTherapyMetricsPer3Days(latest3TherapySessions);
		double latestHmr = therapySessionsPerDay.get(therapySessionsPerDay.size()-1).getHmr();
		// First Time received Data,hence compliance will be 100.
		if(latest3TherapySessions.isEmpty() || Objects.isNull(latestCompliance)){
			noEventService.updatePatientFirstTransmittedDate(patientUserId,currentTherapyDate);
			return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,
					actualMetrics.get("totalDuration").intValue(),true,false,latestHmr);
		}else{ 
			// Default 2 points get deducted by assuming data not received for the day, hence add 2 points
			currentScore = currentScore ==  DEFAULT_COMPLIANCE_SCORE ?  DEFAULT_COMPLIANCE_SCORE : currentScore + 2;
			TherapySession firstTherapySessionToPatient = therapySessionRepository.findTop1ByPatientUserIdOrderByDateAsc(patientUserId);
			// First 3 days No Notifications, hence compliance doesn't change
			if(threeDaysAgo.isBefore(firstTherapySessionToPatient.getDate())){
				if(latestCompliance.getDate().isBefore(currentTherapyDate)){
					return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,
							actualMetrics.get("totalDuration").intValue(),true,false,latestHmr);
				}
				latestCompliance.setScore(currentScore);
				return latestCompliance;
			}
		
			boolean isHMRComplianceViolated = isHMRComplianceViolated(protocolConstant, actualMetrics);

			boolean isSettingsDeviated = isSettingsDeviated(protocolConstant, actualMetrics);
			
			if(isSettingsDeviated(protocolConstant, actualMetrics)){
				currentScore -=  SETTING_DEVIATION_POINTS;
				notificationType =  SETTINGS_DEVIATION;				
			}				

			if(isHMRComplianceViolated){
				currentScore -=  HMR_NON_COMPLIANCE_POINTS;
				if(StringUtils.isBlank(notificationType))
					notificationType =  HMR_NON_COMPLIANCE;
				else
					notificationType =  HMR_AND_SETTINGS_DEVIATION;
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
				return new PatientCompliance(currentScore, currentTherapyDate, patient, patientUser,
						actualMetrics.get("totalDuration").intValue(),!(isHMRComplianceViolated),isSettingsDeviated,latestHmr);
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
				|| (protocolConstant.getMaxFrequency()*0.85) > actualMetrics.get("weightedAvgFrequency")
				|| (protocolConstant.getMaxFrequency()*1.15) < actualMetrics.get("weightedAvgFrequency")){
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
		actualMetrics.put("totalDuration", totalDuration/3);
		actualMetrics.put("treatmentsPerDay", treatmentsPerDay/3);
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
			for(PatientCompliance compliance : patientComplianceList){
				patientUserIds.add(compliance.getPatientUser().getId());
				PatientCompliance newCompliance = new PatientCompliance(
						today.toLocalDate(),
						compliance.getPatient(),
						compliance.getPatientUser(),
						compliance.getHmrRunRate(),
						compliance.getMissedTherapyCount()+1,
						compliance.getLatestTherapyDate(),
						Objects.nonNull(compliance.getHmr())?compliance.getHmr():0.0d);
				int score = Objects.isNull(compliance.getScore()) ? 0 : compliance.getScore(); 
				if(score > 0)
					newCompliance.setScore(score- MISSED_THERAPY_POINTS);
				int missedTherapyCount = compliance.getMissedTherapyCount();
				if(missedTherapyCount > 0 && missedTherapyCount % 3 == 0){
					notificationService.createOrUpdateNotification(compliance.getPatientUser(), compliance.getPatient(), compliance.getPatientUser().getId(),
							today.toLocalDate(), MISSED_THERAPY,false);
				}
				newComplianceList.add(newCompliance);
			}
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
	public void processPatientNotifications(){
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
					mailService.sendNotificationMailToPatient(patientUser,notificationType);
				}
			});
		}catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			mailService.sendJobFailureNotification("processPatientNotifications",writer.toString());
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
	 * Runs every 3pm , sends the statistics notifications to Clinic Admin and HCP.
	 * @throws HillromException 
	 */
	@Scheduled(cron="0 0 15 * * * ")
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
				if(cNotificationVO.getMissedTherapyCount() > 0 && cNotificationVO.getMissedTherapyCount() % 3 == 0)
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
			ProtocolConstants protocolConstant){
		for(LocalDate currentTherapySessionDate : receivedTherapySessionsMap.keySet()){
			List<TherapySession> receivedTherapySessions = receivedTherapySessionsMap.get(currentTherapySessionDate);
			double latestHmr = 0.0d;
			LocalDate firstTransmittedDate = null;
			LocalDate latestTherapyDate = null;
			PatientInfo patient = null;
			User patientUser = null;
			if(receivedTherapySessions.size() > 0){
				latestHmr = receivedTherapySessions.get(receivedTherapySessions.size()-1).getHmr();
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
				noEventService.updatePatientFirstTransmittedDate(patientUser.getId(), currentTherapySessionDate);
				PatientCompliance currentCompliance = new PatientCompliance(DEFAULT_COMPLIANCE_SCORE, currentTherapySessionDate,
						patient, patientUser,totalDuration/3,true,false,latestHmr);
				existingComplianceMap.put(currentTherapySessionDate, currentCompliance);
				calculateAdherenceScoreForTheDuration(patientUser,patient,firstTransmittedDate,
						currentTherapySessionDate,protocolConstant,existingComplianceMap,
						existingTherapySessionMap,receivedTherapySessionsMap);
			}else{ // User Transmitting data in Subsequent requests
				// data is sent in sorted order
				latestTherapyDate = existingTherapySessionMap.lastKey();
				if (Objects.nonNull(firstTransmittedDate) && Objects.nonNull(currentTherapySessionDate)
						&& firstTransmittedDate.isBefore(currentTherapySessionDate)){
					// Data sent in sorted order
					if(latestTherapyDate.isBefore(currentTherapySessionDate)){
						calculateAdherenceScoreForTheDuration(patientUser,patient,firstTransmittedDate,
								currentTherapySessionDate,protocolConstant,existingComplianceMap,
								existingTherapySessionMap,receivedTherapySessionsMap);
					}else{
						calculateAdherenceScoreForTheDuration(patientUser,patient,firstTransmittedDate,
								currentTherapySessionDate,protocolConstant,existingComplianceMap,
								existingTherapySessionMap,receivedTherapySessionsMap);
					} 
				}else{
					// Older data sent
					firstTransmittedDate = currentTherapySessionDate;
					noEventService.updatePatientFirstTransmittedDate(patientUser.getId(), currentTherapySessionDate);
					PatientCompliance currentCompliance = new PatientCompliance(DEFAULT_COMPLIANCE_SCORE, currentTherapySessionDate,
							patient, patientUser,totalDuration/3,true,false,latestHmr);
					existingComplianceMap.put(currentTherapySessionDate, currentCompliance);
					calculateAdherenceScoreForTheDuration(patientUser,patient,firstTransmittedDate,
							currentTherapySessionDate,protocolConstant,existingComplianceMap,
							existingTherapySessionMap,receivedTherapySessionsMap);
				}
			}
			// Save or update all compliance
			for(LocalDate date: existingComplianceMap.keySet()){
				complianceService.createOrUpdate(existingComplianceMap.get(date));
			}
 		}
	}

	private void calculateAdherenceScoreForTheDuration(
			User patientUser,
			PatientInfo patient,
			LocalDate firstTransmittedDate,
			LocalDate currentTherapyDate,
			ProtocolConstants protocolConstant,
			SortedMap<LocalDate, PatientCompliance> existingComplianceMap,
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap) {
		
		LocalDate latestComplianceDate = existingComplianceMap.lastKey();
		List<LocalDate> allDates = new LinkedList<>();
		// Older Data has been sent, hence recalculate compliance till date
		if(currentTherapyDate.isBefore(latestComplianceDate))
			allDates = DateUtil.getAllLocalDatesBetweenDates(currentTherapyDate, latestComplianceDate);
		else // Future Data has been sent 
			allDates = DateUtil.getAllLocalDatesBetweenDates(latestComplianceDate, currentTherapyDate);
		for(LocalDate date : allDates){
			// First Transmission Date to be updated
			if(firstTransmittedDate.isAfter(date)){
				noEventService.updatePatientFirstTransmittedDate(patientUser.getId(),date);
				firstTransmittedDate = date;
			}
			int daysBetween = DateUtil.getDaysCountBetweenLocalDates(firstTransmittedDate, date);
			List<TherapySession> latest3DaysTherapySessions = prepareTherapySessionsForLast3days(date,
					existingTherapySessionMap,receivedTherapySessionsMap);
			Map<String,Double> therapyMetrics = calculateTherapyMetricsPer3Days(latest3DaysTherapySessions);
			double hmr = 0;
			int hmrRunrate = 0;
			if(latest3DaysTherapySessions.size() > 0){
				hmr = latest3DaysTherapySessions.get(latest3DaysTherapySessions.size()-1).getHmr();
			}else{
				if(existingTherapySessionMap.size() > 0){
					SortedMap<LocalDate,List<TherapySession>> previousTherapySessionMap = existingTherapySessionMap.headMap(date);
					if(previousTherapySessionMap.size() > 0){
						List<TherapySession> mostRecentTherapySessions = previousTherapySessionMap.get(previousTherapySessionMap.lastKey());
						hmr = mostRecentTherapySessions.get(mostRecentTherapySessions.size()-1).getHmr();
					}
				} 	
			}
			if(Objects.nonNull(therapyMetrics.get("totalDuration"))){
				hmrRunrate = therapyMetrics.get("totalDuration").intValue();
			}
			SortedMap<LocalDate,List<TherapySession>> headMap = existingTherapySessionMap.headMap(date);
			LocalDate lastTransmissionDate = date;
			if(headMap.size()>0)
				lastTransmissionDate = headMap.lastKey();
			int missedTherapyCount = 0;
			if(daysBetween == 0){ // for first 3 days no deductions
				PatientCompliance compliance = existingComplianceMap.get(date);
				if(Objects.nonNull(compliance)){
					compliance.setScore(DEFAULT_COMPLIANCE_SCORE);
					compliance.setHmr(hmr);
					compliance.setHmrRunRate(hmrRunrate);
					compliance.setHmrCompliant(true);
					compliance.setSettingsDeviated(false);
					compliance.setMissedTherapyCount(0);
				}else{
					compliance = new PatientCompliance(DEFAULT_COMPLIANCE_SCORE, date,
							patient, patientUser,hmrRunrate,true,false,missedTherapyCount,lastTransmissionDate,hmr);
				}
				existingComplianceMap.put(date, compliance);
			}else{
				PatientCompliance compliance = existingComplianceMap.get(date);
				if(headMap.size() > 0){
					missedTherapyCount = DateUtil.getDaysCountBetweenLocalDates(lastTransmissionDate, date);
				}
				if(Objects.isNull(compliance)){
					SortedMap<LocalDate,PatientCompliance> previousComplianceMap = existingComplianceMap.headMap(date);
					PatientCompliance latestcompliance = previousComplianceMap.get(previousComplianceMap.lastKey());
					compliance = buildPatientCompliance(date, latestcompliance,missedTherapyCount);
				}
				processCompliance(compliance, therapyMetrics, existingComplianceMap,
						existingTherapySessionMap, receivedTherapySessionsMap, protocolConstant);
			}
		}
		List<TherapySession> newTherapySessions = new LinkedList<>();
		for(LocalDate date : receivedTherapySessionsMap.keySet()){
			List<TherapySession> sessionsTobeSaved = receivedTherapySessionsMap.get(date);
			existingTherapySessionMap.put(date, sessionsTobeSaved);
			newTherapySessions.addAll(sessionsTobeSaved);
		}
		therapySessionRepository.save(newTherapySessions);
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
		return compliance;
	}

	public void processCompliance(PatientCompliance latestCompliance,Map<String,Double> metricsMap,
			Map<LocalDate,PatientCompliance> complianceMap,
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap,
			ProtocolConstants protocolConstant){

		boolean isHMRComplianceViolated = isHMRComplianceViolated(protocolConstant, metricsMap);

		boolean isSettingsDeviated = isSettingsDeviated(protocolConstant, metricsMap);
		
		int currentScore = latestCompliance.getScore();
		int previousScore = currentScore;
		String notificationType = "";
		User patientUser = latestCompliance.getPatientUser();
		Long patientUserId = patientUser.getId();
		PatientInfo patient = latestCompliance.getPatient();
		
		// Missed Therapy 
		if(latestCompliance.getMissedTherapyCount() > 0){
			// Add MISSED_THERAPY_POINTS, if received Data for the same day
			if(Objects.nonNull(receivedTherapySessionsMap.get(latestCompliance.getDate()))){
				currentScore = currentScore <= DEFAULT_COMPLIANCE_SCORE - 2 ? currentScore + 2 : currentScore;
			}else{// deduct if therapy has been MISSED
				currentScore = currentScore >= 2 ? currentScore - 2 :currentScore;
			}
		}
		
		if(isSettingsDeviated && !latestCompliance.isSettingsDeviated() 
				&& latestCompliance.getMissedTherapyCount() == 0){
			currentScore -=  SETTING_DEVIATION_POINTS;
			notificationType =  SETTINGS_DEVIATION;				
		}				

		if(isHMRComplianceViolated && latestCompliance.isHmrCompliant()
				&& latestCompliance.getMissedTherapyCount() == 0){
			currentScore -=  HMR_NON_COMPLIANCE_POINTS;
			if(StringUtils.isBlank(notificationType))
				notificationType =  HMR_NON_COMPLIANCE;
			else
				notificationType =  HMR_AND_SETTINGS_DEVIATION;
		}
		
		if(previousScore < currentScore){
			notificationService.deleteNotification(patientUserId,latestCompliance.getDate());
			currentScore = currentScore !=  DEFAULT_COMPLIANCE_SCORE ? currentScore + 1 : DEFAULT_COMPLIANCE_SCORE;
		}
		
		// Point has been deducted due to Protocol violation
		if(previousScore > currentScore){
			notificationService.createOrUpdateNotification(patientUser, patient, patientUserId,
					latestCompliance.getDate(), notificationType,false);
		}

		// Compliance Score is non-negative
		currentScore = currentScore > 0? currentScore : 0;
		
		latestCompliance.setScore(currentScore);
		if(Objects.nonNull(metricsMap.get("totalDuration")))
			latestCompliance.setHmrRunRate(metricsMap.get("totalDuration").intValue());
		complianceMap.put(latestCompliance.getDate(), latestCompliance);
		
		List<TherapySession> newTherapySessions = new LinkedList<>();
		for(LocalDate date : receivedTherapySessionsMap.keySet()){
			List<TherapySession> sessionsTobeSaved = receivedTherapySessionsMap.get(date);
			existingTherapySessionMap.put(date, sessionsTobeSaved);
			newTherapySessions.addAll(sessionsTobeSaved);
		}
		therapySessionRepository.save(newTherapySessions);
	} 
	
	private List<TherapySession> prepareTherapySessionsForLast3days(
			LocalDate currentTherapyDate,
			SortedMap<LocalDate, List<TherapySession>> existingTherapySessionMap,
			SortedMap<LocalDate, List<TherapySession>> receivedTherapySessionsMap) {
		List<TherapySession> therapySessions = new LinkedList<>();
		for(int i = 1;i <= 3;i++){
			List<TherapySession> previousExistingTherapySessions = existingTherapySessionMap.get(currentTherapyDate.minusDays(i));
			List<TherapySession> previousReceivedTherapySessions = receivedTherapySessionsMap.get(currentTherapyDate.minusDays(i));
			if(Objects.nonNull(previousExistingTherapySessions))
				therapySessions.addAll(previousExistingTherapySessions);
			if(Objects.nonNull(previousReceivedTherapySessions))
				therapySessions.addAll(previousReceivedTherapySessions);
		}
		return therapySessions;
	}

/*	private void updateComplianceForDuration(
			LocalDate currentTherapyDate,LocalDate latestComplianceDate,
			LocalDate firstTransmissionDate,
			SortedMap<LocalDate, PatientCompliance> complianceMap,
			SortedMap<LocalDate, List<TherapySession>> therapySessionMap,
			PatientCompliance currentCompliance,
			ProtocolConstants protocolConstant) {
		LocalDate firstTherapyDate = null;
		LocalDate latestTherapyDate = null;
		if(Objects.nonNull(therapySessionMap)){
			firstTherapyDate = therapySessionMap.firstKey();
			latestTherapyDate = therapySessionMap.lastKey();
		}
		if(Objects.nonNull(latestTherapyDate) && Objects.nonNull(currentTherapyDate)){
			// Request received in sorted order, no old data
			if(latestTherapyDate.isBefore(currentTherapyDate)){
				List<LocalDate> allDates = DateUtil.getAllLocalDatesBetweenDates(latestTherapyDate.plusDays(1), currentCompliance.getDate());
				// possibilities are  nightly job has already deducted missed therapy points
				for(LocalDate date : allDates){
					PatientCompliance compliance =  complianceMap.get(date);
					// possibilities are  nightly job has already deducted missed therapy points
					if(Objects.nonNull(compliance) && (date.equals(currentTherapyDate) || date.isAfter(currentTherapyDate))){
						int score = compliance.getScore();
						if(score <= (DEFAULT_COMPLIANCE_SCORE -2))
							compliance.setScore(score+2);
					}else{
						// job hasn't run yet
						if(Objects.isNull(compliance)){
							PatientCompliance latestCompliance = complianceMap.get(complianceMap.lastKey());
							latestCompliance.setDate(date);
							compliance = latestCompliance;
						}
						// No missed Therapy Days
						if(date.equals(currentTherapyDate)){
							compliance = calculateComplianceScorePerDay(date,compliance,therapySessionMap,protocolConstant);
							complianceMap.put(date, compliance);
						}else{
							// got missed therapy days
							int score = compliance.getScore();
							if(score > 0)
								compliance.setScore(score - MISSED_THERAPY_POINTS);
							compliance.setDate(date);
							complianceMap.put(date, compliance);
						}
					}
				}
			}else if(Objects.nonNull(firstTherapyDate) && currentTherapyDate.isBefore(firstTherapyDate)){
				// Received old data
				List<LocalDate> allDates = DateUtil.getAllLocalDatesBetweenDates(firstTherapyDate, currentCompliance.getDate());
			}else {
				List<LocalDate> allDates = DateUtil.getAllLocalDatesBetweenDates(latestTherapyDate, currentCompliance.getDate());
			}
		}
	}
	
	public PatientCompliance calculateComplianceScorePerDay(LocalDate date,
			PatientCompliance compliance,
			Map<LocalDate,List<TherapySession>> therapySessionMap,
			ProtocolConstants protocolConstant){
		List<TherapySession> last3DaysTherapySessions = new LinkedList<>();
		for(int i = 1;i<=3;i++){
			List<TherapySession> therapySessions = therapySessionMap.get(date.plusDays(1));
			if(Objects.nonNull(therapySessions) && therapySessions.size() > 0)
				last3DaysTherapySessions.addAll(therapySessions);
		}
		Map<String,Double> actualMetrics  = calculateTherapyMetricsPer3Days(last3DaysTherapySessions);
		
		boolean isHMRComplianceViolated = isHMRComplianceViolated(protocolConstant, actualMetrics);

		boolean isSettingsDeviated = isSettingsDeviated(protocolConstant, actualMetrics);
		
		int currentScore = compliance.getScore();
		int previousScore = currentScore;
		String notificationType = "";
		User patientUser = compliance.getPatientUser();
		Long patientUserId = patientUser.getId();
		PatientInfo patient = compliance.getPatient();
		
		if(isSettingsDeviated){
			currentScore -=  SETTING_DEVIATION_POINTS;
			notificationType =  SETTINGS_DEVIATION;				
		}				

		if(isHMRComplianceViolated){
			currentScore -=  HMR_NON_COMPLIANCE_POINTS;
			if(StringUtils.isBlank(notificationType))
				notificationType =  HMR_NON_COMPLIANCE;
			else
				notificationType =  HMR_AND_SETTINGS_DEVIATION;
		}
		
		if(previousScore < currentScore){
			notificationService.deleteNotification(patientUserId,date);
			currentScore = currentScore !=  DEFAULT_COMPLIANCE_SCORE ? currentScore + 1 : DEFAULT_COMPLIANCE_SCORE;
		}
		
		// Point has been deducted due to Protocol violation
		if(previousScore > currentScore){
			notificationService.createOrUpdateNotification(patientUser, patient, patientUserId,
					date, notificationType,false);
		}

		// Compliance Score is non-negative
		currentScore = currentScore > 0? currentScore : 0;
		
		compliance.setScore(currentScore);
		compliance.setHmrRunRate(actualMetrics.get("totalDuration").intValue());
		
		return compliance;
	}*/
}