package com.hillrom.monarch.service;

import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateWeightedAvg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.hillrom.monarch.repository.PatientMonarchDeviceDataRepository;
import com.hillrom.monarch.repository.PatientNoEventsMonarchRepository;
import com.hillrom.monarch.repository.TherapySessionMonarchRepository;
import com.hillrom.monarch.service.util.PatientVestDeviceTherapyUtilMonarch;
import com.hillrom.monarch.web.rest.dto.TherapyDataMonarchVO;
import com.hillrom.vest.domain.NoteMonarch;
import com.hillrom.vest.domain.PatientComplianceMonarch;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.ProtocolConstantsMonarch;
import com.hillrom.vest.domain.TherapySessionMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.service.AdherenceCalculationService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.TreatmentStatisticsVO;

@Service
@Transactional
public class TherapySessionServiceMonarch {

	@Inject
	private TherapySessionMonarchRepository therapySessionMonarchRepository;

	@Inject
	private AdherenceCalculationServiceMonarch adherenceCalculationServiceMonarch;
	
	@Inject
	@Lazy
	private AdherenceCalculationService adherenceCalculationService;
	
	@Inject
	private PatientComplianceMonarchService complianceServiceMonarch;
	
	@Inject
	private NoteServiceMonarch noteServiceMonarch;
	
	/*@Inject
	private PatientNoEventService patientNoEventService;*/
	
	@Inject
	private PatientNoEventMonarchService patientNoEventMonarchService;

	@Inject
	private PatientNoEventsMonarchRepository patientNoEventRepositoryMonarch;
	
	@Inject
	private PatientMonarchDeviceDataRepository patientMonarchDeviceDataRepository;

	public List<TherapySessionMonarch> saveOrUpdate(List<TherapySessionMonarch> therapySessionsMonarch) throws Exception{
		if(therapySessionsMonarch.size() > 0){			
			User patientUser = therapySessionsMonarch.get(0).getPatientUser();
			PatientInfo patient = therapySessionsMonarch.get(0).getPatientInfo();
			// removeExistingTherapySessions(therapySessions, patientUser);
			Map<LocalDate, List<TherapySessionMonarch>> groupedTherapySessions = therapySessionsMonarch
					.stream()
					.collect(
							Collectors
							.groupingBy(TherapySessionMonarch::getDate));
			SortedMap<LocalDate,List<TherapySessionMonarch>> receivedTherapySessionMapMonarch = new TreeMap<>(groupedTherapySessions);
			ProtocolConstantsMonarch protocol = adherenceCalculationServiceMonarch.getProtocolByPatientUserId(patientUser.getId());
			PatientNoEventMonarch patientNoEvent = patientNoEventRepositoryMonarch.findByPatientUserId(patientUser.getId());
			SortedMap<LocalDate,List<TherapySessionMonarch>> existingTherapySessionMap = getAllTherapySessionsMapByPatientUserId(patientUser.getId());
			SortedMap<LocalDate,PatientComplianceMonarch> existingComplianceMapMonarch = complianceServiceMonarch.getPatientComplainceMapByPatientUserId(patientUser.getId());
			
			
			String deviceType = adherenceCalculationService.getDeviceTypeValue(patient.getId());
			
			if(deviceType.equals("MONARCH")){
				adherenceCalculationServiceMonarch.processAdherenceScore(patientNoEvent, existingTherapySessionMap, 
					receivedTherapySessionMapMonarch, existingComplianceMapMonarch,protocol);
			}else if(deviceType.equals("BOTH")){				
				// Merged Protocol for the both device patients
				ProtocolConstants protocolMergedVest = adherenceCalculationService.getProtocolByPatientUserId(patientUser.getId());
				// Convert Merged protocol with Vest object to monarch object
				ProtocolConstantsMonarch protocolMergedMonarch = convertVestToMonarchProtocol(protocolMergedVest);
				
				adherenceCalculationServiceMonarch.processAdherenceScore(patientNoEvent, existingTherapySessionMap, 
						receivedTherapySessionMapMonarch, existingComplianceMapMonarch,protocolMergedMonarch,patientUser.getId());
			}
		}
		return therapySessionsMonarch;
	}
	
	public ProtocolConstantsMonarch convertVestToMonarchProtocol(ProtocolConstants protocol){
		ProtocolConstantsMonarch convertFromVestProtocol = new ProtocolConstantsMonarch(protocol.getMaxFrequency(), protocol.getMinFrequency(),
				protocol.getMaxPressure(), protocol.getMinPressure(),protocol.getTreatmentsPerDay(),protocol.getMinDuration());  
		return convertFromVestProtocol;
	}
	
	/*public void removeExistingTherapySessions(
			List<TherapySessionMonarch> therapySessions, User patientUser) {
		TherapySession latestTherapySession =  therapySessionRepository.findTop1ByPatientUserIdOrderByEndTimeDesc(patientUser.getId());
		// Removing existing therapySessions from DB
		if(Objects.nonNull(latestTherapySession)){
			Iterator<TherapySessionMonarch> tpsIterator = therapySessions.iterator();
			while(tpsIterator.hasNext()){
				TherapySessionMonarch tps = tpsIterator.next();
				// Remove previous therapy Sessions
				if(tps.getDate().isBefore(latestTherapySession.getDate())){
					tpsIterator.remove();
					//Remove previous therapySessions of the same day.
				} else {
					DateTime tpsStartTime = tps.getStartTime();
					DateTime latestTpsEndTimeFromDB = latestTherapySession.getEndTime();
					if(tps.getDate().equals(latestTherapySession.getDate()) && tpsStartTime.isBefore(latestTpsEndTimeFromDB)){
						tpsIterator.remove();
					}
				}
			}
		}
	}*/
	
	public List<TherapyDataMonarchVO> findByPatientUserIdAndDateRange(Long patientUserId,LocalDate from,LocalDate to){
		List<TherapySessionMonarch> sessions = therapySessionMonarchRepository
				.findByPatientUserIdAndDateRange(patientUserId,from,to);
		Map<LocalDate, NoteMonarch> dateNotesMap = noteServiceMonarch.findByPatientUserIdAndCreatedOnBetweenGroupByCreatedOn(patientUserId, from, to, false);
		Map<LocalDate,List<TherapySessionMonarch>> tpsGroupByDate = groupTherapySessionsByDate(sessions);
		return formatResponse(tpsGroupByDate, dateNotesMap, patientUserId, from, to);
	}
	
	public SortedMap<LocalDate,List<TherapySessionMonarch>> groupTherapySessionsByDate(List<TherapySessionMonarch> therapySessions){
		return new TreeMap<>(therapySessions.stream().collect(Collectors.groupingBy(TherapySessionMonarch :: getDate)));
	}
	
	public List<TherapySessionMonarch> findByPatientUserIdAndDate(Long id,LocalDate date){
		return  therapySessionMonarchRepository.findByPatientUserIdAndDate(id,date);
	}	
	
	public Map<Long,List<TherapySessionMonarch>> getTherapySessionsGroupByPatientUserId(List<Long> patientUserIds){
		List<TherapySessionMonarch> therapySessions = therapySessionMonarchRepository.findTop1ByPatientUserIdInOrderByEndTimeDesc(patientUserIds);
		return therapySessions.stream().collect(Collectors.groupingBy(TherapySessionMonarch::getTherapySessionByPatientUserId));
	}

	public Collection<TreatmentStatisticsVO> getTreatmentStatisticsByPatientUserIdsAndDuration(
			List<Long> patientUserIds,
			LocalDate from,LocalDate to) {
		List<TherapySessionMonarch> therapySessions = therapySessionMonarchRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<LocalDate,List<TherapySessionMonarch>> tpsGroupedByDate = therapySessions.stream().collect(Collectors.groupingBy(TherapySessionMonarch :: getDate));
			return getAvgTreatmentStatisticsForTherapiesGroupedByDate(patientUserIds, tpsGroupedByDate);
	}

	public LinkedList<TreatmentStatisticsVO> getAvgTreatmentStatisticsForTherapiesGroupedByDate(List<Long> patientUserIds,
			Map<LocalDate, List<TherapySessionMonarch>> tpsGroupedByDate) {
		Map<LocalDate, TreatmentStatisticsVO> statisticsMap = new TreeMap<>();
		TreatmentStatisticsVO statisticsVO;
		for(LocalDate date : tpsGroupedByDate.keySet()){
			List<TherapySessionMonarch> tpsOnDate = tpsGroupedByDate.get(date);
			statisticsVO = calculateAvgTreatmentStatistics(patientUserIds,
						tpsOnDate);
			statisticsMap.put(date, statisticsVO);
		}
		return new LinkedList<>(statisticsMap.values());
	}
	
	public TreatmentStatisticsVO calculateAvgTreatmentStatistics(
			List<Long> patientUserIds, List<TherapySessionMonarch> tpsInDuration) {
		TreatmentStatisticsVO statisticsVO;
		int avgTreatment = tpsInDuration.size()/patientUserIds.size();
		int avgDuration = tpsInDuration
				.stream()
				.collect(
						Collectors
								.summingInt(TherapySessionMonarch::getDurationInMinutes))
				/ patientUserIds.size();
		DateTime startTime = tpsInDuration.get(0).getStartTime();
		DateTime endTime = tpsInDuration.get(tpsInDuration.size()-1).getEndTime();
		statisticsVO = new TreatmentStatisticsVO(avgTreatment,avgDuration,startTime,endTime);
		return statisticsVO;
	}

	/**
	 * prepare dummy therapy data for the week
	 * @param from
	 * @param to
	 * @param dummyData
	 */
	private List<TherapyDataMonarchVO> prepareTherapySessionsAddMissedTherapyData(Long patientUserId,
			LocalDate from, LocalDate to,
			Map<LocalDate, List<TherapyDataMonarchVO>> therapySessionMap,
			Map<LocalDate,NoteMonarch> noteMap,
			TherapySessionMonarch latestTherapySession) {
		int minutes = 60*60;
		// Get the latest HMR for the user before the requested duration
		double hmrInHours = Objects.nonNull(latestTherapySession)?latestTherapySession.getHmr()/minutes:0d;
		
		// This is to discard the records if the user requested data beyond his/her first transmission date.
		PatientNoEventMonarch patientNoEvent = patientNoEventMonarchService.findByPatientUserId(patientUserId);
		if(Objects.nonNull(patientNoEvent) && Objects.nonNull(patientNoEvent.getFirstTransmissionDate()))
			from = from.isAfter(patientNoEvent.getFirstTransmissionDate()) ? from : patientNoEvent.getFirstTransmissionDate();
		// Prepare the list of dates to which data has to be shown
		List<LocalDate> dates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		List<TherapyDataMonarchVO> processedTherapies = new LinkedList<>();
		for(LocalDate date : dates){
			// insert therapy done by user
			if(Objects.nonNull(therapySessionMap.get(date))){
				List<TherapyDataMonarchVO> therapySessions = therapySessionMap.get(date);
				therapySessions.forEach(therapy -> {
					therapy.setNoteMonarch(noteMap.get(date));
					processedTherapies.add(therapy);
				});
				// updating HMR from previous day to form step graph
				hmrInHours = therapySessions.get(therapySessions.size()-1).getHmr();
			}else if(date.isBefore(LocalDate.now())){ // Don't consider current date as missed therapy
				// add missed therapy if user misses the therapy
				TherapyDataMonarchVO missedTherapy = createTherapyDataWithTimeStamp(date);
				missedTherapy.setNoteMonarch(noteMap.get(date));
				missedTherapy.setHmr(hmrInHours);
				processedTherapies.add(missedTherapy);
			}
		}
		// Sort based on timestamp
		Collections.sort(processedTherapies);
		return processedTherapies;
	}

	/**
	 * create Dummy therapy data object for missing therapy
	 * @param from
	 * @return
	 */
	private TherapyDataMonarchVO createTherapyDataWithTimeStamp(LocalDate from) {
			TherapyDataMonarchVO therapy = new TherapyDataMonarchVO();
			therapy.setMissedTherapy(true);
			therapy.setTimestamp(from.toDateTimeAtCurrentTime());
			return therapy; 
	}
	
	private List<TherapyDataMonarchVO> formatResponse(Map<LocalDate,List<TherapySessionMonarch>> sessionMap,
			Map<LocalDate, NoteMonarch> noteMap,Long patientUserId,LocalDate from,LocalDate to){
		TherapySessionMonarch latestTherapySession = therapySessionMonarchRepository.findTop1ByPatientUserIdAndDateBeforeOrderByEndTimeDesc(patientUserId,from);
		Map<LocalDate, List<TherapyDataMonarchVO>> therapyDataMap = assignNotesToTherapySession(
				sessionMap, noteMap);
		if(sessionMap.isEmpty() && noteMap.isEmpty()){
			if(Objects.nonNull(latestTherapySession)){
				return prepareTherapySessionsAddMissedTherapyData(patientUserId,from, to, therapyDataMap,noteMap,latestTherapySession);
			}else{
				return new ArrayList<>();
			}
		}else {
			return prepareTherapySessionsAddMissedTherapyData(patientUserId,from, to, therapyDataMap,noteMap,latestTherapySession);
		}
	}

	private Map<LocalDate, List<TherapyDataMonarchVO>> assignNotesToTherapySession(
			Map<LocalDate, List<TherapySessionMonarch>> sessionMap,
			Map<LocalDate, NoteMonarch> noteMap) {
		Map<LocalDate,List<TherapyDataMonarchVO>> therapyDataMap = new TreeMap<>();
		TherapyDataMonarchVO therapyDataVO = null;
		int minutes = 60*60;
		for(LocalDate date : sessionMap.keySet()){
			List<TherapySessionMonarch> sessionsPerDate = sessionMap.get(date);
			List<TherapyDataMonarchVO> therapyDataVOs = therapyDataMap.get(date);
			if(Objects.isNull(therapyDataVOs))
				therapyDataVOs = new LinkedList<>();
			for(TherapySessionMonarch session: sessionsPerDate){
				int programmedCoughPauses = session.getProgrammedCaughPauses()==null?0:session.getProgrammedCaughPauses();
				int normalCoughPauses = session.getNormalCaughPauses();

				
				   DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
				   String startTime = fmt.print(session.getStartTime());
				   String endTime = fmt.print(session.getEndTime());
				
				List<PatientVestDeviceDataMonarch> sessionEvents = patientMonarchDeviceDataRepository.returnByPatientIdAndTimeStampBetween(session.getPatientInfo().getId(),startTime,endTime);
				
				List <Integer> errorList = new LinkedList<>();
				List <Integer> bluetoothSource = new LinkedList<>();
				List <String> powerConnectStatus = new LinkedList<>();
				
				String EVENT_CODE_ERROR_MONARCH = "22";
				String EVENT_CODE_BT_CHANGE_SOURCE_MONARCH = "23";
				String EVENT_CODE_POWER_CONNECTED_MONARCH = "24";
				String EVENT_CODE_POWER_DISCONNECTED_MONARCH = "25";
				
				for(PatientVestDeviceDataMonarch sessionEvent : sessionEvents){					
					// Bluetooth source change
					if(sessionEvent.getEventCode().equals(EVENT_CODE_BT_CHANGE_SOURCE_MONARCH)){
						bluetoothSource.add(sessionEvent.getIntensity());
					}else {
						if(sessionEvent.getEventCode().equals(EVENT_CODE_POWER_CONNECTED_MONARCH) || 
								sessionEvent.getEventCode().equals(EVENT_CODE_POWER_DISCONNECTED_MONARCH)){					
							powerConnectStatus.add(sessionEvent.getEventCode());												
						}
						else if(sessionEvent.getEventCode().equals(EVENT_CODE_ERROR_MONARCH)){
							errorList.add(sessionEvent.getIntensity());
						}
					}
				}
				
				therapyDataVO = new TherapyDataMonarchVO(session.getStartTime(), sessionsPerDate.size(),session.getSessionNo(), 
						session.getFrequency(),	session.getIntensity(), programmedCoughPauses, normalCoughPauses,
						programmedCoughPauses+normalCoughPauses, noteMap.get(date), session.getStartTime(),
						session.getEndTime(), session.getCaughPauseDuration(),
						session.getDurationInMinutes(), session.getHmr().doubleValue()/minutes,false,session.getStartBatteryLevel(),session.getEndBatteryLevel(),errorList,bluetoothSource,powerConnectStatus);
				therapyDataVOs.add(therapyDataVO);
			}
			therapyDataMap.put(date, therapyDataVOs);
		}
		return therapyDataMap;
	} 
	
	public SortedMap<LocalDate,List<TherapySessionMonarch>> getAllTherapySessionsMapByPatientUserId(Long patientUserId){
		List<TherapySessionMonarch> therapySessions =  therapySessionMonarchRepository.findByPatientUserId(patientUserId);
		return groupTherapySessionsByDate(therapySessions);
	}
	
	public List<TherapyDataMonarchVO> getComplianceGraphData(Long patientUserId,LocalDate from,LocalDate to){
		List<TherapyDataMonarchVO> therapyData = findByPatientUserIdAndDateRange(patientUserId, from, to);
		Map<LocalDate,List<TherapyDataMonarchVO>> therapyDataMap = therapyData.stream().collect(Collectors.groupingBy(TherapyDataMonarchVO::getDate));
		SortedMap<LocalDate,List<TherapyDataMonarchVO>> therapyDataGroupByDate = new TreeMap<>(therapyDataMap);
		List<TherapyDataMonarchVO> responseList = new LinkedList<>();
		for(LocalDate date : therapyDataGroupByDate.keySet()){
			List<TherapyDataMonarchVO> therapies = therapyDataGroupByDate.get(date);
			int totalDuration = therapies.stream().collect(Collectors.summingInt(TherapyDataMonarchVO::getDuration));
			int programmedCoughPauses=0,normalCoughPauses=0,coughPauseDuration=0;
			float weightedAvgFrequency = 0.0f,weightedAvgIntensity = 0.0f;
			NoteMonarch noteForTheDay = null;
			int size = therapies.size();
			DateTime start = therapies.get(0).getStart();
			DateTime end = size > 0 ? therapies.get(size-1).getEnd(): null;
			double hmr = size > 0 ? therapies.get(size-1).getHmr(): 0;
			boolean isMissedTherapy = therapies.get(0).isMissedTherapy();
			for(TherapyDataMonarchVO therapy : therapies){
				programmedCoughPauses += therapy.getProgrammedCoughPauses();
				normalCoughPauses += therapy.getNormalCoughPauses();
				weightedAvgFrequency += calculateWeightedAvg(totalDuration, therapy.getDuration(), therapy.getFrequency());
				weightedAvgIntensity += calculateWeightedAvg(totalDuration, therapy.getDuration(), therapy.getIntensity());
				noteForTheDay = therapy.getNoteMonarch();
			}
			int minutes = 60*60;
			TherapyDataMonarchVO dataVO = new TherapyDataMonarchVO(therapies.get(0).getTimestamp(), Math.round(weightedAvgFrequency), Math.round(weightedAvgIntensity),
					programmedCoughPauses, normalCoughPauses, programmedCoughPauses+normalCoughPauses, noteForTheDay, start, end, coughPauseDuration,
					totalDuration, Math.round(hmr/minutes), isMissedTherapy);
			responseList.add(dataVO);
		}
		return responseList;
	}
	
	public void saveAll(Collection<TherapySessionMonarch> therapySessionMonarch){
		therapySessionMonarchRepository.save(therapySessionMonarch);
	}
}
