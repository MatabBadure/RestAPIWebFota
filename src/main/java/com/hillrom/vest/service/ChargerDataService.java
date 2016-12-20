package com.hillrom.vest.service;

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
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ChargerDataRepository;
import com.hillrom.vest.repository.NoteRepository;
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

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_TIMESTAMP_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_CODE_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.FREQUENCY_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.INTENSITY_LEN;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DURATION_LEN;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.EVENT_LOG_START_POS;

@Service
@Transactional
public class ChargerDataService {


			private final Logger log = LoggerFactory.getLogger(ChargerDataService.class);
		
			@Inject
			private ChargerDataRepository chargerDataRepository;
			
			
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
	
	


	
			public JSONObject saveOrUpdateChargerData(String encoded_string, String decoded_string) throws HillromException{	
				
				
				log.error("Encoded String : " + encoded_string);
				log.error("Decoded String : " + decoded_string);
				
				JSONObject chargerJsonData = validateRequest(encoded_string,decoded_string);
				if(chargerJsonData.get("RESULT").equals("OK")){
					ChargerData chargerData = new ChargerData();
					chargerData.setDeviceData(encoded_string);
					chargerData.setCreatedTime(new DateTime());
					chargerDataRepository.save(chargerData);
				}
				return chargerJsonData;
		
			}
	

	
			private JSONObject validateRequest(String rawData,String decoded_data) throws HillromException {
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
							chargerJsonData.put("RESULT", "NOT OK");
							chargerJsonData.put("ERROR","Missing Params");
							return chargerJsonData;
						}else{
							if(!calculateCRC(rawData)){
								chargerJsonData.put("RESULT", "NOT OK");
								chargerJsonData.put("ERROR","CRC Validation Failed");
								return chargerJsonData;
							}else{
								getDeviceData(rawData);
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
							getDeviceData(rawData);
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
	  
			public void getDeviceData(String encoded_string) throws HillromException{
		        byte[] b = java.util.Base64.getDecoder().decode(encoded_string);
		        //b = new byte[] {100,101,118,105,99,101,95,109,111,100,101,108,95,116,121,112,101,61,72,105,108,108,82,111,109,95,77,111,110,97,114,99,104,38,100,101,118,83,78,61,82,48,48,49,80,80,48,48,48,49,38,100,101,118,87,73,70,73,61,0,35,(byte)167,38,100,101,118,86,101,114,61,4,0,0,0,2,0,0,0,38,102,114,97,103,84,111,116,97,108,61,1,38,102,114,97,103,67,117,114,114,101,110,116,61,1,38,100,101,118,105,99,101,68,97,116,97,61,0,0,0,3,17,1,1,4,30,16,17,1,1,4,45,37,0,0,10,(byte)128,0,0,1,50,60,4,30,16,1,12,5,20,62,60,4,30,52,5,12,5,20,62,60,4,31,25,6,12,5,20,62,60,4,33,16,2,12,5,20,62,60,4,33,50,2,12,5,20,62,60,4,34,56,2,12,5,19,62,60,4,34,59,2,12,5,9,62,60,4,35,1,5,12,5,9,62,60,4,35,12,6,12,5,9,62,60,4,35,37,5,12,5,9,62,38,99,114,99,61,(byte)197,(byte)206};
		        byte[] match_devicedata = new byte[]{38,100,101,118,105,99,101,68,97,116,97,61};
		        byte[] match_crc = new byte[]{38,99,114,99,61};
		        String sout = "";
		        for(int i=0;i<b.length;i++) {
		        	int val = b[i] & 0xFF;
		        	sout = sout + val + " ";
		        }
		        
		        log.debug("Input Byte Array :"+sout);
		
		        String deviceData = "";
		        int start = returnMatch(b,match_devicedata);
		        int end = returnMatch(b,match_crc)-match_crc.length;
		        log.debug("start end : "+ start + " : " + end );
		        
		        byte[] deviceDataArray = new byte[end];
		        int j=0;
		        for(int i=start;i<end;i++) {
		        	deviceDataArray[j++] = b[i];
		        	int val = b[i] & 0xFF;
		        	deviceData = deviceData + String.valueOf(Character.toChars(val));
		        }
		        log.debug("deviceData : "+ sout );
		        
		        byte[] session_index  = Arrays.copyOfRange(deviceDataArray, SESSION_INDEX_LOC, SESSION_INDEX_LOC + SESSION_INDEX_LEN);
		        sout = "";
		        
		        for(int k=0;k<session_index.length;k++){
		        	sout = sout + (session_index[k]  & 0xFF) + " ";
		        }
		        log.debug("session_index : "+ sout );
		              
		        byte[] start_time  = Arrays.copyOfRange(deviceDataArray, START_TIME_LOC, START_TIME_LOC + START_TIME_LEN);
		        sout = "";
		        for(int k=0;k<start_time.length;k++){
		        	sout = sout + (start_time[k]  & 0xFF) + " ";
		        }
		        log.debug("start_time : "+ sout );
		        
		        byte[] end_time  = Arrays.copyOfRange(deviceDataArray, END_TIME_LOC, END_TIME_LOC + END_TIME_LEN);        
		        sout = "";
		        for(int k=0;k<end_time.length;k++){
		        	sout = sout + (end_time[k]  & 0xFF) + " ";
		        }
		        log.debug("end_time : "+ sout );
		        
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
		        log.debug("hmr_seconds : "+ sout );
		        
		        //log.debug("Value of deviceDataArray.length : "+ j );
		        for(int i=EVENT_LOG_START_POS;i<j;i=i+7){
		        	
		        	//log.debug("Value of i : "+ i );
		        	
			        byte[] event_timestamp  = Arrays.copyOfRange(deviceDataArray, i + EVENT_TIMESTAMP_LOC-1, (i+EVENT_TIMESTAMP_LOC-1) + EVENT_TIMESTAMP_LEN);
			        sout = "";
			        for(int k=0;k<event_timestamp.length;k++){
			        	sout = sout + (event_timestamp[k]  & 0xFF) + " ";
			        }
			        log.debug("event_timestamp : "+ sout );
			        
			        byte[] event_code  = Arrays.copyOfRange(deviceDataArray, i+EVENT_CODE_LOC-1, (i+EVENT_CODE_LOC-1) + EVENT_CODE_LEN);        
			        sout = "";
			        for(int k=0;k<event_code.length;k++){
			        	sout = sout + (event_code[k]  & 0xFF) + " ";
			        }
			        log.debug("event_code : "+ sout );
			        
			        byte[] frequency  = Arrays.copyOfRange(deviceDataArray, i+FREQUENCY_LOC-1, (i+FREQUENCY_LOC-1) + FREQUENCY_LEN);
			        sout = "";
			        for(int k=0;k<frequency.length;k++){
			        	sout = sout + (frequency[k]  & 0xFF) + " ";
			        }
			        log.debug("frequency : "+ sout );

			        
			        byte[] intensity  = Arrays.copyOfRange(deviceDataArray, i+INTENSITY_LOC-1, (i+INTENSITY_LOC-1) + INTENSITY_LEN);
			        sout = "";
			        for(int k=0;k<intensity.length;k++){
			        	sout = sout + (intensity[k]  & 0xFF) + " ";
			        }
			        log.debug("intensity : "+ sout );

			        
			        byte[] duration  = Arrays.copyOfRange(deviceDataArray, i+DURATION_LOC-1, (i+DURATION_LOC-1) + DURATION_LEN);
			        sout = "";
			        for(int k=0;k<duration.length;k++){
			        	sout = sout + (duration[k]  & 0xFF) + " ";
			        }
			        log.debug("duration : "+ sout );
		        }
		
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
	

	
	

	
}
