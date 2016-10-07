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
	
	


	
	public JSONObject saveOrUpdateChargerData(String decoded_string) throws HillromException{	
		
		int[] decoded_int = new int[decoded_string.length()];
		for(int i=0;i<decoded_string.length();i++){
			int c = decoded_string.charAt(i);
			decoded_int[i] = c;
			decoded_int[i] = decoded_int[i] & 0xff;
		}
		

		
		JSONObject chargerJsonData = validateRequest(decoded_string,decoded_int);
		ChargerData chargerData = new ChargerData();
		chargerData.setDeviceData(chargerJsonData.get(DEVICE_DATA).toString());
		chargerData.setCreatedTime(new DateTime());
		chargerDataRepository.save(chargerData);
		return chargerJsonData;

	}
	

	
	private JSONObject validateRequest(String rawData,int[] decoded_int) throws HillromException {
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
				}else{
					if(!validateCheckSum(decoded_int)){
						chargerJsonData.put("RESULT", "NOT OK");
						chargerJsonData.put("ERROR","CRC Validation Failed");
						return chargerJsonData;
					}else{
						chargerJsonData.put("RESULT", "OK");
						chargerJsonData.put("ERROR","");
						return chargerJsonData;					
					}
				}
			}else{
				if(!validateCheckSum(decoded_int)){
					chargerJsonData.put("RESULT", "NOT OK");
					chargerJsonData.put("ERROR","CRC Validation Failed");
					return chargerJsonData;
				}else{
					chargerJsonData.put("RESULT", "OK");
					chargerJsonData.put("ERROR","");
					return chargerJsonData;					
				}
			}
		}
		
		return chargerJsonData;
	}
	


	private boolean validateCheckSum(int[] int_input) throws HillromException {
		int crc_value = 0;
		String sOut = "";
	
		for(int i=0;i<int_input.length;i++){
			sOut = sOut + int_input[i] + " ";
		}

		log.error("Full Decimal Byte Array : "+sOut);
		
		int secondlast_digit = -1;
		int last_digit = -1;
		
		secondlast_digit = int_input[int_input.length-2];
		last_digit = int_input[int_input.length-1];		
		log.error("second last digit : " + secondlast_digit);		
		log.error("last digit : " + last_digit);
		

		sOut = "";
		for(int i=0;i<int_input.length-2;i++){
			sOut = sOut + int_input[i] + " ";
		}
		log.error("Full Decimal Byte till CRC : "+sOut);
		
		for(int i=0;i<int_input.length-2;i++){
			crc_value = crc_value + int_input[i];
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
	
	

	
}
