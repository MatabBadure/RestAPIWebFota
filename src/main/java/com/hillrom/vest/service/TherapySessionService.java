package com.hillrom.vest.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.TherapyDataVO;

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
	private PatientComplianceRepository complianceRepository;
	
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
			complianceRepository.save(compliance);
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
				int tpsDayOfYear = tps.getDate().getDayOfYear();
				int latestTpsDayOfYear = latestTherapySession.getDate().getDayOfYear();
				if(tpsDayOfYear < latestTpsDayOfYear){
					tpsIterator.remove();
					//Remove previous therapySessions of the same day.
				} else {
					DateTime tpsStartTime = tps.getStartTime();
					DateTime latestTpsEndTimeFromDB = latestTherapySession.getEndTime();
					if(tpsDayOfYear == latestTpsDayOfYear && tpsStartTime.isBefore(latestTpsEndTimeFromDB)){
						tpsIterator.remove();
					}
				}
			}
		}
	}
	
	public List<TherapyDataVO> findByPatientUserIdAndDateRange(Long patientUserId,Long fromTimestamp,Long toTimestamp,String groupBy){
		LocalDate from = LocalDate.fromDateFields(new Date(fromTimestamp));
		LocalDate to = LocalDate.fromDateFields(new Date(toTimestamp));
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
		return formatResponse(calculatedData,from,to,groupBy);
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
		LocalDate startDate = from; 
		while(startDate.isBefore(to)){
			dummyData.put(startDate.getDayOfWeek(), createTherapyDataWithTimeStamp(startDate));
			startDate = startDate.plusDays(1);
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

	public List<TherapySession> findByPatientUserIdAndDate(Long id,Long timestamp){
		return  therapySessionRepository.findByPatientUserIdAndDate(id,LocalDate.fromDateFields(new Date(timestamp)));
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
			LocalDate today = LocalDate.now();
			int days = 0;
			LocalDate latestSessionDate = latestTherapySession.getDate();
			if(Objects.isNull(latestSessionDate))
				return 0;
			while(today.isAfter(latestSessionDate)){
				latestSessionDate = latestSessionDate.plusDays(1);
				++days;
			}
			return days;
		}
		return 0;
	}
}
