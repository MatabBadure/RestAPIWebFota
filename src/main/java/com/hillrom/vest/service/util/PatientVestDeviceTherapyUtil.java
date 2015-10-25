package com.hillrom.vest.service.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;

public class PatientVestDeviceTherapyUtil {

	private static final String EVENT_CODE_DELIMITER = ":";
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
	private static final String EVENT_CODE_RAMPING_PAUSED = "21";
	private static final String EVENT_CODE_RAMP_REACHED_PAUSED = "24";
	private static final String EVENT_CODE_PROGRAM_PAUSED = "18";
	private static final String EVENT_NORMAL_PAUSED = "5";

	private PatientVestDeviceTherapyUtil(){
		
	}
	
	public static List<TherapySession> prepareTherapySessionFromDeviceData(List<PatientVestDeviceData> deviceData){
		List<TherapySession> therapySessions = new LinkedList<>();
		therapySessions = groupEventsToPrepareTherapySession(deviceData);
		return groupTherapySessionsByDay(therapySessions);
	}

	public static Map<String,Integer> getTherapyMetricsMap(
			List<PatientVestDeviceData> deviceEventRecords) {
		Map<String,Integer> metricsMap = new HashMap<>();
		int frequency = 0, pressure = 0, duration = 0, normalCoughPauses = 0, programmedCoughPauses = 0, caughPauseDuration = 0;
		int totalDuration = deviceEventRecords.stream().collect(Collectors.summingInt(PatientVestDeviceData::getDuration));
		for(PatientVestDeviceData deviceEventRecord : deviceEventRecords){
			frequency += calculateWeightedAvg( totalDuration,deviceEventRecord.getFrequency().longValue(),deviceEventRecord.getDuration());
			pressure += calculateWeightedAvg(totalDuration,deviceEventRecord.getPressure().longValue(),deviceEventRecord.getDuration());
			duration += deviceEventRecord.getDuration();
			if(deviceEventRecord.getEventId().startsWith(EVENT_CODE_COUGH_PAUSE) ||
					deviceEventRecord.getEventId().startsWith(EVENT_CODE_PROGRAM_PAUSED) ||
					deviceEventRecord.getEventId().startsWith(EVENT_CODE_RAMPING_PAUSED) ||
					deviceEventRecord.getEventId().startsWith(EVENT_CODE_RAMP_REACHED_PAUSED) 
					){
				++programmedCoughPauses;
				caughPauseDuration += deviceEventRecord.getDuration();
			}else if(deviceEventRecord.getEventId().startsWith(EVENT_NORMAL_PAUSED)){
				++normalCoughPauses;
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

	public static List<TherapySession> groupEventsToPrepareTherapySession(
			List<PatientVestDeviceData> deviceData) {
		List<TherapySession> therapySessions = new LinkedList<TherapySession>();
		for(int i = 0; i < deviceData.size() ; i++){
			PatientVestDeviceData vestDeviceData = deviceData.get(i);
			String eventCode = vestDeviceData.getEventId().split(EVENT_CODE_DELIMITER)[0];
			List<PatientVestDeviceData> groupEntries = new LinkedList<>();
			if(EVENT_CODE_NORMAL_START.equals(eventCode) ||
			   EVENT_CODE_PROGRAM_START.equals(eventCode) || 
			   EVENT_CODE_RAMP_STARTED.equals(eventCode)
			   ){
				groupEntries.add(vestDeviceData);
				for(int j = i+1; j < deviceData.size() ; j++){
					PatientVestDeviceData nextEventEntry = deviceData.get(j);
					String nextEventCode = nextEventEntry.getEventId().split(EVENT_CODE_DELIMITER)[0];
					groupEntries.add(nextEventEntry);
					if(EVENT_CODE_COMPLETED.equals(nextEventCode) ||
					   EVENT_CODE_PROGRAM_COMPLETED.equals(nextEventCode) ||
					   EVENT_CODE_NORMAL_INCOMPLETE.equals(nextEventCode) ||
					   EVENT_CODE_PROGRAM_INCOMPLETE.equals(nextEventCode) ||
					   EVENT_CODE_RAMP_COMPLETED.equals(nextEventCode) ||
					   EVENT_CODE_RAMP_INCOMPLETE.equals(nextEventCode)
							){
						TherapySession therapySession = assignTherapyMatrics(groupEntries);
						therapySessions.add(therapySession);
						break;
					}
				}
			}
		}
		return therapySessions;
	}

	public static TherapySession assignTherapyMatrics(
			List<PatientVestDeviceData> groupEntries) {
		Long timestamp = groupEntries.get(0).getTimestamp();
		User patientUser = groupEntries.get(0).getPatientUser();
		PatientInfo patient = groupEntries.get(0).getPatient();
		Map<String,Integer> metricsMap = getTherapyMetricsMap(groupEntries);
		TherapySession therapySession = new TherapySession();
		therapySession.setDate(LocalDate.fromDateFields(new Date(timestamp)));
		therapySession.setFrequency(metricsMap.get(FREQUENCY));
		therapySession.setPressure(metricsMap.get(PRESSURE));
		therapySession.setDurationInMinutes(metricsMap.get(DURATION).longValue());
		therapySession.setNormalCaughPauses(metricsMap.get(NORMAL_COUGH_PAUSES));
		therapySession.setProgrammedCaughPauses(metricsMap.get(PROGRAMMED_COUGH_PAUSES));
		therapySession.setCaughPauseDuration(metricsMap.get(CAUGH_PAUSE_DURATION));
		
		int size = groupEntries.size();
		therapySession.setHmr(groupEntries.get(size-1).getHmr());
		String sessionType = SESSION_TYPE_NORMAL;
		if(groupEntries.get(0).getEventId().contains(SESSION_TYPE_PROGRAM))
			sessionType = SESSION_TYPE_PROGRAM;
		therapySession.setSessionType(sessionType);
		therapySession.setStartTime(new DateTime(groupEntries.get(0).getTimestamp()));
		therapySession.setEndTime(new DateTime(groupEntries.get(size-1).getTimestamp()));
		therapySession.setPatientInfo(patient);
		therapySession.setPatientUser(patientUser);
		
		return therapySession;
	}
	
	public static List<TherapySession> groupTherapySessionsByDay(List<TherapySession> therapySessions){
		List<TherapySession> updatedTherapySessions = new LinkedList<>();
		Map<LocalDate,List<TherapySession>> groupByTherapyDate = therapySessions.stream()
		        .collect(Collectors.groupingBy(TherapySession::getDate));
		Iterator<LocalDate> keySetItr = groupByTherapyDate.keySet().iterator();
		while(keySetItr.hasNext()){
			List<TherapySession> groupedTherapySessions = groupByTherapyDate.get(keySetItr.next());
			for(int i =0 ; i<groupedTherapySessions.size();i++){
				TherapySession therapySession = groupedTherapySessions.get(i);
				therapySession.setSessionNo(i+1);
				updatedTherapySessions.add(therapySession);
				
			}
		}
		return updatedTherapySessions;
	}

	public static double calculateWeightedAvg(double totalDuration,Long durationInMinutes,
			Integer frequency) {
		return (double)durationInMinutes*frequency/totalDuration;
	}
	
	public static int calculateCumulativeDuration(List<TherapySession> therapySessions){
		return therapySessions.stream().collect(Collectors.summingLong(TherapySession::getDurationInMinutes)).intValue();
	}
	
	public static int calculateHMRRunRatePerDays(List<TherapySession> therapySessions,int days){
		return calculateCumulativeDuration(therapySessions)/days;
	}
}
