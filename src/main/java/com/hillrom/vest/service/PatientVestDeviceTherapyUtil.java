package com.hillrom.vest.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.User;

public class PatientVestDeviceTherapyUtil {

	private static final String EVENT_CODE_NORMAL_INCOMPLETE = "4";
	private static final String EVENT_CODE_PROGRAM_INCOMPLETE = "17";
	private static final String EVENT_CODE_RAMP_INCOMPLETE = "26";
	private static final String EVENT_CODE_RAMP_COMPLETED = "25";
	private static final String EVENT_CODE_RAMP_STARTED = "20";
	private static final String EVENT_CODE_COUGH_PAUSE = "28";
	private static final String PRESSURE = "pressure";
	private static final String SESSION_TYPE_PROGRAM = "Program";
	private static final String SESSION_TYPE_NORMAL = "Normal";
	private static final String CAUGH_PAUSE_DURATION = "caughPauseDuration";
	private static final String PROGRAMMED_COUGH_PAUSES = "programmedCoughPauses";
	private static final String NORMAL_COUGH_PAUSES = "normalCoughPauses";
	private static final String DURATION = "duration";
	private static final String FREQUENCY = "frequency";
	private static final String EVENT_CODE_COMPLETED = "3";
	private static final String EVENT_CODE_PROGRAM_START = "7";
	private static final String EVENT_CODE_NORMAL_START = "1";
	private static final String EVENT_CODE_PROGRAM_COMPLETED = "16";

	private PatientVestDeviceTherapyUtil(){
		
	}
	
	public static List<TherapySession> prepareTherapySessionFromDeviceData(List<PatientVestDeviceData> deviceData){
		List<TherapySession> therapySessions = new LinkedList<>();
		User patientUser = deviceData.get(0).getPatientUser();
		PatientInfo patient = deviceData.get(0).getPatient();
		Map<Long,List<PatientVestDeviceData>> sessionEntries = groupEventsToPrepareTherapySession(deviceData);
		for(Long timestamp : sessionEntries.keySet()){
			List<PatientVestDeviceData> deviceEventRecords = sessionEntries.get(timestamp);
			Map<String,Integer> metricsMap = getTherapyMetricsMap(deviceEventRecords);
			TherapySession therapySession = new TherapySession();
			therapySession.setDate(new DateTime(timestamp));
			therapySession.setFrequency(metricsMap.get(FREQUENCY));
			therapySession.setPressure(metricsMap.get(PRESSURE));
			therapySession.setDurationInSeconds(metricsMap.get(DURATION).longValue());
			therapySession.setNormalCaughPauses(metricsMap.get(NORMAL_COUGH_PAUSES));
			therapySession.setProgrammedCaughPauses(metricsMap.get(PROGRAMMED_COUGH_PAUSES));
			therapySession.setCaughPauseDuration(metricsMap.get(CAUGH_PAUSE_DURATION));
			therapySession.setHmr(deviceEventRecords.get(deviceEventRecords.size()-1).getHmr());
			String sessionType = SESSION_TYPE_NORMAL;
			if(deviceEventRecords.get(0).getEventId().contains(SESSION_TYPE_PROGRAM))
				sessionType = SESSION_TYPE_PROGRAM;
			therapySession.setSessionType(sessionType);
			therapySession.setStartTime(new DateTime(deviceEventRecords.get(0).getTimestamp()));
			therapySession.setEndTime(new DateTime(deviceEventRecords.get(deviceEventRecords.size()-1).getTimestamp()));
			therapySession.setPatientInfo(patient);
			therapySession.setPatientUser(patientUser);
			therapySessions.add(therapySession);
		}
		return groupTherapySessionsByDay(therapySessions);
	}

	public static Map<String,Integer> getTherapyMetricsMap(
			List<PatientVestDeviceData> deviceEventRecords) {
		Map<String,Integer> metricsMap = new HashMap<>();
		int frequency = 0, pressure = 0, duration = 0, normalCoughPauses = 0, programmedCoughPauses = 0, caughPauseDuration = 0;
		for(PatientVestDeviceData deviceEventRecord : deviceEventRecords){
			frequency += deviceEventRecord.getFrequency();
			pressure += deviceEventRecord.getPressure();
			duration += deviceEventRecord.getDuration();
			if(deviceEventRecord.getEventId().startsWith(EVENT_CODE_COUGH_PAUSE)){
				caughPauseDuration += deviceEventRecord.getDuration();
			}
		}
		metricsMap.put(FREQUENCY, frequency);
		metricsMap.put(PRESSURE, pressure);
		metricsMap.put(DURATION, duration);
		metricsMap.put(NORMAL_COUGH_PAUSES, normalCoughPauses);
		metricsMap.put(PROGRAMMED_COUGH_PAUSES, programmedCoughPauses);
		metricsMap.put(CAUGH_PAUSE_DURATION, caughPauseDuration);
		return metricsMap;
	}

	public static Map<Long,List<PatientVestDeviceData>> groupEventsToPrepareTherapySession(
			List<PatientVestDeviceData> deviceData) {
		Map<Long,List<PatientVestDeviceData>> sessionData = new HashMap<>();
		for(int i = 0; i < deviceData.size() ; i++){
			PatientVestDeviceData vestDeviceData = deviceData.get(i);
			List<PatientVestDeviceData> groupEntries = new LinkedList<>();
			if(vestDeviceData.getEventId().startsWith(EVENT_CODE_NORMAL_START) ||
			   vestDeviceData.getEventId().startsWith(EVENT_CODE_PROGRAM_START) || 
			   vestDeviceData.getEventId().startsWith(EVENT_CODE_RAMP_STARTED)
			   ){
				groupEntries.add(vestDeviceData);
				for(int j = i+1; j < deviceData.size() ; j++){
					PatientVestDeviceData nextEventEntry = deviceData.get(j);
					groupEntries.add(nextEventEntry);
					if(nextEventEntry.getEventId().startsWith(EVENT_CODE_COMPLETED) ||
					   nextEventEntry.getEventId().startsWith(EVENT_CODE_PROGRAM_COMPLETED) ||
					   nextEventEntry.getEventId().startsWith(EVENT_CODE_NORMAL_INCOMPLETE) ||
					   nextEventEntry.getEventId().startsWith(EVENT_CODE_PROGRAM_INCOMPLETE) ||
					   nextEventEntry.getEventId().startsWith(EVENT_CODE_RAMP_COMPLETED) ||
					   nextEventEntry.getEventId().startsWith(EVENT_CODE_RAMP_INCOMPLETE)
							){
						sessionData.put(vestDeviceData.getTimestamp(), groupEntries);
						break;
					}
				}
			}
		}
		return sessionData;
	}
	
	public static List<TherapySession> groupTherapySessionsByDay(List<TherapySession> therapySessions){
		List<TherapySession> updatedTherapySessions = new LinkedList<>();
		Map<Integer,List<TherapySession>> groupByTherapyDate = therapySessions.stream()
		        .collect(Collectors.groupingBy(TherapySession::getTherapyDayOfTheYear));
		Iterator<Integer> keySetItr = groupByTherapyDate.keySet().iterator();
		while(keySetItr.hasNext()){
			List<TherapySession> groupedTherapySessions = groupByTherapyDate.get(keySetItr.next());
			for(int i =0 ; i<groupedTherapySessions.size();i++){
				TherapySession therapySession = groupedTherapySessions.get(i);
				therapySession.setSessionNo(i+1);
				updatedTherapySessions.add(therapySession);
				
			}
		}
		System.out.println("***** updatedTherapySessions : "+updatedTherapySessions);
		return updatedTherapySessions;
	}
	
}
