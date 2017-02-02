package com.hillrom.vest.service.monarch;

import java.util.ArrayList;
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
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hillrom.vest.batch.processing.monarch.PatientMonarchDeviceDataReader;
import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.TherapySessionMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientNoEventsRepository;
import com.hillrom.vest.repository.monarch.TherapySessionRepositoryMonarch;
import com.hillrom.vest.service.AdherenceCalculationService;
import com.hillrom.vest.service.NoteService;
import com.hillrom.vest.service.PatientComplianceService;
import com.hillrom.vest.service.PatientNoEventService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.TherapyDataVO;
import com.hillrom.vest.web.rest.dto.TreatmentStatisticsVO;

import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.calculateWeightedAvg;

@Service
@Transactional
public class TherapySessionServiceMonarch {

	@Inject
	private TherapySessionRepositoryMonarch therapySessionRepositoryMonarch;

	@Inject
	private AdherenceCalculationService adherenceCalculationService;
	
	@Inject
	private PatientComplianceService complianceService;
	
	@Inject
	private NoteService noteService;
	
	@Inject
	private PatientNoEventService patientNoEventService;

	@Inject
	private PatientNoEventsRepository patientNoEventRepository;

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
			ProtocolConstants protocol = adherenceCalculationService.getProtocolByPatientUserId(patientUser.getId());
			PatientNoEvent patientNoEvent = patientNoEventRepository.findByPatientUserId(patientUser.getId());
			//SortedMap<LocalDate,List<TherapySessionMonarch>> existingTherapySessionMap = getAllTherapySessionsMapByPatientUserId(patientUser.getId());
			SortedMap<LocalDate,PatientCompliance> existingComplianceMap = complianceService.getPatientComplainceMapByPatientUserId(patientUser.getId());
			/*adherenceCalculationService.processAdherenceScore(patientNoEvent, existingTherapySessionMap, 
					receivedTherapySessionMap, existingComplianceMap,protocol);*/
			saveOrUpdateTherapySessions(receivedTherapySessionMapMonarch);
		}
		return therapySessionsMonarch;
	}
	
	private Map<LocalDate, List<TherapySessionMonarch>> eleminateDuplicateTherapySessions(
			SortedMap<LocalDate, List<TherapySessionMonarch>> receivedTherapySessionsMap) {
		List<List<TherapySessionMonarch>> therapySessionsList = new LinkedList<>(receivedTherapySessionsMap.values());
		Long patientUserId = therapySessionsList.get(0).get(0).getPatientUser().getId();
		LocalDate from = receivedTherapySessionsMap.firstKey();
		LocalDate to = receivedTherapySessionsMap.lastKey();
		List<TherapySessionMonarch> existingTherapySessions = therapySessionRepositoryMonarch.findByPatientUserIdAndDateRange(patientUserId, from, to);
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
								existingSession.getPressure().equals(receivedSession.getPressure()) &&
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
	
	private synchronized void saveOrUpdateTherapySessions(
			SortedMap<LocalDate, List<TherapySessionMonarch>> receivedTherapySessionsMapMonarch) {
		//Map<LocalDate, List<TherapySessionMonarch>> allTherapySessionMapMonarch = eleminateDuplicateTherapySessions(receivedTherapySessionsMapMonarch);
		Map<LocalDate, List<TherapySessionMonarch>> allTherapySessionMapMonarch = receivedTherapySessionsMapMonarch;
		
		List<TherapySessionMonarch> newTherapySessionsMonarch = new LinkedList<>();
		for(LocalDate date : allTherapySessionMapMonarch.keySet()){
			List<TherapySessionMonarch> sessionsTobeSavedMonarch = allTherapySessionMapMonarch.get(date);
			newTherapySessionsMonarch.addAll(sessionsTobeSavedMonarch);
		}
		therapySessionRepositoryMonarch.save(newTherapySessionsMonarch);
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
	
	/*public List<TherapyDataVO> findByPatientUserIdAndDateRange(Long patientUserId,LocalDate from,LocalDate to){
		List<TherapySessionMonarch> sessions = therapySessionRepositoryMonarch
				.findByPatientUserIdAndDateRange(patientUserId,from,to);
		Map<LocalDate, Note> dateNotesMap = noteService.findByPatientUserIdAndCreatedOnBetweenGroupByCreatedOn(patientUserId, from, to, false);
		Map<LocalDate,List<TherapySessionMonarch>> tpsGroupByDate = groupTherapySessionsByDate(sessions);
		return formatResponse(tpsGroupByDate, dateNotesMap, patientUserId, from, to);
	}*/
	
	/*public SortedMap<LocalDate,List<TherapySessionMonarch>> groupTherapySessionsByDate(List<TherapySessionMonarch> therapySessions){
		return new TreeMap<>(therapySessions.stream().collect(Collectors.groupingBy(TherapySession :: getDate)));
	}
	
	public List<TherapySessionMonarch> findByPatientUserIdAndDate(Long id,LocalDate date){
		return  therapySessionRepositoryMonarch.findByPatientUserIdAndDate(id,date);
	}	
	
	public Map<Long,List<TherapySessionMonarch>> getTherapySessionsGroupByPatientUserId(List<Long> patientUserIds){
		List<TherapySessionMonarch> therapySessions = therapySessionRepositoryMonarch.findTop1ByPatientUserIdInOrderByEndTimeDesc(patientUserIds);
		return therapySessions.stream().collect(Collectors.groupingBy(TherapySession::getTherapySessionByPatientUserId));
	}*/

	/*public Collection<TreatmentStatisticsVO> getTreatmentStatisticsByPatientUserIdsAndDuration(
			List<Long> patientUserIds,
			LocalDate from,LocalDate to) {
		List<TherapySessionMonarch> therapySessions = therapySessionRepositoryMonarch.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<LocalDate,List<TherapySessionMonarch>> tpsGroupedByDate = therapySessions.stream().collect(Collectors.groupingBy(TherapySessionMonarch :: getDate));
			return getAvgTreatmentStatisticsForTherapiesGroupedByDate(patientUserIds, tpsGroupedByDate);
	}*/

	/*public LinkedList<TreatmentStatisticsVO> getAvgTreatmentStatisticsForTherapiesGroupedByDate(List<Long> patientUserIds,
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
	}*/

	/**
	 * prepare dummy therapy data for the week
	 * @param from
	 * @param to
	 * @param dummyData
	 */
	private List<TherapyDataVO> prepareTherapySessionsAddMissedTherapyData(Long patientUserId,
			LocalDate from, LocalDate to,
			Map<LocalDate, List<TherapyDataVO>> therapySessionMap,
			Map<LocalDate,Note> noteMap,
			TherapySessionMonarch latestTherapySession) {
		int minutes = 60*60;
		// Get the latest HMR for the user before the requested duration
		double hmrInHours = Objects.nonNull(latestTherapySession)?latestTherapySession.getHmr()/minutes:0d;
		
		// This is to discard the records if the user requested data beyond his/her first transmission date.
		PatientNoEvent patientNoEvent = patientNoEventService.findByPatientUserId(patientUserId);
		if(Objects.nonNull(patientNoEvent) && Objects.nonNull(patientNoEvent.getFirstTransmissionDate()))
			from = from.isAfter(patientNoEvent.getFirstTransmissionDate()) ? from : patientNoEvent.getFirstTransmissionDate();
		// Prepare the list of dates to which data has to be shown
		List<LocalDate> dates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		List<TherapyDataVO> processedTherapies = new LinkedList<>();
		for(LocalDate date : dates){
			// insert therapy done by user
			if(Objects.nonNull(therapySessionMap.get(date))){
				List<TherapyDataVO> therapySessions = therapySessionMap.get(date);
				therapySessions.forEach(therapy -> {
					therapy.setNote(noteMap.get(date));
					processedTherapies.add(therapy);
				});
				// updating HMR from previous day to form step graph
				hmrInHours = therapySessions.get(therapySessions.size()-1).getHmr();
			}else if(date.isBefore(LocalDate.now())){ // Don't consider current date as missed therapy
				// add missed therapy if user misses the therapy
				TherapyDataVO missedTherapy = createTherapyDataWithTimeStamp(date);
				missedTherapy.setNote(noteMap.get(date));
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
	private TherapyDataVO createTherapyDataWithTimeStamp(LocalDate from) {
			TherapyDataVO therapy = new TherapyDataVO();
			therapy.setMissedTherapy(true);
			therapy.setTimestamp(from.toDateTimeAtCurrentTime());
			return therapy; 
	}
	
	private List<TherapyDataVO> formatResponse(Map<LocalDate,List<TherapySessionMonarch>> sessionMap,
			Map<LocalDate, Note> noteMap,Long patientUserId,LocalDate from,LocalDate to){
		TherapySessionMonarch latestTherapySession = therapySessionRepositoryMonarch.findTop1ByPatientUserIdAndDateBeforeOrderByEndTimeDesc(patientUserId,from);
		Map<LocalDate, List<TherapyDataVO>> therapyDataMap = assignNotesToTherapySession(
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

	private Map<LocalDate, List<TherapyDataVO>> assignNotesToTherapySession(
			Map<LocalDate, List<TherapySessionMonarch>> sessionMap,
			Map<LocalDate, Note> noteMap) {
		Map<LocalDate,List<TherapyDataVO>> therapyDataMap = new TreeMap<>();
		TherapyDataVO therapyDataVO = null;
		int minutes = 60*60;
		for(LocalDate date : sessionMap.keySet()){
			List<TherapySessionMonarch> sessionsPerDate = sessionMap.get(date);
			List<TherapyDataVO> therapyDataVOs = therapyDataMap.get(date);
			if(Objects.isNull(therapyDataVOs))
				therapyDataVOs = new LinkedList<>();
			for(TherapySessionMonarch session: sessionsPerDate){
				int programmedCoughPauses = session.getProgrammedCaughPauses();
				int normalCoughPauses = session.getNormalCaughPauses();
				therapyDataVO = new TherapyDataVO(session.getStartTime(), sessionsPerDate.size(),session.getSessionNo(), 
						session.getFrequency(),	session.getPressure(), programmedCoughPauses, normalCoughPauses,
						programmedCoughPauses+normalCoughPauses, noteMap.get(date), session.getStartTime(),
						session.getEndTime(), session.getCaughPauseDuration(),
						session.getDurationInMinutes(), session.getHmr().doubleValue()/minutes,false);
				therapyDataVOs.add(therapyDataVO);
			}
			therapyDataMap.put(date, therapyDataVOs);
		}
		return therapyDataMap;
	} 
	
	/*public SortedMap<LocalDate,List<TherapySessionMonarch>> getAllTherapySessionsMapByPatientUserId(Long patientUserId){
		List<TherapySessionMonarch> therapySessions =  therapySessionRepositoryMonarch.findByPatientUserId(patientUserId);
		return groupTherapySessionsByDate(therapySessions);
	}*/
	
	/*public List<TherapyDataVO> getComplianceGraphData(Long patientUserId,LocalDate from,LocalDate to){
		List<TherapyDataVO> therapyData = findByPatientUserIdAndDateRange(patientUserId, from, to);
		Map<LocalDate,List<TherapyDataVO>> therapyDataMap = therapyData.stream().collect(Collectors.groupingBy(TherapyDataVO::getDate));
		SortedMap<LocalDate,List<TherapyDataVO>> therapyDataGroupByDate = new TreeMap<>(therapyDataMap);
		List<TherapyDataVO> responseList = new LinkedList<>();
		for(LocalDate date : therapyDataGroupByDate.keySet()){
			List<TherapyDataVO> therapies = therapyDataGroupByDate.get(date);
			int totalDuration = therapies.stream().collect(Collectors.summingInt(TherapyDataVO::getDuration));
			int programmedCoughPauses=0,normalCoughPauses=0,coughPauseDuration=0;
			float weightedAvgFrequency = 0.0f,weightedAvgPressure = 0.0f;
			Note noteForTheDay = null;
			int size = therapies.size();
			DateTime start = therapies.get(0).getStart();
			DateTime end = size > 0 ? therapies.get(size-1).getEnd(): null;
			double hmr = size > 0 ? therapies.get(size-1).getHmr(): 0;
			boolean isMissedTherapy = therapies.get(0).isMissedTherapy();
			for(TherapyDataVO therapy : therapies){
				programmedCoughPauses += therapy.getProgrammedCoughPauses();
				normalCoughPauses += therapy.getNormalCoughPauses();
				weightedAvgFrequency += calculateWeightedAvg(totalDuration, therapy.getDuration(), therapy.getFrequency());
				weightedAvgPressure += calculateWeightedAvg(totalDuration, therapy.getDuration(), therapy.getPressure());
				noteForTheDay = therapy.getNote();
			}
			int minutes = 60*60;
			TherapyDataVO dataVO = new TherapyDataVO(therapies.get(0).getTimestamp(), Math.round(weightedAvgFrequency), Math.round(weightedAvgPressure),
					programmedCoughPauses, normalCoughPauses, programmedCoughPauses+normalCoughPauses, noteForTheDay, start, end, coughPauseDuration,
					totalDuration, Math.round(hmr/minutes), isMissedTherapy);
			responseList.add(dataVO);
		}
		return responseList;
	}*/
}
