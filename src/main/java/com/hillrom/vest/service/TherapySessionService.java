package com.hillrom.vest.service;

import java.util.Collection;
import java.util.Collections;
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
import com.hillrom.vest.web.rest.dto.StatisticsVO;
import com.hillrom.vest.web.rest.dto.TherapyDataVO;
import com.hillrom.vest.web.rest.dto.TreatmentStatisticsVO;

@Service
@Transactional
public class TherapySessionService {

	private static final String GROUP_BY_YEARLY = "yearly";
	private static final String GROUP_BY_MONTHLY = "monthly";
	private static final String GROUP_BY_WEEKLY = "weekly";
	
	@Inject
	private TherapySessionRepository therapySessionRepository;
	
	@Inject
	private AdherenceCalculationService adherenceCalculationService;
	
	@Inject
	private PatientComplianceService complianceService;
	
	public List<TherapySession> saveOrUpdate(List<TherapySession> therapySessions) throws HillromException{
		User patientUser = therapySessions.get(0).getPatientUser();
		removeExistingTherapySessions(therapySessions, patientUser);
		Map<Integer, List<TherapySession>> groupedTherapySessions = therapySessions
				.stream()
				.collect(
						Collectors
								.groupingBy(TherapySession::getTherapyDayOfTheYear));
		
		SortedSet<Integer> daysInSortOrder = new TreeSet<>(groupedTherapySessions.keySet());
		for(Integer day : daysInSortOrder){
			List<TherapySession> therapySessionsPerDay = groupedTherapySessions.get(day);
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
		Map<Integer,List<TherapySession>> groupedSessions = new HashMap<>();
		if(GROUP_BY_WEEKLY.equalsIgnoreCase(groupBy)){
			groupedSessions = sessions.stream().collect(Collectors.groupingBy(TherapySession :: getDayOfTheWeek));
		}else if(GROUP_BY_MONTHLY.equals(groupBy)){
			groupedSessions = sessions.stream().collect(Collectors.groupingBy(TherapySession :: getWeekOfYear));
		}else if(GROUP_BY_YEARLY.equals(groupBy)){
			groupedSessions = sessions.stream().collect(Collectors.groupingBy(TherapySession :: getMonthOfTheYear));
		}
		Map<Integer,TherapyDataVO> calculatedData =  calculateWeightedAvgs(groupedSessions);
		if(calculatedData.isEmpty())
			return new LinkedList<>();
		List<TherapyDataVO> results = formatResponse(calculatedData,from,to,groupBy);
		Collections.sort(results);
		return results;
	}
	
	/**
	 *  Add dummy data for missing therapy days/weeks/months
	 * @param calculatedData
	 * @param from
	 * @param to
	 * @param groupBy
	 * @return
	 */
	private List<TherapyDataVO> formatResponse(
			Map<Integer, TherapyDataVO> calculatedData, LocalDate from,
			LocalDate to, String groupBy) {
		Map<Integer,TherapyDataVO>  dummyData = new HashMap<>();
		if(GROUP_BY_WEEKLY.equalsIgnoreCase(groupBy)){
			prepareDummyTherapyDataByWeek(from, to, dummyData);
		}else if(GROUP_BY_MONTHLY.equalsIgnoreCase(groupBy)){
			prepareDummyTherapyDataByMonth(from, to, dummyData);
		}else{
			prepareDummyTherapyDataByYear(from,to,dummyData);
		}
		
		for(Integer key: calculatedData.keySet()){
			dummyData.put(key, calculatedData.get(key));
		}
		List<TherapyDataVO> result = new LinkedList<>(dummyData.values());
		assignHMRForMissedTherapyFromExistingTherapy(result);
		return result;
	}

	/**
	 * Assign HMR of existing therapy to Missing Therapy to support step graph
	 * @param result
	 */
	private void assignHMRForMissedTherapyFromExistingTherapy(
			List<TherapyDataVO> result) {
		for(int i = 0;i < result.size();i++){
			TherapyDataVO therapyDataVO = result.get(i);
			if(!therapyDataVO.isMissedTherapy()){
				for(int j = i+1;j<result.size();j++){
					TherapyDataVO nextTherapyDataVO = result.get(j);
					if(nextTherapyDataVO.isMissedTherapy() && therapyDataVO.getTimestamp().isBefore(nextTherapyDataVO.getTimestamp())){
						nextTherapyDataVO.setHmr(therapyDataVO.getHmr());
						result.set(j, nextTherapyDataVO);
					}else{
						break;
					}
				}
			}
		}
	}

	/**
	 * prepare dummy therapy data for the year
	 * @param from
	 * @param to
	 * @param dummyData
	 */
	private void prepareDummyTherapyDataByYear(LocalDate from, LocalDate to,
			Map<Integer, TherapyDataVO> dummyData) {
		List<LocalDate> dates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		Map<Integer, List<LocalDate>> groupByWeek = DateUtil.groupListOfLocalDatesByMonthOfYear(dates);
		for(Integer weekNo : groupByWeek.keySet()){
			LocalDate startDateOfWeek = groupByWeek.get(weekNo).get(0);
			dummyData.put(weekNo, createTherapyDataWithTimeStamp(startDateOfWeek));	
		}
	}
	
	/**
	 * prepare dummy therapy data for the month 
	 * @param from
	 * @param to
	 * @param dummyData
	 */
	private void prepareDummyTherapyDataByMonth(LocalDate from, LocalDate to,
			Map<Integer, TherapyDataVO> dummyData) {
		List<LocalDate> dates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		Map<Integer, List<LocalDate>> groupByMonth = DateUtil.groupListOfLocalDatesByWeekOfWeekyear(dates);
		for(Integer monthNo : groupByMonth.keySet()){
			LocalDate startDateOfMonth = groupByMonth.get(monthNo).get(0);
			dummyData.put(monthNo, createTherapyDataWithTimeStamp(startDateOfMonth));	
		}		
	}

	/**
	 * prepare dummy therapy data for the week
	 * @param from
	 * @param to
	 * @param dummyData
	 */
	private void prepareDummyTherapyDataByWeek(LocalDate from, LocalDate to,
			Map<Integer, TherapyDataVO> dummyData) {
		List<LocalDate> dates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		Map<Integer, List<LocalDate>> groupByDayOfWeek = DateUtil.groupListOfLocalDatesByDayOfWeek(dates);
		for(Integer dayOfWeek : groupByDayOfWeek.keySet()){
			dummyData.put(dayOfWeek, createTherapyDataWithTimeStamp(groupByDayOfWeek.get(dayOfWeek).get(0)));
		}
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

	public List<TherapySession> findByPatientUserIdAndDate(Long id,LocalDate date){
		return  therapySessionRepository.findByPatientUserIdAndDate(id,date);
	}
	
	private Map<Integer,TherapyDataVO> calculateWeightedAvgs(
			Map<Integer, List<TherapySession>> groupedSessions) {
		Map<Integer,TherapyDataVO> processedData = new HashMap<>();

		for(Integer key : groupedSessions.keySet()){
			List<TherapySession> sessions = groupedSessions.get(key);
			int size = sessions.size();
			int seconds = 60;
			DateTime start = sessions.get(0).getStartTime();
			DateTime end = sessions.get(size-1).getEndTime();
			Double hmr = sessions.get(size-1).getHmr()/seconds;
			TherapyDataVO vo = new TherapyDataVO();
			Long totalDuration = sessions.stream().collect(Collectors.summingLong(TherapySession::getDurationInMinutes));
			double weightedAvgFrequency = 0.0d,weightedAvgPressure = 0.0d;
			int coughPauses = 0,programmedCoughPauses = 0,normalCoughPauses = 0,coughPauseDuration = 0;
			int treatmentsPerDay = 0;
			for(TherapySession therapySession : sessions){
				weightedAvgFrequency += (double)((therapySession.getDurationInMinutes()*therapySession.getFrequency())/totalDuration);
				weightedAvgPressure += (double)((therapySession.getDurationInMinutes()*therapySession.getPressure())/totalDuration);
				coughPauses += therapySession.getNormalCaughPauses()+therapySession.getProgrammedCaughPauses();
				normalCoughPauses += therapySession.getNormalCaughPauses();
				programmedCoughPauses += therapySession.getProgrammedCaughPauses();
				coughPauseDuration += therapySession.getCaughPauseDuration();
				++treatmentsPerDay;
			}
			vo.setWeightedAvgFrequency(weightedAvgFrequency);
			vo.setWeightedAvgPressure(weightedAvgPressure);
			vo.setCoughPauses(coughPauses);
			vo.setNormalCoughPauses(normalCoughPauses);
			vo.setProgrammedCoughPauses(programmedCoughPauses);
			vo.setCoughPauseDuration(coughPauseDuration);
			vo.setStart(start);
			vo.setEnd(end);
			vo.setTimestamp(start.toDateTime());
			vo.setTreatmentsPerDay(treatmentsPerDay);
			vo.setDuration(totalDuration.intValue());
			vo.setHmr(hmr);
			processedData.put(key, vo);
		}
		return processedData;
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
