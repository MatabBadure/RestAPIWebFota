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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.service.AdherenceCalculationService;

public class PatientVestDeviceTherapyUtil {

	private static final float SECONDS_PER_MINUTE = 60f;
	private static final float MILLI_SECONDS_PER_MINUTE = 60000f;
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
	private static final String EVENT_CODE_NORMAL_COMPLETED = "3";
	private static final String EVENT_CODE_PROGRAM_PT1_START = "7";
	private static final String EVENT_CODE_NORMAL_START = "1";
	private static final String EVENT_CODE_PROGRAM_COMPLETED = "16";
	private static final String EVENT_CODE_RAMPING_PAUSED = "21";
	private static final String EVENT_CODE_RAMP_REACHED_PAUSED = "24";
	private static final String EVENT_CODE_PROGRAM_PAUSED = "18";
	private static final String EVENT_CODE_NORMAL_PAUSED = "5";
	private static final String EVENT_CODE_NORMAL_RESUMED = "6";
	private static final String EVENT_CODE_PROGRAM_RESUMED = "19";
	private static final String EVENT_CODE_RAMP_RESUMED = "27";

	private static final Logger log = LoggerFactory.getLogger(PatientVestDeviceTherapyUtil.class);

	private PatientVestDeviceTherapyUtil(){
		
	}
	
	public static List<TherapySession> prepareTherapySessionFromDeviceData(List<PatientVestDeviceData> deviceData) throws Exception{
		List<TherapySession> therapySessions = new LinkedList<>();
		therapySessions = groupEventsToPrepareTherapySession(deviceData);
		return groupTherapySessionsByDay(therapySessions);
	}

	public static Map<String,Integer> getTherapyMetricsMap(
			List<PatientVestDeviceData> deviceEventRecords) {
		Map<String,Integer> metricsMap = new HashMap<>();
		int durationOfSession = 0, normalCoughPauses = 0, programmedCoughPauses = 0, caughPauseDuration = 0;
		int durationForWeightedAvgCalc = getTotalDurationForWeightedAvgCalculation(deviceEventRecords);
		float frequency = 0, pressure = 0;
		for(int i = 0;i < deviceEventRecords.size(); i ++){
			PatientVestDeviceData deviceEventRecord = deviceEventRecords.get(i);
			frequency += calculateWeightedAvg( durationForWeightedAvgCalc,deviceEventRecord.getDuration(),deviceEventRecord.getFrequency());
			pressure += calculateWeightedAvg(durationForWeightedAvgCalc,deviceEventRecord.getDuration(),deviceEventRecord.getPressure());
			if(isProgrammedCoughPause(deviceEventRecord))
				++programmedCoughPauses;
			if(isNormalCoughPause(deviceEventRecord))
				++normalCoughPauses;
		}
		durationOfSession = calculateDurationOfSession(deviceEventRecords);
		caughPauseDuration = getCoughPauseDuration(deviceEventRecords,durationOfSession);
		metricsMap.put(FREQUENCY, Math.round(frequency));
		metricsMap.put(PRESSURE, Math.round(pressure));
		metricsMap.put(DURATION, durationOfSession);
		metricsMap.put(NORMAL_COUGH_PAUSES, normalCoughPauses);
		metricsMap.put(PROGRAMMED_COUGH_PAUSES, programmedCoughPauses);
		metricsMap.put(CAUGH_PAUSE_DURATION, caughPauseDuration);
		return metricsMap;
	}

	private static int getCoughPauseDuration(List<PatientVestDeviceData> deviceEventRecords,int durationOfSession) {
		PatientVestDeviceData endOfSessionEvent = deviceEventRecords.get(deviceEventRecords.size()-1);
		long endTimestamp = endOfSessionEvent.getTimestamp();
		long startTimestamp = deviceEventRecords.get(0).getTimestamp();
		int totalDuration = (int) Math.round((endTimestamp-startTimestamp)/MILLI_SECONDS_PER_MINUTE);
		return (totalDuration - durationOfSession) > 0 ? (totalDuration - durationOfSession) : 0;
	}

	private static int calculateDurationOfSession(
			List<PatientVestDeviceData> deviceEventRecords) {
		double endHmr = deviceEventRecords.get(deviceEventRecords.size()-1).getHmr();
		double startHmr = deviceEventRecords.get(0).getHmr();
		return (int)Math.round((endHmr - startHmr)/SECONDS_PER_MINUTE);
	}

	/**
	 *  Return totalDuration except the incomplete event
	 * @param deviceEventRecords
	 * @return
	 */
	private static Integer getTotalDurationForWeightedAvgCalculation(
			List<PatientVestDeviceData> deviceEventRecords) {
		int totalDuration = 0;
		for(int i = 0;i <deviceEventRecords.size();i++ ){
			PatientVestDeviceData eventRecord = deviceEventRecords.get(i);
			if(!isInCompleteEvent(eventRecord)){
				totalDuration += eventRecord.getDuration();
			}
		}
		return totalDuration;
	}

	private static boolean isNormalCoughPause(
			PatientVestDeviceData deviceEventRecord) {
		return deviceEventRecord.getEventId().startsWith(EVENT_CODE_NORMAL_PAUSED) ||
				deviceEventRecord.getEventId().startsWith(EVENT_CODE_RAMPING_PAUSED) || 
				deviceEventRecord.getEventId().startsWith(EVENT_CODE_PROGRAM_PAUSED) ||
				deviceEventRecord.getEventId().startsWith(EVENT_CODE_RAMP_REACHED_PAUSED);
	}

	private static boolean isProgrammedCoughPause(
			PatientVestDeviceData deviceEventRecord) {
		return deviceEventRecord.getEventId().startsWith(EVENT_CODE_COUGH_PAUSE);
	}

	private static boolean isInCompleteEvent(
			PatientVestDeviceData nextEvent) {
		if(nextEvent.getEventId().startsWith(EVENT_CODE_NORMAL_INCOMPLETE) || 
				   nextEvent.getEventId().startsWith(EVENT_CODE_PROGRAM_INCOMPLETE) ||
				   nextEvent.getEventId().startsWith(EVENT_CODE_RAMP_INCOMPLETE)){
			return true;
		}else{
			return false;
		}
	}
	
	public static List<TherapySession> groupEventsToPrepareTherapySession(
			List<PatientVestDeviceData> deviceData) throws Exception{
		List<TherapySession> therapySessions = new LinkedList<TherapySession>();
		// This List will hold un-finished session events , will be discarded to get delta on next transmission 
		List<PatientVestDeviceData> eventsToBeDiscarded = new LinkedList<>();
		for(int i = 0;i < deviceData.size() ; i++){
			PatientVestDeviceData vestDeviceData = deviceData.get(i);
			String eventCode = vestDeviceData.getEventId().split(EVENT_CODE_DELIMITER)[0];
			List<PatientVestDeviceData> groupEntries = new LinkedList<>();
			if(isStartEventForTherapySession(eventCode)
			   ){
				groupEntries.add(vestDeviceData);
				for(int j = i+1; j < deviceData.size() ; j++){
					PatientVestDeviceData nextEventEntry = deviceData.get(j);
					String nextEventCode = nextEventEntry.getEventId().split(EVENT_CODE_DELIMITER)[0];
						// Group entry if the nextEvent is not a start event
						if(!isStartEventForTherapySession(nextEventCode))
							groupEntries.add(nextEventEntry);
					if(isCompleteOrInCompleteEventForTherapySession(nextEventCode)
						|| isStartEventForTherapySession(nextEventCode)	){
						// subsequent start events indicate therapy is incomplete due to unexpected reason
						if(isStartEventForTherapySession(nextEventCode)){
							PatientVestDeviceData inCompleteEvent = (PatientVestDeviceData) groupEntries.get(groupEntries.size()-1).clone();
							if(groupEntries.get(0).getEventId().contains(SESSION_TYPE_PROGRAM))
								inCompleteEvent.setEventId(getEventStringByEventCode(Integer.parseInt(EVENT_CODE_PROGRAM_INCOMPLETE)));
							else
								inCompleteEvent.setEventId(getEventStringByEventCode(Integer.parseInt(EVENT_CODE_NORMAL_INCOMPLETE)));
							inCompleteEvent.setDuration(0);// DO NOT CHANGE: This is the indication, dummy event has been added for making session
							inCompleteEvent.setFrequency(0);
							inCompleteEvent.setPressure(0);
							groupEntries.add(inCompleteEvent);// Add dummy incomplete event to finish the session
							deviceData.add(j, inCompleteEvent);
						}
						TherapySession therapySession = assignTherapyMatrics(groupEntries);
						therapySessions.add(therapySession);
						i=j; // to skip the events iterated, shouldn't be removed in any case
						break;
					}else if(j == deviceData.size()-1){
						//will be discarded to get delta on next transmission
						log.debug("Discarding the events to make session with delta on next transmission");
						log.debug("Events List"+groupEntries);
						eventsToBeDiscarded.addAll(groupEntries);
						i=j; // to skip the events iterated, shouldn't be removed in any case
						break;
					}
				}
			}
		}
		// Discarding these events to make session from delta
		if(eventsToBeDiscarded.size() > 0){
			deviceData.removeAll(eventsToBeDiscarded);
		}
		return therapySessions;
	}

	private static boolean isCompleteOrInCompleteEventForTherapySession(
			String nextEventCode) {
		return EVENT_CODE_NORMAL_COMPLETED.equals(nextEventCode) ||
		   EVENT_CODE_PROGRAM_COMPLETED.equals(nextEventCode) ||
		   EVENT_CODE_NORMAL_INCOMPLETE.equals(nextEventCode) ||
		   EVENT_CODE_PROGRAM_INCOMPLETE.equals(nextEventCode) ||
		   EVENT_CODE_RAMP_COMPLETED.equals(nextEventCode) ||
		   EVENT_CODE_RAMP_INCOMPLETE.equals(nextEventCode);
	}

	private static boolean isStartEventForTherapySession(String eventCode) {
		return EVENT_CODE_NORMAL_START.equals(eventCode) ||
		   EVENT_CODE_PROGRAM_PT1_START.equals(eventCode) ||
		   EVENT_CODE_RAMP_STARTED.equals(eventCode);
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
		therapySession.setDurationInMinutes(metricsMap.get(DURATION));
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
	
	public static List<TherapySession> groupTherapySessionsByDay(List<TherapySession> therapySessions)
			throws Exception{
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

	public static double calculateWeightedAvg(double totalDuration,int durationInMinutes,
			Integer frequency) {
		if(totalDuration == 0) // safety check for divided by 0 exception
			return 0;
		return (durationInMinutes*frequency/totalDuration);
	}
	
	public static int calculateCumulativeDuration(List<TherapySession> therapySessions){
		return therapySessions.stream().collect(Collectors.summingInt(TherapySession::getDurationInMinutes));
	}
	
	public static int calculateHMRRunRatePerSession(List<TherapySession> therapySessions){
		float sessionsCount = therapySessions.isEmpty()?1:therapySessions.size();
		return Math.round(calculateCumulativeDuration(therapySessions)/sessionsCount);
	}
	
	public static String getEventStringByEventCode(int eventCode) {

		String eventString;
		switch (eventCode) {
		case 1:
			eventString = eventCode + ":SessionEventCodeNormalStarted";
			break;
		case 2:
			eventString = eventCode + ":SessionEventCodeNormalSPChanged";
			break;
		case 3:
			eventString = eventCode + ":SessionEventCodeCompleted";
			break;
		case 4:
			eventString = eventCode + ":SessionEventCodeNormalIncomplete";
			break;
		case 5:
			eventString = eventCode + ":SessionEventCodeNormalPaused";
			break;
		case 6:
			eventString = eventCode + ":SessionEventCodeNormalResumed";
			break;
		case 7:
			eventString = eventCode + ":SessionEventCodeProgramPt1Started";
			break;
		case 8:
			eventString = eventCode + ":SessionEventCodeProgramPt2Started";
			;
			break;
		case 9:
			eventString = eventCode + ":SessionEventCodeProgramPt3Started";
			;
			break;
		case 10:
			eventString = eventCode + ":SessionEventCodeProgramPt4Started";
			;
			break;
		case 11:
			eventString = eventCode + ":SessionEventCodeProgramPt5Started";
			break;
		case 12:
			eventString = eventCode + ":SessionEventCodeProgramPt6Started";
			;
			break;
		case 13:
			eventString = eventCode + ":SessionEventCodeProgramPt7Started";
			;
			break;
		case 14:
			eventString = eventCode + ":SessionEventCodeProgramPt8Started";
			;
			break;
		case 15:
			eventString = eventCode + ":SessionEventCodeProgramSPChanged";
			;
			break;
		case 16:
			eventString = eventCode + ":SessionEventCodeProgramCompleted";
			;
			break;
		case 17:
			eventString = eventCode + ":SessionEventCodeProgramIncomplete";
			;
			break;
		case 18:
			eventString = eventCode + ":SessionEventCodeProgramPaused";
			;
			break;
		case 19:
			eventString = eventCode + ":SessionEventCodeProgramResumed";
			;
			break;
		case 20:
			eventString = eventCode + ":SessionEventCodeRampStarted";
			;
			break;
		case 21:
			eventString = eventCode + ":SessionEventCodeRampingPaused";
			;
			break;
		case 22:
			eventString = eventCode + ":SessionEventCodeRampReached";
			;
			break;
		case 23:
			eventString = eventCode + ":SessionEventCodeRampReachedSPChanged";
			;
			break;
		case 24:
			eventString = eventCode + ":SessionEventCodeRampReachedPaused";
			;
			break;
		case 25:
			eventString = eventCode + ":SessionEventCodeRampCompleted";
			;
			break;
		case 26:
			eventString = eventCode + ":SessionEventCodeRampIncomplete";
			;
			break;
		case 27:
			eventString = eventCode + ":SessionEventCodeRampResumed";
			;
			break;
		case 28:
			eventString = eventCode + ":SessionEventCodeCoughPaused";
			;
			break;
		default:
			eventString = 0 + ":Unknown";
			break;
		}
		return eventString;
	}
}
