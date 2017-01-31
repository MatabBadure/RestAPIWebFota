package com.hillrom.vest.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import javax.inject.Inject;
import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.ChargerData;
import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PingPongPing;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ChargerDataRepository;
import com.hillrom.vest.repository.NoteRepository;
import com.hillrom.vest.repository.PingPongPingRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.service.util.ParserUtil;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_SN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_WIFI;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HUB_ID;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HUB_RECEIVE_TIME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.TWO_NET_PROPERTIES;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_LTE;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_SERIAL_NUMBER;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_VER;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.CRC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_ADDRESS;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_DATA;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_MODEL;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FRAG_TOTAL;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FRAG_CURRENT;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.SESSION_INDEX_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_TIME_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_TIME_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_BATTERY_LEVEL_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_BATTERY_LEVEL_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_EVENTS_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_PODS_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HMR_SECONDS_LOC;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_TIMESTAMP_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_CODE_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FREQUENCY_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.INTENSITY_LOC;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DURATION_LOC;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.SESSION_INDEX_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_TIME_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_TIME_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.START_BATTERY_LEVEL_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.END_BATTERY_LEVEL_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_EVENTS_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.NUMBER_OF_PODS_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HMR_SECONDS_LEN;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_LOG_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_TIMESTAMP_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_CODE_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FREQUENCY_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.INTENSITY_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DURATION_LEN;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_LOG_START_POS;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.CRC_FIELD_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_DATA_FIELD_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FRAG_TOTAL_FIELD_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FRAG_CURRENT_FIELD_NAME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEV_WIFI;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEV_SN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEV_VER;

@Service
@Transactional
public class ChargerDataService {


			private final Logger log = LoggerFactory.getLogger(ChargerDataService.class);
		
			@Inject
			private ChargerDataRepository chargerDataRepository;

			@Inject
			private PingPongPingRepository pingPongPingRepository;
			
			
			
			public ChargerData findLatestData(){
				ChargerData chargerData =  chargerDataRepository.findLatestData();
					return chargerData;
			}
		
			public ChargerData findById(Long id){
				ChargerData chargerData =  chargerDataRepository.findById(id);
					return chargerData;
			}
		
			public Page<ChargerData> findAll(Pageable pageable){
				Page<ChargerData> chargerDataList =  chargerDataRepository.findAll(pageable);
					return chargerDataList;
			}
	
	


	
			public JSONObject saveOrUpdateChargerData(String encoded_string, String decoded_string,BufferedWriter bwTherapy,BufferedWriter bwDevice) throws HillromException{	
				
				
				log.error("Encoded String : " + encoded_string);
				log.error("Decoded String : " + decoded_string);
				
				JSONObject chargerJsonData = validateRequest(encoded_string,decoded_string,bwTherapy,bwDevice);
				if(chargerJsonData.get("DEVICE_DATA").equals("PING_PONG_PING")){
					log.debug("deviceData is PING_PONG_PING" + " Insert into PING_PONG_PING table");
					//PingPongPing pingPongPingData = new PingPongPing();
					//pingPongPingData.setCreatedTime(new DateTime());
					//pingPongPingRepository.save(pingPongPingData);
					
				}
				if(chargerJsonData.get("RESULT").equals("OK")){
					log.debug("deviceData is CHARGER DATA "); 
					//ChargerData chargerData = new ChargerData();
					//chargerData.setDeviceData(encoded_string);
					//chargerData.setCreatedTime(new DateTime());
					//chargerDataRepository.save(chargerData);
				}
				return chargerJsonData;
		
			}
	

	
			private JSONObject validateRequest(String rawData,String decoded_data,BufferedWriter bwTherapy,BufferedWriter bwDevice) throws HillromException {
				log.error("Inside validateRequest " + rawData);
				JSONObject chargerJsonData = ParserUtil.getChargerQclJsonDataFromRawMessage(decoded_data);
				String reqParams[] = new String[]{DEVICE_MODEL,DEVICE_SN,
						DEVICE_WIFI,DEVICE_LTE,DEVICE_VER,FRAG_TOTAL,FRAG_CURRENT,DEVICE_DATA,CRC};
				
				
				if(Objects.isNull(chargerJsonData) || chargerJsonData.keySet().isEmpty()){
					throw new HillromException("Missing Params : "+String.join(",",reqParams));
				}else if(Objects.nonNull(chargerJsonData)){
					List<String> missingParams = RandomUtil.getDifference(Arrays.asList(reqParams), new ArrayList<String>(chargerJsonData.keySet()));
		
					if(missingParams.size() > 0){
						if(missingParams.contains(DEVICE_MODEL) || missingParams.contains(DEVICE_SN) || (missingParams.contains(DEVICE_WIFI) && missingParams.contains(DEVICE_LTE)) ||
								missingParams.contains(DEVICE_VER) || missingParams.contains(DEVICE_DATA) || missingParams.contains(CRC) ||
								missingParams.contains(FRAG_TOTAL) || missingParams.contains(FRAG_CURRENT)
								){
							//chargerJsonData.put("DEVICE_DATA", getDeviceData(rawData,bw));
							chargerJsonData.put("RESULT", "NOT OK");
							chargerJsonData.put("ERROR","Missing Params");
							return chargerJsonData;
						}else{
							if(!calculateCRC(rawData)){
								//chargerJsonData.put("DEVICE_DATA", getDeviceData(rawData,bw));
								chargerJsonData.put("RESULT", "NOT OK");
								chargerJsonData.put("ERROR","CRC Validation Failed");
								return chargerJsonData;
							}else{
								chargerJsonData.put("DEVICE_DATA", getDeviceData(rawData,bwTherapy,bwDevice));
								chargerJsonData.put("RESULT", "OK");
								chargerJsonData.put("ERROR","");
								return chargerJsonData;					
							}
						}
					}else{
						if(!calculateCRC(rawData)){
							chargerJsonData.put("RESULT", "NOT OK");
							chargerJsonData.put("ERROR","CRC Validation Failed");
							return chargerJsonData;
						}else{
							chargerJsonData.put("DEVICE_DATA", getDeviceData(rawData,bwTherapy,bwDevice));
							chargerJsonData.put("RESULT", "OK");
							chargerJsonData.put("ERROR","");
							return chargerJsonData;					
						}
					}
				}
				
				return chargerJsonData;
			}
	



	

			  private boolean calculateCRC(String base64String)
			  {
		 
				log.error("Inside  calculateCRC : " ,base64String);
				  
			    int nCheckSum = 0;
		
			    byte[] decoded = java.util.Base64.getDecoder().decode(base64String);
			    
			    int nDecodeCount = 0;
			    for ( ; nDecodeCount < (decoded.length-2); nDecodeCount++ )
			    {
			      int nValue = (decoded[nDecodeCount] & 0xFF);
			      nCheckSum += nValue;
			    }
			    
			    
			    System.out.format("Inverted Value = %d [0X%x] \r\n" ,nCheckSum,nCheckSum);
			    
			    while ( nCheckSum >  65535 )
			    {
			      nCheckSum -= 65535;
			    }
			    
			    int nMSB = decoded[nDecodeCount+1] & 0xFF;
			    int nLSB = decoded[nDecodeCount] & 0xFF;
			    
			    System.out.format("MSB = %d [0x%x]\r\n" ,nMSB, nMSB);
			    System.out.format("LSB = %d [0x%x]\r\n" ,nLSB, nLSB);
			    log.error("Total Value = " + nCheckSum);
			    nCheckSum = ((~nCheckSum)& 0xFFFF) + 1;
			    System.out.format("Checksum Value = %d [0X%x] \r\n" ,nCheckSum,nCheckSum);
			    
			    String msb_digit = Integer.toHexString(nMSB);
			    String lsb_digit = Integer.toHexString(nLSB);
			    String checksum_num =  Integer.toHexString(nCheckSum);
			    
			    if(msb_digit.length()<2)
			    	msb_digit = "0"+msb_digit;
			    if(lsb_digit.length()<2)
			    	lsb_digit = "0"+lsb_digit;
			    
			    System.out.println("MSB : " + msb_digit + " " +  "LSB : " + lsb_digit);
			    System.out.println("Checksum : " + checksum_num);
			    
			    if((msb_digit+lsb_digit).equalsIgnoreCase(checksum_num)){
			    	return true;
			    }else{
			    	log.error("CRC VALIDATION FAILED :"); 
			    	return false;
			    }
		
				
			}
	  
			public String getDeviceData(String encoded_string,BufferedWriter bwTherapy,BufferedWriter bwDevice) throws HillromException{
				

				
		        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
		        String sout = "";
		        for(int i=0;i<b.length;i++) {
		        	int val = b[i] & 0xFF;
		        	sout = sout + val + " ";
		        }
		        
		        log.debug("Input Byte Array :"+sout);
		
		        String deviceData = "";
		        int start = returnMatch(b,DEVICE_DATA_FIELD_NAME);
		        int end = returnMatch(b,CRC_FIELD_NAME)-CRC_FIELD_NAME.length;
		        log.debug("start end : "+ start + " : " + end );
		        
		        byte[] deviceDataArray = new byte[end];
		        int j=0;
		        for(int i=start;i<end;i++) {
		        	deviceDataArray[j++] = b[i];
		        	int val = b[i] & 0xFF;
		        	deviceData = deviceData + String.valueOf(Character.toChars(val));
		        }
		        log.debug("deviceData : "+ sout );
		        
		        if(deviceData.equalsIgnoreCase("PING_PONG_PING")){
		        	return "PING_PONG_PING";
		        }
	        	log.debug("deviceData is NOT PING_PONG_PING" );
		        
				int x = getFragTotal(encoded_string);
				int y = getFragCurrent(encoded_string);
				byte[] devsnbt = getDevSN(encoded_string,bwTherapy);
				byte[] wifibt = getDevWifi(encoded_string,bwTherapy);
				byte[] verbt = getDevVer(encoded_string,bwTherapy);
		        
		        byte[] session_index  = Arrays.copyOfRange(deviceDataArray, SESSION_INDEX_LOC, SESSION_INDEX_LOC + SESSION_INDEX_LEN);
		        sout = "";
		        
		        for(int k=0;k<session_index.length;k++){
		        	sout = sout + (session_index[k]  & 0xFF) + " ";
		        }
		        log.debug("session_index : "+ sout );
		        
		        log.debug("Combined session_index : "+ intergerCombinedFromHex(session_index));
		        writetofile(intergerCombinedFromHex(session_index)+",",bwTherapy);
		        
		        
		        byte[] start_time  = Arrays.copyOfRange(deviceDataArray, START_TIME_LOC, START_TIME_LOC + START_TIME_LEN);
		        sout = "";
		        for(int k=0;k<start_time.length;k++){
		        	
		        	sout = sout + (appendZero((start_time[k]  & 0xFF)+"")) + " ";
		        }
		        //log.debug("start_time : "+ sout );
		        sout = convertToDateTime(sout);
		        log.debug("start_time : "+ sout );
		        writetofile(sout+",",bwTherapy);
		        
		        byte[] end_time  = Arrays.copyOfRange(deviceDataArray, END_TIME_LOC, END_TIME_LOC + END_TIME_LEN);        
		        sout = "";
		        for(int k=0;k<end_time.length;k++){
		        	sout = sout + (appendZero((end_time[k]  & 0xFF)+"")) + " ";
		        }
		        //log.debug("end_time : "+ sout );
		        sout = convertToDateTime(sout);
		        log.debug("end_time : "+ sout );
		        writetofile(sout+",",bwTherapy);
		        
		        byte[] start_battery_level  = Arrays.copyOfRange(deviceDataArray, START_BATTERY_LEVEL_LOC, START_BATTERY_LEVEL_LOC + START_BATTERY_LEVEL_LEN);
		        sout = "";
		        for(int k=0;k<start_battery_level.length;k++){
		        	sout = sout + (start_battery_level[k]  & 0xFF) + " ";
		        }
		        log.debug("start_battery_level : "+ sout );
		        writetofile(sout+",",bwTherapy);
		        
		        byte[] end_battery_level  = Arrays.copyOfRange(deviceDataArray, END_BATTERY_LEVEL_LOC, END_BATTERY_LEVEL_LOC + END_BATTERY_LEVEL_LEN);
		        sout = "";
		        for(int k=0;k<end_battery_level.length;k++){
		        	sout = sout + (end_battery_level[k]  & 0xFF) + " ";
		        }
		        log.debug("end_battery_level : "+ sout );
		        writetofile(sout+",",bwTherapy);
		        
		        byte[] number_of_events  = Arrays.copyOfRange(deviceDataArray, NUMBER_OF_EVENTS_LOC, NUMBER_OF_EVENTS_LOC + NUMBER_OF_EVENTS_LEN);
		        sout = "";
		        for(int k=0;k<number_of_events.length;k++){
		        	sout = sout + (number_of_events[k]  & 0xFF) + " ";
		        }
		        log.debug("number_of_events : "+ sout );
		        writetofile(sout+",",bwTherapy);
		        
		        byte[] number_of_pods  = Arrays.copyOfRange(deviceDataArray, NUMBER_OF_PODS_LOC, NUMBER_OF_PODS_LOC + NUMBER_OF_PODS_LEN);
		        sout = "";
		        for(int k=0;k<number_of_pods.length;k++){
		        	sout = sout + (number_of_pods[k]  & 0xFF) + " ";
		        }
		        log.debug("number_of_pods : "+ sout );
		        writetofile(sout+",",bwTherapy);
		        
		        byte[] hmr_seconds  = Arrays.copyOfRange(deviceDataArray, HMR_SECONDS_LOC, HMR_SECONDS_LOC + HMR_SECONDS_LEN);
		        sout = "";
		        for(int k=0;k<hmr_seconds.length;k++){
		        	sout = sout + (hmr_seconds[k]  & 0xFF) + " ";
		        }
		        log.debug("hmr_seconds : "+ sout );
		        log.debug("Combined hmr_seconds : "+ intergerCombinedFromHex(hmr_seconds));
		        writetofile(intergerCombinedFromHex(hmr_seconds)+",",bwTherapy);
		        
		        //log.debug("Value of deviceDataArray.length : "+ j );
		        for(int i=EVENT_LOG_START_POS+1;i<j;i=i+EVENT_LOG_LEN){
		        	
		        	//log.debug("Value of i : "+ i );
		        	
			        byte[] event_timestamp  = Arrays.copyOfRange(deviceDataArray, i + EVENT_TIMESTAMP_LOC-1, (i+EVENT_TIMESTAMP_LOC-1) + EVENT_TIMESTAMP_LEN);
			        sout = "";
			        for(int k=0;k<event_timestamp.length;k++){
			        	sout = sout + (appendZero((event_timestamp[k]  & 0xFF)+"")) + " ";
			        }
			        //log.debug("event_timestamp : "+ sout );
			        sout = convertToTimestamp(sout);
			        log.debug("event_timestamp : "+ sout );
			        writetofile(sout+",",bwDevice);
			        
			        byte[] event_code  = Arrays.copyOfRange(deviceDataArray, i+EVENT_CODE_LOC-1, (i+EVENT_CODE_LOC-1) + EVENT_CODE_LEN);        
			        sout = "";
			        for(int k=0;k<event_code.length;k++){
			        	sout = sout + (event_code[k]  & 0xFF) + " ";
			        }
			        log.debug("event_code : "+ sout );
			        writetofile(sout+",",bwDevice);
			        
			        byte[] frequency  = Arrays.copyOfRange(deviceDataArray, i+FREQUENCY_LOC-1, (i+FREQUENCY_LOC-1) + FREQUENCY_LEN);
			        sout = "";
			        for(int k=0;k<frequency.length;k++){
			        	sout = sout + (frequency[k]  & 0xFF) + " ";
			        }
			        log.debug("frequency : "+ sout );
			        writetofile(sout+",",bwDevice);

			        
			        byte[] intensity  = Arrays.copyOfRange(deviceDataArray, i+INTENSITY_LOC-1, (i+INTENSITY_LOC-1) + INTENSITY_LEN);
			        sout = "";
			        for(int k=0;k<intensity.length;k++){
			        	sout = sout + (intensity[k]  & 0xFF) + " ";
			        }
			        log.debug("intensity : "+ sout );
			        writetofile(sout+",",bwDevice);

			        
			        byte[] duration  = Arrays.copyOfRange(deviceDataArray, i+DURATION_LOC-1, (i+DURATION_LOC-1) + DURATION_LEN);
			        sout = "";
			        for(int k=0;k<duration.length;k++){
			        	sout = sout + (duration[k]  & 0xFF) + " ";
			        }
			        log.debug("duration : "+ sout );
			        writetofile(sout+",",bwDevice);
			        
			        writetofile("\n",bwDevice);
		        }
		        
		        writetofile("\n",bwTherapy);
		        return "NOT_PING_PONG_PING";
		
			}

			public byte[] getDevSN(String encoded_string,BufferedWriter bwTherapy) throws HillromException{
		        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
		        String sout = "";
		        for(int i=0;i<b.length;i++) {
		        	int val = b[i] & 0xFF;
		        	sout = sout + val + " ";
		        }
		        
		        //log.debug("Input Byte Array in devSN :"+sout);
		
		        
		        String devSN = "";
		        int start = returnMatch(b,DEV_SN);
		        int end = returnMatch(b,DEV_WIFI)-DEV_WIFI.length;
		        log.debug("start end : "+ start + " : " + end );
		        
		        byte[] devSNArray = new byte[end];
		        int j=0;
		        sout = "";
		        for(int i=start;i<end;i++) {
		        	devSNArray[j++] = b[i];
		        	int val = b[i] & 0xFF;
		        	devSN = devSN + val + " ";
		        }

		        
		        log.debug("Value of devSN : "+ devSN );
		        String devSNString = new String(devSNArray);
		        log.debug("devSNString : "+ devSNString );
		        writetofile(devSNString+",",bwTherapy);
		        return devSNArray;
		        
			}
			
			public byte[] getDevWifi(String encoded_string,BufferedWriter bwTherapy) throws HillromException{
		        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
		        String sout = "";
		        for(int i=0;i<b.length;i++) {
		        	int val = b[i] & 0xFF;
		        	sout = sout + val + " ";
		        }
		        
		        //log.debug("Input Byte Array in devWifi :"+sout);
		
		        
		        String devWifi = "";
		        int start = returnMatch(b,DEV_WIFI);
		        int end = returnMatch(b,DEV_VER)-DEV_VER.length;
		        log.debug("start end : "+ start + " : " + end );
		        
		        byte[] devWifiArray = new byte[end];
		        int j=0;
		        sout = "";String hexWifi = "";
		        for(int i=start;i<end;i++) {
		        	devWifiArray[j++] = b[i];
		        	int val = b[i] & 0xFF;
		        	devWifi = devWifi + val + " ";
		        	hexWifi = hexWifi + java.lang.Integer.toHexString(val);
		        }

		        
		        log.debug("Value of devWifi : "+ devWifi );
		        log.debug("Value of devWifi Hex : "+ hexWifi );
		        writetofile(hexWifi+",",bwTherapy);
		        return devWifiArray;
		        
			}
			
			public byte[] getDevVer(String encoded_string,BufferedWriter bwTherapy) throws HillromException{
		        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
		        String sout = "";
		        for(int i=0;i<b.length;i++) {
		        	int val = b[i] & 0xFF;
		        	sout = sout + val + " ";
		        }
		        
		        //log.debug("Input Byte Array in devVer :"+sout);
		
		        
		        String devVer = "";
		        int start = returnMatch(b,DEV_VER);
		        int end = returnMatch(b,FRAG_TOTAL_FIELD_NAME)-FRAG_TOTAL_FIELD_NAME.length;
		        log.debug("start end : "+ start + " : " + end );
		        
		        byte[] devVerArray = new byte[end];
		        int j=0;
		        sout = ""; String hexVer = "";
		        for(int i=start;i<end;i++) {
		        	devVerArray[j++] = b[i];
		        	int val = b[i] & 0xFF;
		        	devVer = devVer + val + " ";
		        	hexVer = hexVer + java.lang.Integer.toHexString(val);
		        }

		        
		        log.debug("Value of devVer : "+ devVer );
		        log.debug("Value of devVer Hex : "+ hexVer );
		        writetofile(hexVer+",",bwTherapy);
		        return devVerArray;
		        
			}
			
			public int getFragTotal(String encoded_string) throws HillromException{
		        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
		        String sout = "";
		        for(int i=0;i<b.length;i++) {
		        	int val = b[i] & 0xFF;
		        	sout = sout + val + " ";
		        }
		        
		        //log.debug("Input Byte Array in getFragTotal :"+sout);
		
		        
		        int start = returnMatch(b,FRAG_TOTAL_FIELD_NAME);
		        log.debug("start : "+ start  );
		        
		        int fragTotal = b[start] & 0xFF;
		        
		        log.debug("Total number of fragments : "+ fragTotal );
		        return fragTotal;
		        
			}
			
			public int getFragCurrent(String encoded_string) throws HillromException{
		        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
		        String sout = "";
		        for(int i=0;i<b.length;i++) {
		        	int val = b[i] & 0xFF;
		        	sout = sout + val + " ";
		        }
		        
		        //log.debug("Input Byte Array in getFragCurrent :"+sout);
		
		        
		        int start = returnMatch(b,FRAG_CURRENT_FIELD_NAME);
		        log.debug("start : "+ start  );
		        
		        int fragCurrent = b[start] & 0xFF;
		        
		        log.debug("Current fragment number : "+ fragCurrent );
		        return fragCurrent;
		        
			}
	        
	        private int returnMatch(byte[] inputArray,byte[] matchArray){

	            for(int i=0;i<inputArray.length;i++){
	            	int val = inputArray[i] & 0xFF;
	            	boolean found = false;
	            	
	            	if((val == 38) && !found){
	            		int j=i;int k=0;
	            		while((inputArray[j++]==matchArray[k++]) && (k<matchArray.length)){
	            			
	            		}
	            		if(k==matchArray.length){
	            			found = true;
	            			return j;
	            		}
	            	}
	            }
	            
	            return -1;
	        	
	        }
	
	    	public int intergerCombinedFromHex(byte[] input)
	    	{
	    	    
	    	    String hexString =  "";
	    	    int hexTotal = 0;
	    	    for (int t = 0; t < input.length; t++)
	    	    {
	    	    	hexTotal = hexTotal + Integer.parseInt(Integer.toHexString(input[t]& 0xFF), 16);
	    	    }
	    	    log.debug("hexTotal : " + hexTotal);
	    	    return hexTotal;
	    	}



	    	
	    	public void writetofile(String content,BufferedWriter bw) {

	    		try  {

	    			bw.append(content);


	    		} catch (IOException e) {

	    			e.printStackTrace();

	    		}

	    	}
	    	
	    	public String appendZero(String val){
	    		if(val.length() <= 1){
	    			return "0"+val;
	    		}else{
	    			return val;
	    		}
	    	}
	    	
	    	public String convertToDateTime(String val){
	    		val = val.replaceFirst(" ", "-");
	    		val = val.replaceFirst(" ", "-");
	    		val = val.replaceFirst(" ", "t");
	    		val = val.replaceFirst(" ", ":");
	    		val = val.replaceFirst(" ", ":");
	    		val = val.replaceFirst("t", " ");
	    		return val;
	    	}
	
	    	public String convertToTimestamp(String val){
	    		val = val.replaceFirst(" ", ":");
	    		val = val.replaceFirst(" ", ":");
	    		return val;
	    	}	

	
}
