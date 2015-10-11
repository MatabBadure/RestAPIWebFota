package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.TherapyDataVO;
import com.hillrom.vest.web.rest.dto.TreatmentStatisticsVO;

@Service
@Transactional
public class TherapySessionService {

	
	@Inject
	private TherapySessionRepository therapySessionRepository;
	
	@Inject
	private AdherenceCalculationService adherenceCalculationService;
	
	@Inject
	private PatientComplianceService complianceService;
	
	@Inject
	private NoteService noteService;
	
	@Inject
	private PatientNoEventService patientNoEventService;
	
	public List<TherapySession> saveOrUpdate(List<TherapySession> therapySessions) throws HillromException{
		User patientUser = therapySessions.get(0).getPatientUser();
		removeExistingTherapySessions(therapySessions, patientUser);
		Map<LocalDate, List<TherapySession>> groupedTherapySessions = therapySessions
				.stream()
				.collect(
						Collectors
								.groupingBy(TherapySession::getDate));
		
		SortedSet<LocalDate> daysInSortOrder = new TreeSet<>(groupedTherapySessions.keySet());
		for(LocalDate date : daysInSortOrder){
			List<TherapySession> therapySessionsPerDay = groupedTherapySessions.get(date);
			ProtocolConstants protocol = adherenceCalculationService.getProtocolByPatientUserId(patientUser.getId());
			PatientCompliance compliance =  adherenceCalculationService.calculateCompliancePerDay(therapySessionsPerDay,protocol);
			complianceService.createOrUpdate(compliance);
			therapySessionRepository.save(therapySessionsPerDay);
		}
		return therapySessions;
	}

	public void removeExistingTherapySessions(
			List<TherapySession> therapySessions, User patientUser) {
		TherapySession latestTherapySession =  therapySessionRepository.findTop1ByPatientUserIdOrderByEndTimeDesc(patientUser.getId());
		// Removing existing therapySessions from DB
		if(Objects.nonNull(latestTherapySession)){
			Iterator<TherapySession> tpsIterator = therapySessions.iterator();
			while(tpsIterator.hasNext()){
				TherapySession tps = tpsIterator.next();
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
	}
	
	public List<TherapyDataVO> findByPatientUserIdAndDateRange(Long patientUserId,LocalDate from,LocalDate to){
		List<TherapySession> sessions = therapySessionRepository
				.findByPatientUserIdAndDateRange(patientUserId,from,to);
		Map<LocalDate, Note> dateNotesMap = noteService.findByPatientUserIdAndCreatedOnBetweenGroupByCreatedOn(patientUserId, from, to, false);
		Map<LocalDate,List<TherapySession>> tpsGroupByDate = groupTherapySessionsByDate(sessions);
		return formatResponse(tpsGroupByDate, dateNotesMap, patientUserId, from, to);
	}
	
	public Map<LocalDate,List<TherapySession>> groupTherapySessionsByDate(List<TherapySession> therapySessions){
		return therapySessions.stream().collect(Collectors.groupingBy(TherapySession :: getDate));
	}
	
	public List<TherapySession> findByPatientUserIdAndDate(Long id,LocalDate date){
		return  therapySessionRepository.findByPatientUserIdAndDate(id,date);
	}	
	
	public int getMissedTherapyCountByPatientUserId(Long id){
		TherapySession latestTherapySession = therapySessionRepository.findTop1ByPatientUserIdOrderByEndTimeDesc(id);
		if(Objects.nonNull(latestTherapySession)){
			DateTime today = DateTime.now();
			DateTime latestSessionDate = DateUtil.convertLocalDateToDateTime(latestTherapySession.getDate());
			if(Objects.isNull(latestSessionDate))
				return 0;
			return Days.daysBetween(latestSessionDate, today.toInstant()).getDays();
		}
		return 0;
	}
	
	public Map<Long,List<TherapySession>> getTherapySessionsGroupByPatientUserId(List<Long> patientUserIds){
		List<TherapySession> therapySessions = therapySessionRepository.findTop1ByPatientUserIdInOrderByEndTimeDesc(patientUserIds);
		return therapySessions.stream().collect(Collectors.groupingBy(TherapySession::getTherapySessionByPatientUserId));
	}

	public Collection<TreatmentStatisticsVO> getTreatmentStatisticsByPatientUserIdsAndDuration(
			List<Long> patientUserIds,
			LocalDate from,LocalDate to) {
		List<TherapySession> therapySessions = therapySessionRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<LocalDate,List<TherapySession>> tpsGroupedByDate = therapySessions.stream().collect(Collectors.groupingBy(TherapySession :: getDate));
			return getAvgTreatmentStatisticsForTherapiesGroupedByDate(patientUserIds, tpsGroupedByDate);
	}

	public Collection<TreatmentStatisticsVO> getAvgTreatmentStatisticsForTherapiesGroupedByDate(List<Long> patientUserIds,
			Map<LocalDate, List<TherapySession>> tpsGroupedByDate) {
		Map<LocalDate, TreatmentStatisticsVO> statisticsMap = new TreeMap<>();
		TreatmentStatisticsVO statisticsVO;
		for(LocalDate date : tpsGroupedByDate.keySet()){
			List<TherapySession> tpsOnDate = tpsGroupedByDate.get(date);
			statisticsVO = calculateAvgTreatmentStatistics(patientUserIds,
						tpsOnDate);
			statisticsMap.put(date, statisticsVO);
		}
		return statisticsMap.values();
	}
	
	public TreatmentStatisticsVO calculateAvgTreatmentStatistics(
			List<Long> patientUserIds, List<TherapySession> tpsInDuration) {
		TreatmentStatisticsVO statisticsVO;
		int avgTreatment = tpsInDuration.size()/patientUserIds.size();
		int avgDuration = tpsInDuration
				.stream()
				.collect(
						Collectors
								.summingLong(TherapySession::getDurationLongValue))
				.intValue()
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
	private List<TherapyDataVO> prepareTherapySessionsAddMissedTherapyData(Long patientUserId,
			LocalDate from, LocalDate to,
			Map<LocalDate, List<TherapyDataVO>> therapySessionMap,
			Map<LocalDate,Note> noteMap,
			TherapySession latestTherapySession) {
		int seconds = 60;
		// Get the latest HMR for the user before the requested duration
		double hmrInMinutes = Objects.nonNull(latestTherapySession)?latestTherapySession.getHmr()/seconds:0d;
		
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
				hmrInMinutes = therapySessions.get(therapySessions.size()-1).getHmr();
			}else{
				// add missed therapy if user misses the therapy
				TherapyDataVO missedTherapy = createTherapyDataWithTimeStamp(date);
				missedTherapy.setNote(noteMap.get(date));
				missedTherapy.setHmr(hmrInMinutes);
				processedTherapies.add(missedTherapy);
			}
		}
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
	
	private List<TherapyDataVO> formatResponse(Map<LocalDate,List<TherapySession>> sessionMap,
			Map<LocalDate, Note> noteMap,Long patientUserId,LocalDate from,LocalDate to){
		TherapySession latestTherapySession = therapySessionRepository.findTop1ByPatientUserIdAndDateBeforeOrderByEndTimeDesc(patientUserId,from);
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
			Map<LocalDate, List<TherapySession>> sessionMap,
			Map<LocalDate, Note> noteMap) {
		Map<LocalDate,List<TherapyDataVO>> therapyDataMap = new TreeMap<>();
		TherapyDataVO therapyDataVO = null;
		for(LocalDate date : sessionMap.keySet()){
			List<TherapySession> sessionsPerDate = sessionMap.get(date);
			List<TherapyDataVO> therapyDataVOs = therapyDataMap.get(date);
			if(Objects.isNull(therapyDataVOs))
				therapyDataVOs = new LinkedList<>();
			for(TherapySession session: sessionsPerDate){
				int programmedCoughPauses = session.getProgrammedCaughPauses();
				int normalCoughPauses = session.getNormalCaughPauses();
				therapyDataVO = new TherapyDataVO(session.getStartTime(), sessionsPerDate.size(),session.getSessionNo(), 
						session.getFrequency(),	session.getPressure(), programmedCoughPauses, normalCoughPauses,
						programmedCoughPauses+normalCoughPauses, noteMap.get(date), session.getStartTime(),
						session.getEndTime(), session.getCaughPauseDuration(),
						session.getDurationInMinutes().intValue(), session.getHmr().doubleValue()/60,false);
				therapyDataVOs.add(therapyDataVO);
			}
			therapyDataMap.put(date, therapyDataVOs);
		}
		return therapyDataMap;
	} 
	
}
