package com.hillrom.vest.service.util.monarch;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.PatientVestDeviceHistoryMonarch;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.TherapySessionMonarch;
import com.hillrom.vest.domain.User;

public class PatientVestDeviceTherapyUtilMonarch {

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
	
	private static final String INTENSITY = "intensity";
	private static final String EVENT_CODE_NORMAL_START_MONARCH = "1";
	private static final String EVENT_CODE_NORMAL_DATA_CHANGE_MONARCH = "2";
	private static final String EVENT_CODE_NORMAL_COMPLETE_MONARCH = "3";
	private static final String EVENT_CODE_NORMAL_INCOMPLETE_MONARCH = "4";
	private static final String EVENT_CODE_NORMAL_PAUSE_MONARCH = "5";
	private static final String EVENT_CODE_NORMAL_RESUME_MONARCH = "6";	
	private static final String EVENT_CODE_PROGRAM_STEP1_START_MONARCH = "7";	
	private static final String EVENT_CODE_PROGRAM_STEP2_START_MONARCH = "8";
	private static final String EVENT_CODE_PROGRAM_STEP3_START_MONARCH = "9";
	private static final String EVENT_CODE_PROGRAM_STEP4_START_MONARCH = "10";
	private static final String EVENT_CODE_PROGRAM_STEP5_START_MONARCH = "11";	
	private static final String EVENT_CODE_PROGRAM_STEP6_START_MONARCH = "12";
	private static final String EVENT_CODE_PROGRAM_STEP7_START_MONARCH = "13";
	private static final String EVENT_CODE_PROGRAM_STEP8_START_MONARCH = "14";	
	private static final String EVENT_CODE_PROGRAM_COMPLETE_MONARCH = "15";	
	private static final String EVENT_CODE_PROGRAM_INCOMPLETE_MONARCH = "16";
	private static final String EVENT_CODE_PROGRAM_PAUSE_MONARCH = "17";
	private static final String EVENT_CODE_PROGRAM_RESUME_MONARCH = "18";
	private static final String EVENT_CODE_PROGRAM_DATA_CHANGE_MONARCH = "19";	
	private static final String EVENT_CODE_COUGH_PAUSE_START_MONARCH = "20";	
	private static final String EVENT_CODE_COUGH_PAUSE_END_MONARCH = "21";
	private static final String EVENT_CODE_ERROR_MONARCH = "22";
	private static final String EVENT_CODE_BT_CHANGE_SOURCE_MONARCH = "23";
	private static final String EVENT_CODE_POWER_CONNECTED_MONARCH = "24";
	private static final String EVENT_CODE_POWER_DISCONNECTED_MONARCH = "25";
	
	

	private static final Logger log = LoggerFactory.getLogger(PatientVestDeviceTherapyUtilMonarch.class);

	private PatientVestDeviceTherapyUtilMonarch(){
		
	}
	
	public static List<TherapySessionMonarch> prepareTherapySessionFromDeviceDataMonarch(List<PatientVestDeviceDataMonarch> deviceDataMonarch,PatientVestDeviceHistoryMonarch latestInActiveDeviceHistory) throws Exception{
		List<TherapySessionMonarch> therapySessionsMonarch = new LinkedList<>();
		therapySessionsMonarch = groupEventsToPrepareTherapySessionMonarch(deviceDataMonarch,latestInActiveDeviceHistory);
		return groupTherapySessionsByDayMonarch(therapySessionsMonarch);
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
	
	public static Map<String,Integer> getTherapyMetricsMapMonarch(
			List<PatientVestDeviceDataMonarch> deviceEventRecordsMonarch) {
		Map<String,Integer> metricsMapMonarch = new HashMap<>();
		//int durationOfSession = 0, normalCoughPauses = 0, programmedCoughPauses = 0, caughPauseDuration = 0;
		int normalCoughPauses = 0,totalCoughPauseDuration = 0;
		int durationOfSessionMonarch = 0, coughPauses = 0, caughPauseDuration = 0;
		int durationForWeightedAvgCalcMonarch = getTotalDurationForWeightedAvgCalculationMonarch(deviceEventRecordsMonarch);
		float frequency = 0, intensity = 0;
		int startCoughPause=-1, endCoughPause = -1, startNormalCoughPause = -1, endNormalCoughPause = -1;
		for(int i = 0;i < deviceEventRecordsMonarch.size(); i ++){
			PatientVestDeviceDataMonarch deviceEventRecordMonarch = deviceEventRecordsMonarch.get(i);
			frequency += calculateWeightedAvg( durationForWeightedAvgCalcMonarch,deviceEventRecordMonarch.getDuration(),deviceEventRecordMonarch.getFrequency());
			intensity += calculateWeightedAvg(durationForWeightedAvgCalcMonarch,deviceEventRecordMonarch.getDuration(),deviceEventRecordMonarch.getIntensity());
			
			if(isNormalStartCoughPauseMonarch(deviceEventRecordMonarch)){
				++normalCoughPauses;
				startNormalCoughPause = i;
			}else if(isStartCoughPauseMonarch(deviceEventRecordMonarch)){
				++coughPauses;
				startCoughPause = i;
			}
			
			if(isNormalEndCoughPauseMonarch(deviceEventRecordMonarch) || isProgrammedStartCoughPauseMonarch(deviceEventRecordMonarch))
				endNormalCoughPause = i;
			else if(isEndCoughPauseMonarch(deviceEventRecordMonarch) || isProgrammedEndCoughPauseMonarch(deviceEventRecordMonarch))
				endCoughPause = i;
			
			if(startCoughPause>0 && endCoughPause>0 && endCoughPause == (startCoughPause+1)){
				caughPauseDuration = calculateCoughPauseMonarch(deviceEventRecordsMonarch,startCoughPause, endCoughPause);
				if(caughPauseDuration<30){
					--coughPauses;
				}else{
					totalCoughPauseDuration += caughPauseDuration; 
				}
			}else if(startNormalCoughPause>0 && endNormalCoughPause>0 && endNormalCoughPause == (startNormalCoughPause+1)){
				caughPauseDuration = calculateCoughPauseMonarch(deviceEventRecordsMonarch,startNormalCoughPause, endNormalCoughPause);
				if(caughPauseDuration<30){
					--normalCoughPauses;
				}else{
					totalCoughPauseDuration += caughPauseDuration; 
				}
			}
		}
		durationOfSessionMonarch = calculateDurationOfSessionMonarch(deviceEventRecordsMonarch);
		
		metricsMapMonarch.put(FREQUENCY, Math.round(frequency));
		metricsMapMonarch.put(INTENSITY, Math.round(intensity));
		metricsMapMonarch.put(DURATION, durationOfSessionMonarch);
		metricsMapMonarch.put(NORMAL_COUGH_PAUSES, normalCoughPauses);
		metricsMapMonarch.put(PROGRAMMED_COUGH_PAUSES, coughPauses);
		metricsMapMonarch.put(CAUGH_PAUSE_DURATION, totalCoughPauseDuration);
		return metricsMapMonarch;
	}

	private static int getCoughPauseDuration(List<PatientVestDeviceData> deviceEventRecords,int durationOfSession) {
		PatientVestDeviceData endOfSessionEvent = deviceEventRecords.get(deviceEventRecords.size()-1);
		long endTimestamp = endOfSessionEvent.getTimestamp();
		long startTimestamp = deviceEventRecords.get(0).getTimestamp();
		int totalDuration = (int) Math.round((endTimestamp-startTimestamp)/MILLI_SECONDS_PER_MINUTE);
		return (totalDuration - durationOfSession) > 0 ? (totalDuration - durationOfSession) : 0;
	}
	
	private static int getCoughPauseDurationMonarch(List<PatientVestDeviceDataMonarch> deviceEventRecords,int durationOfSession) {
		PatientVestDeviceDataMonarch endOfSessionEvent = deviceEventRecords.get(deviceEventRecords.size()-1);
		long endTimestamp = endOfSessionEvent.getTimestamp();
		long startTimestamp = deviceEventRecords.get(0).getTimestamp();
		int totalDuration = (int) Math.round((endTimestamp-startTimestamp)/MILLI_SECONDS_PER_MINUTE);
		return (totalDuration - durationOfSession) > 0 ? (totalDuration - durationOfSession) : 0;
	}

	private static int calculateDurationOfSession(
			List<PatientVestDeviceData> deviceEventRecords) {
		// HMR Difference
		double endHmr = deviceEventRecords.get(deviceEventRecords.size()-1).getHmr();
		double startHmr = deviceEventRecords.get(0).getHmr();
		int hmrDiff  = (int)Math.round((endHmr - startHmr)/SECONDS_PER_MINUTE);
		
		// Duration of the device was run
		PatientVestDeviceData endOfSessionEvent = deviceEventRecords.get(deviceEventRecords.size()-1);
		long endTimestamp = endOfSessionEvent.getTimestamp();
		long startTimestamp = deviceEventRecords.get(0).getTimestamp();
		int totalDuration = (int) Math.round((endTimestamp-startTimestamp)/MILLI_SECONDS_PER_MINUTE);
		
		// HILL-1384 : since it is observed , hmr being corrupted
		if(hmrDiff > totalDuration)
			return totalDuration;
		else
			return hmrDiff;
		
	}
	
	private static int calculateDurationOfSessionMonarch(
			List<PatientVestDeviceDataMonarch> deviceEventRecordsMonarch) {
		// HMR Difference
		double endHmr = deviceEventRecordsMonarch.get(deviceEventRecordsMonarch.size()-1).getHmr();
		double startHmr = deviceEventRecordsMonarch.get(0).getHmr();
		int hmrDiff  = (int)Math.round((endHmr - startHmr)/SECONDS_PER_MINUTE);
		
		// Duration of the device was run
		PatientVestDeviceDataMonarch endOfSessionEvent = deviceEventRecordsMonarch.get(deviceEventRecordsMonarch.size()-1);
		long endTimestamp = endOfSessionEvent.getTimestamp();
		long startTimestamp = deviceEventRecordsMonarch.get(0).getTimestamp();
		int totalDuration = (int) Math.round((endTimestamp-startTimestamp)/MILLI_SECONDS_PER_MINUTE);
		
		
		// HILL-1384 : since it is observed , hmr being corrupted
		/*if(hmrDiff > totalDuration)
			return totalDuration;
		else
			return hmrDiff;*/
		
		// Not consider the HMR, and considering only the timestamp difference
		return totalDuration;
		
	}
	
	private static int calculateCoughPauseMonarch(
			List<PatientVestDeviceDataMonarch> deviceEventRecordsMonarch,int startCoughPause, int endCoughPause) {
		// HMR Difference
		
		long endCoughPauseHMR = deviceEventRecordsMonarch.get(endCoughPause).getTimestamp();
		long startCoughPauseHMR = deviceEventRecordsMonarch.get(startCoughPause).getTimestamp();
		int coughPauseDuration = (int)Math.round((endCoughPauseHMR - startCoughPauseHMR)/1000);
		
		return coughPauseDuration;		
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
	
	private static Integer getTotalDurationForWeightedAvgCalculationMonarch(
			List<PatientVestDeviceDataMonarch> deviceEventRecords) {
		int totalDuration = 0;
		for(int i = 0;i <deviceEventRecords.size();i++ ){
			PatientVestDeviceDataMonarch eventRecord = deviceEventRecords.get(i);
			if(!isInCompleteEventMonarch(eventRecord)){
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
	
	/*private static boolean isNormalCoughPauseMonarch(
			PatientVestDeviceDataMonarch deviceEventRecord) {
		return deviceEventRecord.getEventCode().startsWith(EVENT_CODE_NORMAL_PAUSED) ||
				deviceEventRecord.getEventCode().startsWith(EVENT_CODE_RAMPING_PAUSED) || 
				deviceEventRecord.getEventCode().startsWith(EVENT_CODE_PROGRAM_PAUSED) ||
				deviceEventRecord.getEventCode().startsWith(EVENT_CODE_RAMP_REACHED_PAUSED);
	}*/

	private static boolean isProgrammedCoughPause(
			PatientVestDeviceData deviceEventRecord) {
		return deviceEventRecord.getEventId().startsWith(EVENT_CODE_COUGH_PAUSE);
	}
	
	/*private static boolean isProgrammedCoughPauseMonarch(
			PatientVestDeviceDataMonarch deviceEventRecord) {
		return deviceEventRecord.getEventCode().startsWith(EVENT_CODE_COUGH_PAUSE);
	}*/
	
	private static boolean isNormalStartCoughPauseMonarch(
			PatientVestDeviceDataMonarch deviceEventRecord) {
		return deviceEventRecord.getEventCode().startsWith(EVENT_CODE_NORMAL_PAUSE_MONARCH);
	}
	
	private static boolean isNormalEndCoughPauseMonarch(
			PatientVestDeviceDataMonarch deviceEventRecord) {
		return deviceEventRecord.getEventCode().startsWith(EVENT_CODE_NORMAL_RESUME_MONARCH);
	}

	private static boolean isProgrammedStartCoughPauseMonarch(
			PatientVestDeviceDataMonarch deviceEventRecord) {
		return deviceEventRecord.getEventCode().startsWith(EVENT_CODE_PROGRAM_PAUSE_MONARCH);
	}
	
	private static boolean isProgrammedEndCoughPauseMonarch(
			PatientVestDeviceDataMonarch deviceEventRecord) {
		return deviceEventRecord.getEventCode().startsWith(EVENT_CODE_PROGRAM_RESUME_MONARCH);
	}
	
	private static boolean isStartCoughPauseMonarch(
			PatientVestDeviceDataMonarch deviceEventRecord) {
		return deviceEventRecord.getEventCode().startsWith(EVENT_CODE_COUGH_PAUSE_START_MONARCH);
	}
	
	private static boolean isEndCoughPauseMonarch(
			PatientVestDeviceDataMonarch deviceEventRecord) {
		return deviceEventRecord.getEventCode().startsWith(EVENT_CODE_COUGH_PAUSE_END_MONARCH);
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
	
	private static boolean isInCompleteEventMonarch(
			PatientVestDeviceDataMonarch nextEvent) {
		if(nextEvent.getEventCode().startsWith(EVENT_CODE_NORMAL_INCOMPLETE_MONARCH) || 
				   nextEvent.getEventCode().startsWith(EVENT_CODE_PROGRAM_INCOMPLETE_MONARCH) ){
			return true;
		}else{
			return false;
		}
	}
	
	public static List<TherapySessionMonarch> groupEventsToPrepareTherapySessionMonarch(
			List<PatientVestDeviceDataMonarch> deviceDataMonarch,PatientVestDeviceHistoryMonarch latestInActiveDeviceHistory) throws Exception{
		List<TherapySessionMonarch> therapySessions = new LinkedList<TherapySessionMonarch>();
		// This List will hold un-finished session events , will be discarded to get delta on next transmission 
		List<PatientVestDeviceDataMonarch> eventsToBeDiscardedMonarch = new LinkedList<>();
		for(int i = 0;i < deviceDataMonarch.size() ; i++){
			PatientVestDeviceDataMonarch vestDeviceDataMonarch = deviceDataMonarch.get(i);
			//String eventCode = vestDeviceData.getEventCode().split(EVENT_CODE_DELIMITER)[0];
			String eventCodeMonarch = vestDeviceDataMonarch.getEventCode();
			List<PatientVestDeviceDataMonarch> groupEntriesMonarch = new LinkedList<>();
			if(isStartEventForTherapySessionMonarch(eventCodeMonarch)){
				groupEntriesMonarch.add(vestDeviceDataMonarch);
				for(int j = i+1; j < deviceDataMonarch.size() ; j++){
					PatientVestDeviceDataMonarch nextEventEntryMonarch = deviceDataMonarch.get(j);
					String nextEventCodeMonarch = nextEventEntryMonarch.getEventCode();
						// Group entry if the nextEvent is not a start event
						if(!isStartEventForTherapySessionMonarch(nextEventCodeMonarch))
							groupEntriesMonarch.add(nextEventEntryMonarch);
					if(isCompleteOrInCompleteEventForTherapySessionMonarch(nextEventCodeMonarch)
						|| isStartEventForTherapySessionMonarch(nextEventCodeMonarch)	){
						// subsequent start events indicate therapy is incomplete due to unexpected reason
						if(isStartEventForTherapySessionMonarch(nextEventCodeMonarch)){
							PatientVestDeviceDataMonarch inCompleteEventMonarch = (PatientVestDeviceDataMonarch) groupEntriesMonarch.get(groupEntriesMonarch.size()-1).clone();
							if(groupEntriesMonarch.get(0).getEventCode().equals(EVENT_CODE_PROGRAM_COMPLETE_MONARCH))
								inCompleteEventMonarch.setEventCode(getEventStringByEventCode(Integer.parseInt(EVENT_CODE_PROGRAM_INCOMPLETE_MONARCH)));
							else
								inCompleteEventMonarch.setEventCode(getEventStringByEventCode(Integer.parseInt(EVENT_CODE_NORMAL_INCOMPLETE_MONARCH)));
							inCompleteEventMonarch.setDuration(0);// DO NOT CHANGE: This is the indication, dummy event has been added for making session
							inCompleteEventMonarch.setFrequency(0);
							inCompleteEventMonarch.setIntensity(0);
							groupEntriesMonarch.add(inCompleteEventMonarch);// Add dummy incomplete event to finish the session
							deviceDataMonarch.add(j, inCompleteEventMonarch);
						}
						TherapySessionMonarch therapySession = assignTherapyMatricsMonarch(groupEntriesMonarch);
						//applyGlobalHMRMonarch(therapySession,latestInActiveDeviceHistory);
						therapySessions.add(therapySession);
						i=j; // to skip the events iterated, shouldn't be removed in any case
						break;
					}else if(j == deviceDataMonarch.size()-1){
						//will be discarded to get delta on next transmission
						log.debug("Discarding the events to make session with delta on next transmission");
						log.debug("Events List"+groupEntriesMonarch);
						eventsToBeDiscardedMonarch.addAll(groupEntriesMonarch);
						i=j; // to skip the events iterated, shouldn't be removed in any case
						break;
					}
				}
			}
		}
		// Discarding these events to make session from delta
		if(eventsToBeDiscardedMonarch.size() > 0){
			deviceDataMonarch.removeAll(eventsToBeDiscardedMonarch);
		}
		return therapySessions;
	}

	private static void applyGlobalHMR(TherapySessionMonarch therapySession,
			PatientVestDeviceHistory latestInActiveDeviceHistory)throws Exception {
		if( Objects.nonNull(latestInActiveDeviceHistory) && (therapySession.getHmr() < latestInActiveDeviceHistory.getHmr())){
			if(!(latestInActiveDeviceHistory.getSerialNumber().equalsIgnoreCase(therapySession.getSerialNumber()))){
				therapySession.setHmr(therapySession.getHmr()+latestInActiveDeviceHistory.getHmr());
			}
		}
	}
	
	private static void applyGlobalHMRMonarch(TherapySessionMonarch therapySession,
			PatientVestDeviceHistoryMonarch latestInActiveDeviceHistory)throws Exception {
		if( Objects.nonNull(latestInActiveDeviceHistory) && (therapySession.getHmr() < latestInActiveDeviceHistory.getHmr())){
			if(!(latestInActiveDeviceHistory.getSerialNumber().equalsIgnoreCase(therapySession.getSerialNumber()))){
				therapySession.setHmr(therapySession.getHmr()+latestInActiveDeviceHistory.getHmr());
			}
		}
	}
	
	private static boolean isCompleteOrInCompleteEventForTherapySessionMonarch(
			String nextEventCode) {
		return EVENT_CODE_NORMAL_COMPLETE_MONARCH.equals(nextEventCode) ||
				EVENT_CODE_PROGRAM_COMPLETE_MONARCH.equals(nextEventCode) ||
				EVENT_CODE_NORMAL_INCOMPLETE_MONARCH.equals(nextEventCode) ||
				EVENT_CODE_PROGRAM_INCOMPLETE_MONARCH.equals(nextEventCode);
	}

	private static boolean isStartEventForTherapySessionMonarch(String eventCode) {
		return EVENT_CODE_NORMAL_START_MONARCH.equals(eventCode) ||
				EVENT_CODE_PROGRAM_STEP1_START_MONARCH.equals(eventCode);
	}

	public static TherapySessionMonarch assignTherapyMatrics(
			List<PatientVestDeviceData> groupEntries) {
		Long timestamp = groupEntries.get(0).getTimestamp();
		User patientUser = groupEntries.get(0).getPatientUser();
		PatientInfo patient = groupEntries.get(0).getPatient();
		Map<String,Integer> metricsMap = getTherapyMetricsMap(groupEntries);
		TherapySessionMonarch therapySession = new TherapySessionMonarch();
		therapySession.setSerialNumber(groupEntries.get(0).getSerialNumber());
		therapySession.setBluetoothId(groupEntries.get(0).getBluetoothId());
		therapySession.setDate(LocalDate.fromDateFields(new Date(timestamp)));
		therapySession.setFrequency(metricsMap.get(FREQUENCY));
		therapySession.setIntensity(metricsMap.get(INTENSITY));
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
	
	public static TherapySessionMonarch assignTherapyMatricsMonarch(
			List<PatientVestDeviceDataMonarch> groupEntriesMonarch) {
		Long timestamp = groupEntriesMonarch.get(0).getTimestamp();
		User patientUser = groupEntriesMonarch.get(0).getPatientUser();
		PatientInfo patient = groupEntriesMonarch.get(0).getPatient();
		Map<String,Integer> metricsMapMonarch = getTherapyMetricsMapMonarch(groupEntriesMonarch);
		TherapySessionMonarch therapySessionMonarch = new TherapySessionMonarch();
		therapySessionMonarch.setSerialNumber(groupEntriesMonarch.get(0).getSerialNumber());
		//therapySession.setBluetoothId(groupEntries.get(0).getBluetoothId());
		therapySessionMonarch.setDate(LocalDate.fromDateFields(new Date(timestamp)));
		therapySessionMonarch.setFrequency(metricsMapMonarch.get(FREQUENCY));
		therapySessionMonarch.setIntensity(metricsMapMonarch.get(INTENSITY));
		therapySessionMonarch.setDurationInMinutes(metricsMapMonarch.get(DURATION));
		therapySessionMonarch.setNormalCaughPauses(metricsMapMonarch.get(NORMAL_COUGH_PAUSES));
		therapySessionMonarch.setProgrammedCaughPauses(metricsMapMonarch.get(PROGRAMMED_COUGH_PAUSES));
		therapySessionMonarch.setCaughPauseDuration(metricsMapMonarch.get(CAUGH_PAUSE_DURATION));
		
		int size = groupEntriesMonarch.size();
		therapySessionMonarch.setHmr(groupEntriesMonarch.get(size-1).getHmr());
		String sessionType = SESSION_TYPE_PROGRAM;
		if(groupEntriesMonarch.get(0).getEventCode().equals(EVENT_CODE_NORMAL_START_MONARCH))
			sessionType = SESSION_TYPE_NORMAL;
		therapySessionMonarch.setSessionType(sessionType);
		therapySessionMonarch.setStartTime(new DateTime(groupEntriesMonarch.get(0).getTimestamp()));
		therapySessionMonarch.setEndTime(new DateTime(groupEntriesMonarch.get(size-1).getTimestamp()));
		therapySessionMonarch.setPatientInfo(patient);
		therapySessionMonarch.setPatientUser(patientUser);
		
		therapySessionMonarch.setTherapyIndex(groupEntriesMonarch.get(0).getTherapyIndex());
		therapySessionMonarch.setStartBatteryLevel(groupEntriesMonarch.get(0).getStartBatteryLevel());
		therapySessionMonarch.setEndBatteryLevel(groupEntriesMonarch.get(0).getEndBatteryLevel());
		therapySessionMonarch.setNumberOfEvents(groupEntriesMonarch.get(0).getNumberOfEvents());
		therapySessionMonarch.setNumberOfPods(groupEntriesMonarch.get(0).getNumberOfPods());		
		therapySessionMonarch.setDevWifi(groupEntriesMonarch.get(0).getDevWifi());
		therapySessionMonarch.setDevLte(groupEntriesMonarch.get(0).getDevLte());
		therapySessionMonarch.setDevBt(groupEntriesMonarch.get(0).getDevBt());
		therapySessionMonarch.setDevVersion(groupEntriesMonarch.get(0).getDevVersion());
		
		return therapySessionMonarch;
	}
	
	public static List<TherapySessionMonarch> groupTherapySessionsByDay(List<TherapySessionMonarch> therapySessions)
			throws Exception{
		List<TherapySessionMonarch> updatedTherapySessions = new LinkedList<>();
		Map<LocalDate,List<TherapySessionMonarch>> groupByTherapyDate = therapySessions.stream()
		        .collect(Collectors.groupingBy(TherapySessionMonarch::getDate));
		Iterator<LocalDate> keySetItr = groupByTherapyDate.keySet().iterator();
		while(keySetItr.hasNext()){
			List<TherapySessionMonarch> groupedTherapySessions = groupByTherapyDate.get(keySetItr.next());
			for(int i =0 ; i<groupedTherapySessions.size();i++){
				TherapySessionMonarch therapySession = groupedTherapySessions.get(i);
				therapySession.setSessionNo(i+1);
				updatedTherapySessions.add(therapySession);
				
			}
		}
		return updatedTherapySessions;
	}
	
	public static List<TherapySessionMonarch> groupTherapySessionsByDayMonarch(List<TherapySessionMonarch> therapySessions)
			throws Exception{
		List<TherapySessionMonarch> updatedTherapySessionsMonarch = new LinkedList<>();
		Map<LocalDate,List<TherapySessionMonarch>> groupByTherapyDate = therapySessions.stream()
		        .collect(Collectors.groupingBy(TherapySessionMonarch::getDate));
		Iterator<LocalDate> keySetItr = groupByTherapyDate.keySet().iterator();
		while(keySetItr.hasNext()){
			List<TherapySessionMonarch> groupedTherapySessions = groupByTherapyDate.get(keySetItr.next());
			for(int i =0 ; i<groupedTherapySessions.size();i++){
				TherapySessionMonarch therapySession = groupedTherapySessions.get(i);
				therapySession.setSessionNo(i+1);
				updatedTherapySessionsMonarch.add(therapySession);
				
			}
		}
		return updatedTherapySessionsMonarch;
	}

	public static double calculateWeightedAvg(double totalDuration,int durationInMinutes,
			Integer frequency) {
		if(totalDuration == 0) // safety check for divided by 0 exception
			return 0;
		return (durationInMinutes*frequency/totalDuration);
	}
	
	public static int calculateCumulativeDuration(List<TherapySessionMonarch> therapySessions){
		return therapySessions.stream().collect(Collectors.summingInt(TherapySessionMonarch::getDurationInMinutes));
	}
	
	public static int calculateHMRRunRatePerSession(List<TherapySessionMonarch> therapySessions){
		float sessionsCount = therapySessions.isEmpty()?1:therapySessions.size();
		return Math.round(calculateCumulativeDuration(therapySessions)/sessionsCount);
	}
	
	public static int calculateCumulativeDurationVest(List<TherapySession> therapySessions){
		return therapySessions.stream().collect(Collectors.summingInt(TherapySession::getDurationInMinutes));
	}
	
	public static int calculateHMRRunRatePerSessionBoth(List<TherapySessionMonarch> therapySessions,List<TherapySession> therapySessionsVest){
		float sessionsCountMonarch = therapySessions.isEmpty()?0:therapySessions.size();		
		float sessionsCountVest = therapySessionsVest.isEmpty()?0:therapySessions.size();
		
		float sessionsCount = (sessionsCountMonarch+sessionsCountVest) == 0 ? 1 :(sessionsCountMonarch+sessionsCountVest);
		
		return Math.round((calculateCumulativeDuration(therapySessions)+calculateCumulativeDurationVest(therapySessionsVest))
																								/(sessionsCount));
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
