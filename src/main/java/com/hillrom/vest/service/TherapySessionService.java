package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.GROUP_BY_YEARLY;
import static com.hillrom.vest.config.Constants.GROUP_BY_MONTHLY;
import static com.hillrom.vest.config.Constants.GROUP_BY_WEEKLY;
import java.util.Collection;
import java.util.HashMap;
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

import com.hillrom.vest.domain.PatientCompliance;
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
	
	public List<TherapyDataVO> findByPatientUserIdAndDateRange(Long patientUserId,LocalDate from,LocalDate to,String groupBy){
		List<TherapySession> sessions = therapySessionRepository
				.findByPatientUserIdAndDateRange(patientUserId,from,to);
		List<TherapyDataVO> results = new LinkedList<>();
		TherapyDataVO therapyDataVO = null;
		Map<LocalDate,List<TherapySession>> tpsGroupByDate = groupTherapySessionsByDate(sessions);
		for(LocalDate date : tpsGroupByDate.keySet()){
			List<TherapySession> sessionsPerDate = tpsGroupByDate.get(date);
			for(TherapySession session: sessionsPerDate){
				int programmedCoughPauses = session.getProgrammedCaughPauses();
				int normalCoughPauses = session.getNormalCaughPauses();
				therapyDataVO = new TherapyDataVO(session.getStartTime(), sessionsPerDate.size(),session.getSessionNo(), 
						session.getFrequency(),	session.getPressure(), programmedCoughPauses, normalCoughPauses,
						programmedCoughPauses+normalCoughPauses, null, session.getStartTime(),
						session.getEndTime(), session.getCaughPauseDuration(),
						session.getDurationInMinutes().intValue(), session.getHmr().doubleValue()/60);
				results.add(therapyDataVO);
			}
		}
		
			return results;
	}
	
	public Map<LocalDate,List<TherapySession>> groupTherapySessionsByDate(List<TherapySession> therapySessions){
		return new TreeMap<>(therapySessions.stream().collect(Collectors.groupingBy(TherapySession :: getDate)));
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
			LocalDate from,LocalDate to, String groupBy) {
		
		List<TherapySession> therapySessions = therapySessionRepository.findByDateBetweenAndPatientUserIdIn(from, to, patientUserIds);
		Map<LocalDate,List<TherapySession>> tpsGroupedByDate = therapySessions.stream().collect(Collectors.groupingBy(TherapySession :: getDate));

		List<LocalDate> requestedDates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		
		if(GROUP_BY_WEEKLY.equalsIgnoreCase(groupBy))
			return calculateAvgTreatmentStatisticsForWeek(patientUserIds, tpsGroupedByDate, requestedDates);
		else
			return calculateAvgTreatmentStatisticsForMonthOrYear(patientUserIds, therapySessions, requestedDates, groupBy);
		
	}

	public Collection<TreatmentStatisticsVO> calculateAvgTreatmentStatisticsForWeek(List<Long> patientUserIds,
			Map<LocalDate, List<TherapySession>> tpsGroupedByDate,
			List<LocalDate> requestedDates) {
		Map<LocalDate, TreatmentStatisticsVO> statisticsMap = new TreeMap<>();
		TreatmentStatisticsVO statisticsVO;
		for(LocalDate requestedDate : requestedDates){
			List<TherapySession> tpsOnDate = tpsGroupedByDate.get(requestedDate);
			if(Objects.nonNull(tpsOnDate)){
				statisticsVO = calculateAvgTreatmentStatistics(patientUserIds,
						tpsOnDate);
			}else{
				statisticsVO = new TreatmentStatisticsVO(requestedDate, requestedDate);
			}
			statisticsMap.put(requestedDate, statisticsVO);
		}
		return statisticsMap.values();
	}
	
	public Collection<TreatmentStatisticsVO> calculateAvgTreatmentStatisticsForMonthOrYear(List<Long> patientUserIds,
			List<TherapySession> therapySessions,
			List<LocalDate> requestedDates,String groupBy) {
		Map<LocalDate, TreatmentStatisticsVO> statisticsMap = new TreeMap<>();
		Map<Integer,List<LocalDate>> datesGroupedByDuration = new HashMap<>();
		Map<Integer,List<TherapySession>> tpsGroupedOnDuration = new HashMap<>();
		if(GROUP_BY_MONTHLY.equalsIgnoreCase(groupBy)){
			datesGroupedByDuration = requestedDates.stream().collect(Collectors.groupingBy(LocalDate :: getWeekOfWeekyear));
			tpsGroupedOnDuration = therapySessions.stream().collect(Collectors.groupingBy(TherapySession :: getWeekOfYear));
		}else if(GROUP_BY_YEARLY.equalsIgnoreCase(groupBy)){
			datesGroupedByDuration = requestedDates.stream().collect(Collectors.groupingBy(LocalDate :: getMonthOfYear));
			tpsGroupedOnDuration = therapySessions.stream().collect(Collectors.groupingBy(TherapySession :: getMonthOfTheYear));
		}
		TreatmentStatisticsVO statisticsVO;
		for(Integer day: datesGroupedByDuration.keySet()){
			List<TherapySession> tpsInDuration = tpsGroupedOnDuration.get(day);
			List<LocalDate> datesInDuration = datesGroupedByDuration.get(day);
			LocalDate startDate = datesInDuration.get(0);
			LocalDate endDate = datesInDuration.get(datesInDuration.size()-1);
			if(Objects.nonNull(tpsInDuration)){
				statisticsVO = calculateAvgTreatmentStatistics(patientUserIds,
						tpsInDuration);
			}else{
				statisticsVO = new TreatmentStatisticsVO(startDate, endDate);
			}
			statisticsMap.put(endDate, statisticsVO);
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
	
}
