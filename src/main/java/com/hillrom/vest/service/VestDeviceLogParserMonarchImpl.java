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
import java.util.Objects;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hillrom.vest.batch.processing.PatientVestDeviceDataDeltaReader;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.domain.PatientVestDeviceRawLogMonarch;
import com.hillrom.vest.domain.PingPongPing;
import com.hillrom.vest.repository.PingPongPingRepository;
import com.hillrom.vest.service.util.ParserUtil;
import com.hillrom.vest.service.util.monarch.ParserUtilMonarch;

import static com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil.getEventStringByEventCode;
import static com.hillrom.vest.config.VestDeviceRawLogOffsetConstants.INFO_PACKET_HEADER;

import javax.inject.Inject;
@Component
public class VestDeviceLogParserMonarchImpl implements DeviceLogMonarchParser {

	@Inject
	private PingPongPingRepository pingpongpingrepository;
	private final Logger log = LoggerFactory.getLogger(PatientVestDeviceDataDeltaReader.class);
	
	private PatientVestDeviceRawLogMonarch createPatientVestDeviceRawLogMonarch(
			String rawMessageMonarch,JSONObject qclJsonDataMonarch)  throws Exception{
		PatientVestDeviceRawLogMonarch patientVestDeviceRawLogMonarch = new PatientVestDeviceRawLogMonarch();
		patientVestDeviceRawLogMonarch.setRawMessage(rawMessageMonarch);

		// TO BE ELEMINATED : CUC version no longer used in Monarch
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
		patientVestDeviceRawLogMonarch.setDeviceAddress(ParserUtilMonarch.getDevWifiOrLteString(rawMessageMonarch,1) == null ? 
															ParserUtilMonarch.getDevWifiOrLteString(rawMessageMonarch,2) : 
																ParserUtilMonarch.getDevWifiOrLteString(rawMessageMonarch,1) );
		
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
		
		JSONObject jsonDataMonarch = ParserUtil.getChargerJsonDataFromRawMessage(decodedString);
		
		/*String hub_timestamp = ParserUtil.getValueFromQclJsonData(qclJsonDataMonarch,HUB_RECEIVE_TIME);
		String sp_timestamp = ParserUtil.getValueFromQclJsonData(qclJsonDataMonarch,SP_RECEIVE_TIME);*/

		PatientVestDeviceRawLogMonarch patientVestDeviceRawLogMonarch = createPatientVestDeviceRawLogMonarch(rawMessageMonarch,jsonDataMonarch);
		
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
	
	
	private String injectPingPongData(String deviceData,String slNo,String wifiId,String lteId) {		
		try{
			if(deviceData.equals("PING_PONG_PING")) {
				log.debug("deviceData is PING_PONG_PING" + " Insert into PING_PONG_PING table");
				PingPongPing pingPongPingData = new PingPongPing();
				pingPongPingData.setCreatedTime(new DateTime());				
				if(Objects.isNull(lteId)){
					pingPongPingData.setDevWifi(wifiId);	
				}else if(Objects.isNull(wifiId)){
					pingPongPingData.setDevLte(lteId);
				}
				pingPongPingData.setSerialNumber(slNo);
				pingpongpingrepository.save(pingPongPingData);
				return "OK";
			}
		}catch(Exception e){
			e.printStackTrace();			
		}	
		return null;
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
        
		int fragTotal = ParserUtilMonarch.getFragTotal(base64String);
		int fragCurr = ParserUtilMonarch.getFragCurrent(base64String);
		
		byte[] devsnbt = ParserUtilMonarch.getDevSN(base64String);
		
		// TO BE ELIMINATED : Not used in monarch
		//byte[] wifibt = ParserUtilMonarch.getDevWifi(base64String);
		//byte[] verbt = ParserUtilMonarch.getDevVer(base64String);
        
		String deviceSerNo = new String(devsnbt);
		
		// Flag 1 for WIFI
		String wifiSerNo = ParserUtilMonarch.getDevWifiOrLteString(base64String, 1);
		
		//String lteSerNo = null;
		// Flag 2 for LTE
		String lteSerNo = ParserUtilMonarch.getDevWifiOrLteString(base64String, 2);
		
		String deviceVer = ParserUtilMonarch.getDevVerString(base64String);
		
		if(Objects.nonNull(injectPingPongData(deviceData, deviceSerNo, wifiSerNo, lteSerNo))){
			return monarchDeviceData;
		}
		
        byte[] session_index  = Arrays.copyOfRange(deviceDataArray, SESSION_INDEX_LOC, SESSION_INDEX_LOC + SESSION_INDEX_LEN);
        sout = "";
        
        for(int k=0;k<session_index.length;k++){
        	sout = sout + (session_index[k]  & 0xFF) + " ";
        }
        log.debug("session_index : "+ sout );
        //String sessionIndexVal = new String(session_index);
        //String sessionIndexVal =  sout;
        
        log.debug("Combined session_index : "+ ParserUtilMonarch.intergerCombinedFromHex(session_index));
        Integer sessionIndexVal =  ParserUtilMonarch.intergerCombinedFromHex(session_index);
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

        // TO BE ELIMINATED - If not required
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
        	sout = sout + (start_battery_level[k]  & 0xFF) ;
        }
        log.debug("start_battery_level : "+ sout );
        String startBatteryLevel = sout;
        
        byte[] end_battery_level  = Arrays.copyOfRange(deviceDataArray, END_BATTERY_LEVEL_LOC, END_BATTERY_LEVEL_LOC + END_BATTERY_LEVEL_LEN);
        sout = "";
        for(int k=0;k<end_battery_level.length;k++){
        	sout = sout + (end_battery_level[k]  & 0xFF) ;
        }
        log.debug("end_battery_level : "+ sout );
        String endBatteryLevel = sout;
        
        byte[] number_of_events  = Arrays.copyOfRange(deviceDataArray, NUMBER_OF_EVENTS_LOC, NUMBER_OF_EVENTS_LOC + NUMBER_OF_EVENTS_LEN);
        sout = "";
        for(int k=0;k<number_of_events.length;k++){
        	sout = sout + (number_of_events[k]  & 0xFF) ;
        }
        log.debug("number_of_events : "+ sout );
        String numOfEvents = sout;
        
        byte[] number_of_pods  = Arrays.copyOfRange(deviceDataArray, NUMBER_OF_PODS_LOC, NUMBER_OF_PODS_LOC + NUMBER_OF_PODS_LEN);
        sout = "";
        for(int k=0;k<number_of_pods.length;k++){
        	sout = sout + (number_of_pods[k]  & 0xFF) ;
        }
        log.debug("number_of_pods : "+ sout );
        String numOfPods = sout;
        
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
	        
	        // TO BE ELIMINATED
	        //String eventTimestamp = sout;	        
	        
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
	        
	        int eventHour = event_timestamp[0];
	        int eventMin = event_timestamp[1];
	        int eventSec = event_timestamp[2];
	        
	        String dateValue = "20"+start_year+"-"+start_month+"-"+start_date+" "+eventHour+":"+eventMin+":"+eventSec+".0";
	        
	        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(dateValue);
	        long tsTime2 = timestamp.getTime();
			
	        monarchDeviceDataVal.setTimestamp(tsTime2);
	        // TO BE ELIMINATED : Not used in monarch todo : hardcoded for temporary
	        monarchDeviceDataVal.setSequenceNumber(1);
	        monarchDeviceDataVal.setEventCode(eventCode);
	        monarchDeviceDataVal.setSerialNumber(deviceSerNo);
	        monarchDeviceDataVal.setHmr(hmrSeconds);
	        monarchDeviceDataVal.setFrequency(Integer.parseInt(freqValue));
	        monarchDeviceDataVal.setIntensity(Integer.parseInt(intensityVal));
	        monarchDeviceDataVal.setDuration(Integer.parseInt(durationVal));
	        
	        // TO BE ELIMINATED : Bluetooth Id needs to be deleted from Monarch table. which is not applicable in Monarch
	        monarchDeviceDataVal.setBluetoothId("Dummy_bluetooth_id");
	        
			monarchDeviceDataVal.setFragTotal(fragTotal);			
	        monarchDeviceDataVal.setFragCurrent(fragCurr);
	        
	        monarchDeviceDataVal.setTherapyIndex(sessionIndexVal);	        
	        monarchDeviceDataVal.setStartBatteryLevel(Integer.parseInt(startBatteryLevel));
	        monarchDeviceDataVal.setEndBatteryLevel(Integer.parseInt(endBatteryLevel));
	        monarchDeviceDataVal.setNumberOfEvents(Integer.parseInt(numOfEvents));
	        monarchDeviceDataVal.setNumberOfPods(Integer.parseInt(numOfPods));
	        monarchDeviceDataVal.setDevWifi(wifiSerNo);
	        monarchDeviceDataVal.setDevVersion(deviceVer);
	        
	        monarchDeviceData.add(monarchDeviceDataVal);
	        
        }
        
        return monarchDeviceData;
	}
}
