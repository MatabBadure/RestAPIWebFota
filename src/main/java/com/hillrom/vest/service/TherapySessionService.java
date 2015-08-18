package com.hillrom.vest.service;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
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
	
	public List<TherapySession> saveOrUpdate(List<TherapySession> therapySessions){
		User patientUser = therapySessions.get(0).getPatientUser();
		List<TherapySession> existingTherapySessions =  therapySessionRepository.findByPatientUserId(patientUser.getId());
		// Removing existing therapySessions from DB
		if(existingTherapySessions.size() > 0){
			TherapySession latestThreapySession = existingTherapySessions.get(0);
			Iterator<TherapySession> tpsIterator = therapySessions.iterator();
			while(tpsIterator.hasNext()){
				TherapySession tps = tpsIterator.next();
				// Remove previous therapy Sessions
				int tpsDayOfYear = tps.getDate().getDayOfYear();
				int latestTpsDayOfYear = latestThreapySession.getDate().getDayOfYear();
				if(tpsDayOfYear < latestTpsDayOfYear){
					tpsIterator.remove();
					//Remove previous therapySessions of the same day.
				} else {
					Integer tpsSessionNo = tps.getSessionNo();
					Integer latestTpsSessionNo = latestThreapySession.getSessionNo();
					if(tpsDayOfYear == latestTpsDayOfYear && tpsSessionNo <= latestTpsSessionNo){
						tpsIterator.remove();
					}
				}
			}
		}
		return therapySessionRepository.save(therapySessions);
	}
	
	public List<TherapyDataVO> findByPatientUserIdAndDateRange(Long id,Long fromTimestamp,Long toTimestamp,String groupBy){
		List<TherapySession> sessions = therapySessionRepository
				.findByPatientUserIdAndDateRange(id,
						LocalDate.fromDateFields(new Date(fromTimestamp)),
						LocalDate.fromDateFields(new Date(toTimestamp)));
		if(GROUP_BY_WEEKLY.equalsIgnoreCase(groupBy)){
			Map<Integer,List<TherapySession>> groupedSessions = sessions.stream().collect(Collectors.groupingBy(TherapySession :: getWeekOfTheMonth));
			return (List<TherapyDataVO>) calculateWeightedAvgs(groupedSessions); 
		}else if(GROUP_BY_MONTHLY.equals(groupBy)){
			Map<Integer,List<TherapySession>> groupedSessions = sessions.stream().collect(Collectors.groupingBy(TherapySession :: getMonthOfTheYear));
			return (List<TherapyDataVO>) calculateWeightedAvgs(groupedSessions);
		}else if(GROUP_BY_YEARLY.equals(groupBy)){
			Map<Integer,List<TherapySession>> groupedSessions = sessions.stream().collect(Collectors.groupingBy(TherapySession :: getYear));
			return (List<TherapyDataVO>) calculateWeightedAvgs(groupedSessions);
		}
		return null;
	}

	public List<TherapySession> findByPatientUserIdAndDate(Long id,Long timestamp){
		return  therapySessionRepository.findByPatientUserIdAndDate(id,LocalDate.fromDateFields(new Date(timestamp)));
	}
	
	private List<TherapyDataVO> calculateWeightedAvgs(
			Map<Integer, List<TherapySession>> groupedSessions) {
		List<TherapyDataVO> processedData = new LinkedList<>();
		
		for(Integer key : groupedSessions.keySet()){
			List<TherapySession> sessions = groupedSessions.get(key);
			DateTime start = sessions.get(0).getStartTime();
			DateTime end = sessions.get(sessions.size()-1).getEndTime();
			TherapyDataVO vo = new TherapyDataVO();
			long totalDuration = sessions.stream().collect(Collectors.summingLong(TherapySession::getDurationInMinutes));
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
			processedData.add(vo);
		}
		return processedData;
	}
}
