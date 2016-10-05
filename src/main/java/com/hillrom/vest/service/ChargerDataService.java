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
	
	

	public JSONObject saveOrUpdateChargerData(int[] rawData) throws HillromException{	
		calculateCheckSum(rawData);
		
		JSONObject chargerJsonData = new JSONObject();
		chargerJsonData.put("RESULT", "OK");
		return chargerJsonData;
	}
	
	public JSONObject saveOrUpdateChargerData(String rawData) throws HillromException{			
		JSONObject chargerJsonData = validateRequest(rawData);
		ChargerData chargerData = new ChargerData();
		chargerData.setDeviceData(chargerJsonData.get(DEVICE_DATA).toString());
		chargerData.setCreatedTime(new DateTime());
		chargerDataRepository.save(chargerData);
		return chargerJsonData;
	}
	
	private JSONObject validateRequest(final String rawData) throws HillromException {
		JSONObject chargerJsonData = ParserUtil.getQclJsonDataFromRawMessage(rawData);
		String reqParams[] = new String[]{DEVICE_SN,
				DEVICE_WIFI,DEVICE_LTE,DEVICE_VER,DEVICE_DATA,CRC};
		
		
		if(Objects.isNull(chargerJsonData) || chargerJsonData.keySet().isEmpty()){
			throw new HillromException("Missing Params : "+String.join(",",reqParams));
		}else if(Objects.nonNull(chargerJsonData)){
			List<String> missingParams = RandomUtil.getDifference(Arrays.asList(reqParams), new ArrayList<String>(chargerJsonData.keySet()));

			if(missingParams.size() > 0){
				if(missingParams.contains(DEVICE_SN) || (missingParams.contains(DEVICE_WIFI) && missingParams.contains(DEVICE_LTE)) ||
						missingParams.contains(DEVICE_VER) || missingParams.contains(DEVICE_DATA) || missingParams.contains(CRC)
						){
					chargerJsonData.put("RESULT", "NOT OK");
					chargerJsonData.put("ERROR","Missing Params");
					return chargerJsonData;
					//throw new HillromException("Missing Params : "+String.join(",",missingParams));
				}else{
					if(!validateCheckSum(rawData)){
						//throw new HillromException("Invalid Checksum : "+chargerJsonData.getOrDefault(CRC, new JSONObject()).toString());	
						chargerJsonData.put("RESULT", "NOT OK");
						chargerJsonData.put("ERROR","CRC Validation Failed");
						return chargerJsonData;
					}
				}
			}else{
				if(!validateCheckSum(rawData)){
					//throw new HillromException("Invalid Checksum : "+chargerJsonData.getOrDefault(CRC, new JSONObject()).toString());	
					chargerJsonData.put("RESULT", "NOT OK");
					chargerJsonData.put("ERROR","CRC Validation Failed");
					return chargerJsonData;
				}else{
					chargerJsonData.put("RESULT", "OK");
					return chargerJsonData;					
				}
			}
		}
		
		return chargerJsonData;
	}
	
	//private boolean validateCheckSum(String rawData,int secondlast_digit,int last_digit) throws HillromException {
	private boolean validateCheckSum(String rawData) throws HillromException {
		log.error("Raw Data inside validate check sum : " + rawData);
		
		int crc_value = 0;
		String sOut = "";

		byte[] b3 = rawData.getBytes(); //rawData.getBytes(StandardCharsets.UTF_8); // Java 7+ only
		int[] int_input = ParserUtil.convertToIntArray(b3);
		for(int i=0;i<int_input.length;i++){
			sOut = sOut + int_input[i] + " ";
		}

		log.error("Full Decimal Byte Array : "+sOut);
		
		int secondlast_digit = -1;
		int last_digit = -1;
		
		log.error("Byte Size of CRC String : "+ rawData.substring(rawData.lastIndexOf("&crc=")+5,rawData.length()).getBytes().length);
		
		
		if((rawData.lastIndexOf("&crc=")>0) && (rawData.substring(rawData.lastIndexOf("&crc=")+5,rawData.length()).getBytes().length==2)){
			secondlast_digit = Math.abs(int_input[int_input.length-2]);
			last_digit = Math.abs(int_input[int_input.length-1]);
		}
		log.error("second last digit : " + secondlast_digit);		
		log.error("last digit : " + last_digit);
		
		String rawData_tillcrc = rawData.substring(0,rawData.lastIndexOf("&crc=")+5);
		log.error("Raw Data till CRC : " + rawData_tillcrc);
		b3 = rawData_tillcrc.getBytes();
		int_input = ParserUtil.convertToIntArray(b3);
		for(int i=0;i<int_input.length;i++){
			sOut = sOut + int_input[i] + " ";
		}
		log.error("Full Decimal Byte till CRC : "+sOut);
		
		for(int i=0;i<int_input.length;i++){
			crc_value = crc_value + Math.abs(b3[i]);
		}
		log.error("crc_value : "+ crc_value);
		
		
		String binary_representation_crc = appendLeadingZeros(Integer.toBinaryString(crc_value));
		
		log.error("binary representation of calculated total : " + binary_representation_crc);
		
		log.error("once complement : " + firstcomplement(binary_representation_crc));
		
		Integer ones_complement_of_crc = (Integer)Integer.parseUnsignedInt(firstcomplement(binary_representation_crc), 2);		
		log.error("Decimal representation of once complement : " + ones_complement_of_crc);
		
		Integer twos_complement_of_crc = (Integer) (ones_complement_of_crc + 1);

		log.error("twos complement : " + twos_complement_of_crc);
		
		log.error("twos complement in binary : " + Integer.toBinaryString(twos_complement_of_crc));


		Integer lsb_digit = (Integer)Integer.parseUnsignedInt(Integer.toBinaryString(twos_complement_of_crc & 0xFF),2);               // Least significant "byte"
		Integer msb_digit = (Integer)Integer.parseUnsignedInt(Integer.toBinaryString((twos_complement_of_crc & 0xFF00) >> 8),2);      // Most significant "byte"
		

		log.error("lsb_digit : " + lsb_digit);
		log.error("msb_digit : " + msb_digit);
		
		if((lsb_digit != secondlast_digit) || (msb_digit != last_digit)){
			log.error("CRC VALIDATION FAILED :"); 
			return false;
		}else{
			return true;
		}
			
		
		
	}

	
	String firstcomplement(String binary)
	{
	    String complement="";
	    for(int i=0; i<binary.length(); i++)
	    {
	         if(binary.charAt(i)=='0')
	             complement = complement + "1";
	         if(binary.charAt(i)=='1')
	        	 complement = complement + "0";
	    }
	    
	    return complement;

	 }
	

	String appendLeadingZeros(String binary)
	{
		int size = binary.length();
		if(size != 16){
			for(int i=0;i<16-size;i++){
				binary = '0'+ binary;
			}
		}

		return binary;
 
	}
	
	
	  int calculateCheckSum(int[] rawData)
	  {
			log.error("RAW DATA : " + rawData);

	    
		    int nCheckSum = 0;
		    
		    for ( int nCounter = 0; nCounter < (rawData.length) - 2; nCounter++ )
		    {
		      nCheckSum += rawData[nCounter];
		    }
		    
		    log.error("Check Sum  : "+ nCheckSum);
		    
		    while ( nCheckSum >  65535 )
		    {
		      nCheckSum -= 65535;
		    }
		    
		    nCheckSum = ((~nCheckSum)& 0xFFFF) + 1;
		    
		    log.error("Check Sum  : "+ nCheckSum);
		    
		    return(nCheckSum);
		  }
	
}
