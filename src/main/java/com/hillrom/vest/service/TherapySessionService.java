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
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.TherapySessionRepository;
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
		List<TherapySession> sessions = therapySessionRepository
				.findByPatientUserIdAndDateRange(patientUserId,
						LocalDate.fromDateFields(new Date(fromTimestamp)),
						LocalDate.fromDateFields(new Date(toTimestamp)));
		Map<Integer,List<TherapySession>> groupedSessions = new HashMap<>();
		if(GROUP_BY_WEEKLY.equalsIgnoreCase(groupBy)){
			groupedSessions = sessions.stream().collect(Collectors.groupingBy(TherapySession :: getDayOfTheWeek));
		}else if(GROUP_BY_MONTHLY.equals(groupBy)){
			groupedSessions = sessions.stream().collect(Collectors.groupingBy(TherapySession :: getWeekOfYear));
		}else if(GROUP_BY_YEARLY.equals(groupBy)){
			groupedSessions = sessions.stream().collect(Collectors.groupingBy(TherapySession :: getMonthOfTheYear));
		}
		return calculateWeightedAvgs(groupedSessions);
	}

	public List<TherapySession> findByPatientUserIdAndDate(Long id,Long timestamp){
		return  therapySessionRepository.findByPatientUserIdAndDate(id,LocalDate.fromDateFields(new Date(timestamp)));
	}
	
	private List<TherapyDataVO> calculateWeightedAvgs(
			Map<Integer, List<TherapySession>> groupedSessions) {
		List<TherapyDataVO> processedData = new LinkedList<>();

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
			processedData.add(vo);
		}
		return processedData;
	}
	
	public int getMissedTherapyCountByPatientUserId(Long id){
		TherapySession latestTherapySession = therapySessionRepository.findTop1ByPatientUserIdOrderByEndTimeDesc(id);
		if(Objects.nonNull(latestTherapySession)){
			DateTime today = DateTime.now();
			DateTime latestSessionDate = new DateTime(latestTherapySession.getDate());
			if(Objects.isNull(latestSessionDate))
				return 0;
			return Days.daysBetween(latestSessionDate, today).getDays();
		}
		return 0;
	}
}
