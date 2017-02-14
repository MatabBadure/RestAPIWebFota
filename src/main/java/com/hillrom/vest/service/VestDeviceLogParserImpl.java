package com.hillrom.vest.service;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.AIR_INTERFACE_TYPE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.CRC_FIELD_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.CUC_VERSION;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.CUSTOMER_ID;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.CUSTOMER_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_ADDRESS;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_DATA;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_DATA_FIELD_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_MODEL_TYPE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_SERIAL_NUMBER;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_TYPE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DURATION_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DURATION_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_BATTERY_LEVEL_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_BATTERY_LEVEL_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_TIME_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_TIME_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_CODE_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_CODE_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_LOG_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_LOG_START_POS;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_TIMESTAMP_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_TIMESTAMP_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FREQUENCY_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FREQUENCY_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HMR_SECONDS_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HMR_SECONDS_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HUB_ID;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HUB_RECEIVE_TIME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HUB_RECEIVE_TIME_OFFSET;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.INTENSITY_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.INTENSITY_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_EVENTS_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_EVENTS_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_PODS_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_PODS_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.SESSION_INDEX_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.SESSION_INDEX_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.SP_RECEIVE_TIME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_BATTERY_LEVEL_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_BATTERY_LEVEL_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_TIME_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_TIME_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.TIMEZONE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_VER;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_MODEL;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_SN;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.CHECKSUM_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.CHECKSUM_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.DAY_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.DAY_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.DURATION_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.DURATION_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.EVENT_CODE_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.EVENT_CODE_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.FREQUENCY_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.FREQUENCY_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.HMR_HOUR_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.HMR_HOUR_END_OFFSET1;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.HMR_HOUR_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.HMR_HOUR_START_OFFSET1;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.HMR_MINUTE_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.HMR_MINUTE_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.HOUR_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.HOUR_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.MINUTE_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.MINUTE_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.MONTH_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.MONTH_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.PRESSURE_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.PRESSURE_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.SECOND_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.SECOND_START_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.YEAR_END_OFFSET;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.YEAR_START_OFFSET;
import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.getEventStringByEventCode;
import static com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants.DATA_PACKET_HEADER;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hillrom.vest.batch.processing.PatientVestDeviceDataDeltaReader;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.domain.PatientVestDeviceRawLogMonarch;
import com.hillrom.vest.domain.PingPongPing;
import com.hillrom.vest.service.util.ParserUtil;
import com.hillrom.vest.service.util.monarch.ParserUtilMonarch;

import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.getEventStringByEventCode;
import static com.hillrom.vest.config.VestDeviceRawLogOffsetConstants.INFO_PACKET_HEADER;

@Component
public class VestDeviceLogParserImpl implements DeviceLogParser {

	private final Logger log = LoggerFactory.getLogger(PatientVestDeviceDataDeltaReader.class);
	
	private static final String EXPECTED_STRING = "24454F50F0F0F0F0";
	private static final int RECORD_SIZE = 16;

	@Override
	public PatientVestDeviceRawLog parseBase64StringToPatientVestDeviceRawLog(
			String rawMessage) throws Exception{
		JSONObject qclJsonData = ParserUtil.getQclJsonDataFromRawMessage(rawMessage);
		
		String hub_timestamp = ParserUtil.getValueFromQclJsonData(qclJsonData,HUB_RECEIVE_TIME);
		String sp_timestamp = ParserUtil.getValueFromQclJsonData(qclJsonData,SP_RECEIVE_TIME);

		PatientVestDeviceRawLog patientVestDeviceRawLog = createPatientVestDeviceRawLog(rawMessage,qclJsonData);
		patientVestDeviceRawLog.setDeviceAddress(ParserUtil
				.getValueFromQclJsonData(qclJsonData,DEVICE_ADDRESS));

		try {
			patientVestDeviceRawLog.setHubReceiveTime(Long.parseLong(hub_timestamp));
			patientVestDeviceRawLog.setSpReceiveTime(Long.parseLong(sp_timestamp));
			if(StringUtils.isBlank(patientVestDeviceRawLog.getDeviceAddress()) || 
					StringUtils.isBlank(patientVestDeviceRawLog.getDeviceSerialNumber()) ||
					StringUtils.isBlank(patientVestDeviceRawLog.getDeviceData())){
				throw new IllegalArgumentException(
						"Could not parse data, Bad Content");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Could not parse data, Bad Content");
		}
		log.debug("PatientVestDeviceRawLog : "+patientVestDeviceRawLog);
		return patientVestDeviceRawLog;
	}

	private PatientVestDeviceRawLog createPatientVestDeviceRawLog(
			String rawMessage,JSONObject qclJsonData) {
		PatientVestDeviceRawLog patientVestDeviceRawLog = new PatientVestDeviceRawLog();
		patientVestDeviceRawLog.setRawMessage(rawMessage);
		patientVestDeviceRawLog
				.setAirInterfaceType(ParserUtil
						.getValueFromQclJsonData(qclJsonData, AIR_INTERFACE_TYPE));
		patientVestDeviceRawLog.setCucVersion(ParserUtil.getValueFromQclJsonData(
				qclJsonData, CUC_VERSION));
		patientVestDeviceRawLog.setCustomerId(ParserUtil.getValueFromQclJsonData(
				qclJsonData, CUSTOMER_ID));
		patientVestDeviceRawLog.setCustomerName(ParserUtil
				.getValueFromQclJsonData(qclJsonData, CUSTOMER_NAME));

		patientVestDeviceRawLog.setDeviceData(ParserUtil.getValueFromQclJsonData(
				qclJsonData, DEVICE_DATA));
		patientVestDeviceRawLog
				.setDeviceModelType(ParserUtil
						.getValueFromQclJsonData(
								qclJsonData,DEVICE_MODEL_TYPE));
		patientVestDeviceRawLog
				.setDeviceSerialNumber(ParserUtil
						.getValueFromQclJsonData(
								qclJsonData,DEVICE_SERIAL_NUMBER));
		patientVestDeviceRawLog.setDeviceType(ParserUtil.getValueFromQclJsonData(
				qclJsonData,DEVICE_TYPE));
		patientVestDeviceRawLog.setHubId(ParserUtil.getValueFromQclJsonData(
				qclJsonData, HUB_ID));
		patientVestDeviceRawLog.setTimezone(ParserUtil.getValueFromQclJsonData(
				qclJsonData, TIMEZONE));
		patientVestDeviceRawLog
				.setHubReceiveTimeOffset(ParserUtil
						.getValueFromQclJsonData(
								qclJsonData,
								 HUB_RECEIVE_TIME_OFFSET));
		
		return patientVestDeviceRawLog;
	}

	@Override
	public List<PatientVestDeviceData> parseBase64StringToPatientVestDeviceLogEntry(
			String base64String) throws Exception{

		List<PatientVestDeviceData> patientVestDeviceRecords = new LinkedList<>();
		String base16String = ParserUtil.convertToBase16String(base64String);
		
		log.debug("Base16 String "+base16String);
		
		if (base16String.length() <= 16)
			return patientVestDeviceRecords;

		// To validate bad data
		String endTags = base16String.substring(base16String.length() - 16);
		
		log.debug("end tags for the message "+endTags);
		
		if (!EXPECTED_STRING.equals(endTags.toUpperCase().trim()))
			throw new IllegalArgumentException(
					"Could not parse data, Request contains Partial Data");

		int logcount = 1;
		int start;
		int end;
		start = 32 * 2 + (logcount - 1) * RECORD_SIZE * 2;
		end = 32 * 2 + (logcount * RECORD_SIZE * 2);
		while (start < base16String.length() & (end < base16String.length())) {
			String log_segment = base16String.substring(start, end);
			
			log.debug("Data packet : "+log_segment);
			if(log_segment.startsWith(DATA_PACKET_HEADER)){
				PatientVestDeviceData patientVestDeviceRecord = getPatientVestDeviceData(
						log_segment, logcount);
				if(!patientVestDeviceRecord.getEventId().startsWith("0"))
					patientVestDeviceRecords.add(patientVestDeviceRecord);
			}
			
			logcount++;
			start = 32 * 2 + (logcount - 1) * RECORD_SIZE * 2;
			end = 32 * 2 + (logcount * RECORD_SIZE * 2);
		}
		return patientVestDeviceRecords;
	}

	private PatientVestDeviceData getPatientVestDeviceData(String base16String,
			int sequenceNumber) {
		PatientVestDeviceData patientVestDeviceData = new PatientVestDeviceData();
		patientVestDeviceData.setSequenceNumber(sequenceNumber);
		patientVestDeviceData.setHmr(getPatientVestDeviceDataHMR(base16String));
		patientVestDeviceData
				.setPressure(getPatientVestDeviceDataPressure(base16String));
		patientVestDeviceData
				.setFrequency(getPatientVestDeviceDataFrequency(base16String));
		patientVestDeviceData
				.setDuration(getPatientVestDeviceDataDuration(base16String));
		patientVestDeviceData
				.setEventId(getPatientVestDeviceEventCode(base16String));
		patientVestDeviceData.setTimestamp(getPatientVestDeviceDataTimeStamp(
				base16String)*1000);
		patientVestDeviceData.setChecksum(getPatientVestDeviceDataChecksum(base16String));
		return patientVestDeviceData;
	}
	
	private Integer getPatientVestDeviceDataDuration(String base16String) {
		String durationReadingString = ParserUtil.getFieldByStartAndEndOffset(
				base16String, DURATION_START_OFFSET, DURATION_END_OFFSET);
		return ParserUtil.convertHexStringToInteger(durationReadingString);
	}

	private Integer getPatientVestDeviceDataFrequency(String base16String) {
		String frequencyReadingString = ParserUtil.getFieldByStartAndEndOffset(
				base16String, FREQUENCY_START_OFFSET, FREQUENCY_END_OFFSET);
		return ParserUtil.convertHexStringToInteger(frequencyReadingString);
	}

	private Integer getPatientVestDeviceDataPressure(String base16String) {
		String pressureReadingString = ParserUtil.getFieldByStartAndEndOffset(
				base16String, PRESSURE_START_OFFSET, PRESSURE_END_OFFSET);
		return ParserUtil.convertHexStringToInteger(pressureReadingString);
	}

	private Double getPatientVestDeviceDataHMR(String base16String) {
		String hmrHourReadingString = ParserUtil.getFieldByStartAndEndOffset(
				base16String, HMR_HOUR_START_OFFSET, HMR_HOUR_END_OFFSET);
		hmrHourReadingString = hmrHourReadingString
				.concat(ParserUtil
						.getFieldByStartAndEndOffset(
								base16String, HMR_HOUR_START_OFFSET1,
								HMR_HOUR_END_OFFSET1));
		Long hmrHourReadingLong = ParserUtil
				.convertHexStringToLong(hmrHourReadingString);
		String hmrMinutesReadingString = ParserUtil
				.getFieldByStartAndEndOffset(
						base16String, HMR_MINUTE_START_OFFSET, HMR_MINUTE_END_OFFSET);
		Long hmrMinutesReadingLong = ParserUtil
				.convertHexStringToLong(hmrMinutesReadingString);
		return (double) (hmrHourReadingLong * 60 * 60 + hmrMinutesReadingLong * 60);
	}

	private String getPatientVestDeviceEventCode(String base16String) {
		String eventCodeString = ParserUtil.getFieldByStartAndEndOffset(
				base16String, EVENT_CODE_START_OFFSET, EVENT_CODE_END_OFFSET);
		String eventCode = getEventStringByEventCode(ParserUtil
				.convertHexStringToInteger(eventCodeString));
		return eventCode;
	}

	private long getPatientVestDeviceDataTimeStamp(String base16String) {
		String yearString = ParserUtil.getFieldByStartAndEndOffset(
				base16String, YEAR_START_OFFSET, YEAR_END_OFFSET);
		int year = 2000 + ParserUtil.convertHexStringToInteger(yearString);
		String monthString = ParserUtil.getFieldByStartAndEndOffset(
				base16String, MONTH_START_OFFSET, MONTH_END_OFFSET);
		int month = ParserUtil.convertHexStringToInteger(monthString);
		String dayString = ParserUtil.getFieldByStartAndEndOffset(base16String,
				DAY_START_OFFSET,DAY_END_OFFSET);
		int day = ParserUtil.convertHexStringToInteger(dayString);
		String hourString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,HOUR_START_OFFSET,HOUR_END_OFFSET);
		int hour = ParserUtil.convertHexStringToInteger(hourString);
		String minuteString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,MINUTE_START_OFFSET,MINUTE_END_OFFSET);
		int minute = ParserUtil.convertHexStringToInteger(minuteString);
		String secondString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,SECOND_START_OFFSET,SECOND_END_OFFSET);
		int second = ParserUtil.convertHexStringToInteger(secondString);
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month-1, day, hour, minute, second);
		return calendar.getTimeInMillis()/1000;
	}

	private int getPatientVestDeviceDataChecksum(String base16String){
		String checksumString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,CHECKSUM_START_OFFSET,CHECKSUM_END_OFFSET);
		return ParserUtil.convertHexStringToInteger(checksumString);
	}

	private PatientVestDeviceRawLogMonarch createPatientVestDeviceRawLogMonarch(
			String rawMessageMonarch,JSONObject qclJsonDataMonarch)  throws Exception{
		PatientVestDeviceRawLogMonarch patientVestDeviceRawLogMonarch = new PatientVestDeviceRawLogMonarch();
		patientVestDeviceRawLogMonarch.setRawMessage(rawMessageMonarch);

		patientVestDeviceRawLogMonarch.setCucVersion(ParserUtilMonarch.getValueFromQclJsonDataMonarch(
				qclJsonDataMonarch, DEVICE_VER));


		patientVestDeviceRawLogMonarch.setDeviceData(ParserUtilMonarch.getMonarchDeviceData(rawMessageMonarch));

		patientVestDeviceRawLogMonarch
				.setDeviceModelType(ParserUtilMonarch
						.getValueFromQclJsonDataMonarch(
								qclJsonDataMonarch,DEVICE_MODEL));
		patientVestDeviceRawLogMonarch
				.setDeviceSerialNumber(ParserUtilMonarch
						.getValueFromQclJsonDataMonarch(
								qclJsonDataMonarch,DEVICE_SN));
		
		patientVestDeviceRawLogMonarch.setTotalFragments(ParserUtilMonarch.getFragTotal(rawMessageMonarch)+"");
		patientVestDeviceRawLogMonarch.setCurrentFragment(ParserUtilMonarch.getFragCurrent(rawMessageMonarch)+"");
		patientVestDeviceRawLogMonarch.setChecksum(ParserUtilMonarch.getCRCChecksum(rawMessageMonarch)+"");
		
		return patientVestDeviceRawLogMonarch;
	}
	
	public String decodeData(final String rawMessage){
		byte[] decoded = java.util.Base64.getDecoder().decode(rawMessage);
		
        String sout = "";
        for(int i=0;i<decoded.length;i++) {
        	int val = decoded[i] & 0xFF;
        	sout = sout + val + " ";
        }
        
        log.debug("Input Byte Array :"+sout);

		String decoded_string = new String(decoded);
		log.error("Decoded value is " + decoded_string);
		return decoded_string;
	}
	
	@Override
	public PatientVestDeviceRawLogMonarch parseBase64StringToPatientMonarchDeviceRawLog(
			String rawMessageMonarch) throws Exception{
		
		String decodedString = decodeData(rawMessageMonarch);
		
		JSONObject qclJsonDataMonarch = ParserUtil.getChargerQclJsonDataFromRawMessage(decodedString);
		
		/*String hub_timestamp = ParserUtil.getValueFromQclJsonData(qclJsonDataMonarch,HUB_RECEIVE_TIME);
		String sp_timestamp = ParserUtil.getValueFromQclJsonData(qclJsonDataMonarch,SP_RECEIVE_TIME);*/

		PatientVestDeviceRawLogMonarch patientVestDeviceRawLogMonarch = createPatientVestDeviceRawLogMonarch(rawMessageMonarch,qclJsonDataMonarch);
		
		patientVestDeviceRawLogMonarch.setDeviceAddress("Hardcoded_Dev_address");

		try {
			/*patientVestDeviceRawLog.setHubReceiveTime(Long.parseLong(hub_timestamp));
			patientVestDeviceRawLog.setSpReceiveTime(Long.parseLong(sp_timestamp));*/
			
			/*if(StringUtils.isBlank(patientVestDeviceRawLogMonarch.getDeviceAddress()) || 
					StringUtils.isBlank(patientVestDeviceRawLogMonarch.getDeviceSerialNumber()) ||
					StringUtils.isBlank(patientVestDeviceRawLogMonarch.getDeviceData())){*/
				
			
			if(StringUtils.isBlank(patientVestDeviceRawLogMonarch.getDeviceSerialNumber()) ||
						StringUtils.isBlank(patientVestDeviceRawLogMonarch.getDeviceData())){	
				throw new IllegalArgumentException(
						"Could not parse data, Bad Content");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Could not parse data, Bad Content");
		}
		log.debug("PatientVestDeviceRawLog : "+patientVestDeviceRawLogMonarch);
		return patientVestDeviceRawLogMonarch;
	}
	
	

	@Override
	public List<PatientVestDeviceDataMonarch> parseBase64StringToPatientMonarchDeviceLogEntry(
			String base64String) throws Exception {
		
		List <PatientVestDeviceDataMonarch> monarchDeviceData = new LinkedList<>();
		
        byte[] b = java.util.Base64.getDecoder().decode(base64String);
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
        log.debug("Input Byte Array :"+sout);

        String deviceData = "";
        int start = ParserUtilMonarch.returnMatch(b,DEVICE_DATA_FIELD_NAME);
        int end = ParserUtilMonarch.returnMatch(b,CRC_FIELD_NAME)-CRC_FIELD_NAME.length;
        log.debug("start end : "+ start + " : " + end );
        
        byte[] deviceDataArray = new byte[end];
        int j=0;
        for(int i=start;i<end;i++) {
        	deviceDataArray[j++] = b[i];
        	int val = b[i] & 0xFF;
        	deviceData = deviceData + String.valueOf(Character.toChars(val));
        }
        log.debug("deviceData : "+ sout );
        /*
        if(deviceData.equalsIgnoreCase("PING_PONG_PING")){        	
    			log.debug("deviceData is PING_PONG_PING" + " Insert into PING_PONG_PING table");
    			PingPongPing pingPongPingData = new PingPongPing();
    			pingPongPingData.setCreatedTime(new DateTime());
    			
    			pingPongPingRepository.save(pingPongPingData);    			
    		}
        }*/
        
		int x = ParserUtilMonarch.getFragTotal(base64String);
		int y = ParserUtilMonarch.getFragCurrent(base64String);
		
		byte[] devsnbt = ParserUtilMonarch.getDevSN(base64String);
		byte[] wifibt = ParserUtilMonarch.getDevWifi(base64String);
		byte[] verbt = ParserUtilMonarch.getDevVer(base64String);
        
		String deviceSerNo = new String(devsnbt);
		//String wifiSerNo = new String(wifibt);
		//String deviceVer = new String(verbt);
				
		
        byte[] session_index  = Arrays.copyOfRange(deviceDataArray, SESSION_INDEX_LOC, SESSION_INDEX_LOC + SESSION_INDEX_LEN);
        sout = "";
        
        for(int k=0;k<session_index.length;k++){
        	sout = sout + (session_index[k]  & 0xFF) + " ";
        }
        log.debug("session_index : "+ sout );
        //String sessionIndexVal = new String(session_index);
        //String sessionIndexVal =  sout;
        
        log.debug("Combined session_index : "+ ParserUtilMonarch.intergerCombinedFromHex(session_index));
              
        byte[] start_time  = Arrays.copyOfRange(deviceDataArray, START_TIME_LOC, START_TIME_LOC + START_TIME_LEN);
        sout = "";
        for(int k=0;k<start_time.length;k++){
        	sout = sout + (start_time[k]  & 0xFF) + " ";
        }
        log.debug("start_time : "+ sout );
        
        int start_date =  start_time[2];
        int start_month =  start_time[1];
        int start_year =  start_time[0];
        int start_hour =  start_time[3];
        int start_minute =  start_time[4];
        int start_second =  start_time[5];
        
        byte[] end_time  = Arrays.copyOfRange(deviceDataArray, END_TIME_LOC, END_TIME_LOC + END_TIME_LEN);        
        sout = "";
        for(int k=0;k<end_time.length;k++){
        	sout = sout + (end_time[k]  & 0xFF) + " ";
        }
        log.debug("end_time : "+ sout );
        
        int end_date =  end_time[0];
        int end_month =  end_time[1];
        int end_year =  end_time[2];
        int end_hour =  end_time[3];
        int end_minute =  end_time[4];
        int end_second =  end_time[5];
        
        int startEndVary = 0;
        if(start_date != end_date){
        	startEndVary = 1;	
        }
        
        byte[] start_battery_level  = Arrays.copyOfRange(deviceDataArray, START_BATTERY_LEVEL_LOC, START_BATTERY_LEVEL_LOC + START_BATTERY_LEVEL_LEN);
        sout = "";
        for(int k=0;k<start_battery_level.length;k++){
        	sout = sout + (start_battery_level[k]  & 0xFF) + " ";
        }
        log.debug("start_battery_level : "+ sout );
        
        byte[] end_battery_level  = Arrays.copyOfRange(deviceDataArray, END_BATTERY_LEVEL_LOC, END_BATTERY_LEVEL_LOC + END_BATTERY_LEVEL_LEN);
        sout = "";
        for(int k=0;k<end_battery_level.length;k++){
        	sout = sout + (end_battery_level[k]  & 0xFF) + " ";
        }
        log.debug("end_battery_level : "+ sout );
        
        byte[] number_of_events  = Arrays.copyOfRange(deviceDataArray, NUMBER_OF_EVENTS_LOC, NUMBER_OF_EVENTS_LOC + NUMBER_OF_EVENTS_LEN);
        sout = "";
        for(int k=0;k<number_of_events.length;k++){
        	sout = sout + (number_of_events[k]  & 0xFF) + " ";
        }
        log.debug("number_of_events : "+ sout );
        
        byte[] number_of_pods  = Arrays.copyOfRange(deviceDataArray, NUMBER_OF_PODS_LOC, NUMBER_OF_PODS_LOC + NUMBER_OF_PODS_LEN);
        sout = "";
        for(int k=0;k<number_of_pods.length;k++){
        	sout = sout + (number_of_pods[k]  & 0xFF) + " ";
        }
        log.debug("number_of_pods : "+ sout );
        
        byte[] hmr_seconds  = Arrays.copyOfRange(deviceDataArray, HMR_SECONDS_LOC, HMR_SECONDS_LOC + HMR_SECONDS_LEN);
        sout = "";
        for(int k=0;k<hmr_seconds.length;k++){
        	sout = sout + (hmr_seconds[k]  & 0xFF) + " ";
        }        
        int combinedHmr = ParserUtilMonarch.intergerCombinedFromHex(hmr_seconds);        
        double hmrSeconds = (double)combinedHmr;
        
        //log.debug("Value of deviceDataArray.length : "+ j );
        for(int i=EVENT_LOG_START_POS+1;i<j;i=i+EVENT_LOG_LEN){
        	
        	//log.debug("Value of i : "+ i );
        	
	        byte[] event_timestamp  = Arrays.copyOfRange(deviceDataArray, i + EVENT_TIMESTAMP_LOC-1, (i+EVENT_TIMESTAMP_LOC-1) + EVENT_TIMESTAMP_LEN);
	        sout = "";
	        for(int k=0;k<event_timestamp.length;k++){
	        	sout = sout + (event_timestamp[k]  & 0xFF) + " ";
	        }
	        String eventTimestamp = sout;	        
	        
	        byte[] event_code  = Arrays.copyOfRange(deviceDataArray, i+EVENT_CODE_LOC-1, (i+EVENT_CODE_LOC-1) + EVENT_CODE_LEN);        
	        sout = "";
	        for(int k=0;k<event_code.length;k++){
	        	sout = sout + (event_code[k]  & 0xFF);
	        }
	        String eventCode = sout;
	        
	        byte[] frequency  = Arrays.copyOfRange(deviceDataArray, i+FREQUENCY_LOC-1, (i+FREQUENCY_LOC-1) + FREQUENCY_LEN);
	        sout = "";
	        for(int k=0;k<frequency.length;k++){
	        	sout = sout + (frequency[k]  & 0xFF);
	        }
	        String freqValue = sout;

	        
	        byte[] intensity  = Arrays.copyOfRange(deviceDataArray, i+INTENSITY_LOC-1, (i+INTENSITY_LOC-1) + INTENSITY_LEN);
	        sout = "";
	        for(int k=0;k<intensity.length;k++){
	        	sout = sout + (intensity[k]  & 0xFF);
	        }
	        String intensityVal = sout;

	        
	        byte[] duration  = Arrays.copyOfRange(deviceDataArray, i+DURATION_LOC-1, (i+DURATION_LOC-1) + DURATION_LEN);
	        sout = "";
	        for(int k=0;k<duration.length;k++){
	        	sout = sout + (duration[k]  & 0xFF);
	        }
	        String durationVal = sout;
	        
	        PatientVestDeviceDataMonarch monarchDeviceDataVal = new PatientVestDeviceDataMonarch();
	        
	        
	       /* SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");	        
	        Date lFromDate1 = null;
			try {
				lFromDate1 = datetimeFormatter1.parse(new String(event_timestamp));
			} catch (ParseException e) {
				e.printStackTrace();
			}        
			Long eventTS = lFromDate1.getTime();
			

	        
	        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());*/
	        
	        int eventHour = event_timestamp[0];
	        int eventMin = event_timestamp[1];
	        int eventSec = event_timestamp[2];
	        
	        String dateValue = "20"+start_year+"-"+start_month+"-"+start_date+" "+eventHour+":"+eventMin+":"+eventSec+".0";
	        
	        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(dateValue);
	        long tsTime2 = timestamp.getTime();
	        
	        /*DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        Date date = null;
			try {
				date = dateFormat.parse("23/09/2007 15:30:30");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        long time = date.getTime();	        */
	        
	        /*java.util.Date today = new java.util.Date();
	        java.sql.Timestamp ts1 = new java.sql.Timestamp(today.getTime());
	        long tsTime1 = ts1.getTime();
	        */
			
	        monarchDeviceDataVal.setTimestamp(tsTime2);
	        // todo : hardcoded for temporary
	        monarchDeviceDataVal.setSequenceNumber(1); 
	        monarchDeviceDataVal.setEventCode(eventCode);
	        monarchDeviceDataVal.setSerialNumber(deviceSerNo);
	        monarchDeviceDataVal.setHmr(hmrSeconds);
	        monarchDeviceDataVal.setFrequency(Integer.parseInt(freqValue));
	        monarchDeviceDataVal.setIntensity(Integer.parseInt(intensityVal));
	        monarchDeviceDataVal.setDuration(Integer.parseInt(durationVal));
	        
	        monarchDeviceData.add(monarchDeviceDataVal);
	        
        }
        
        return monarchDeviceData;
	}
}
