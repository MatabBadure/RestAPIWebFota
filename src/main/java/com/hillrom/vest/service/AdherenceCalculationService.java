package com.hillrom.vest.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.exceptionhandler.HillromException;

public class AdherenceCalculationService {

	@Inject
	private PatientProtocolService protocolService;
	
	public List<PatientCompliance> getComplainceScoreByTherapySessions(List<TherapySession> therapySessions) throws HillromException{
		Long patientUserId = therapySessions.get(0).getPatientUser().getId();
		PatientProtocolData protocolData =  protocolService.getProtocolsAssociatedWithPatient(patientUserId);
		
		Map<Integer,List<TherapySession>> groupByTherapyDate = therapySessions.stream()
		        .collect(Collectors.groupingBy(TherapySession::getTherapyDayOfTheYear));
		
		Map<Integer,PatientCompliance> patientCompliance = new HashMap<>();
		
		Iterator<Integer> days = groupByTherapyDate.keySet().iterator();
		while(days.hasNext()){
			Integer day = days.next();
			List<TherapySession> therapySessionsPerDay = groupByTherapyDate.get(day);
			PatientCompliance calculatedPatientCompliance = calculateCompliancePerDay(therapySessionsPerDay,day,protocolData);
			patientCompliance.put(day, calculatedPatientCompliance);
		}
		return null;
	}

	private PatientCompliance calculateCompliancePerDay(
			List<TherapySession> therapySessionsPerDay, Integer day,PatientProtocolData protocolData) {
		long totalDuration = therapySessionsPerDay.stream().collect(Collectors.summingLong(TherapySession::getDurationInMinutes));
		double weightedAvgFrequency = 0.0;
		double weightedAvgPressure = 0.0;
		for(TherapySession therapySession : therapySessionsPerDay){
			weightedAvgFrequency += (therapySession.getDurationInMinutes()/totalDuration)*therapySession.getFrequency();
			weightedAvgPressure += (therapySession.getDurationInMinutes()/totalDuration)*therapySession.getPressure();
		}
		if(protocolData.getMinimumMinutesOfUsePerDay() > totalDuration){
			
		}  
		return null;
	}
	
	
	
}
