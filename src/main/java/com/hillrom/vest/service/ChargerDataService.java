package com.hillrom.vest.service;

import java.math.BigInteger;
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
	
	

	
	public ChargerData saveOrUpdateChargerData(String rawData) throws HillromException{			
		JSONObject chargerJsonData = validateRequest(rawData);
		ChargerData chargerData = new ChargerData();
		chargerData.setDeviceData(chargerJsonData.get(DEVICE_DATA).toString());
		chargerData.setCreatedTime(new DateTime());
		chargerDataRepository.save(chargerData);
		return chargerData;
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
					throw new HillromException("Missing Params : "+String.join(",",missingParams));
				}else{
					if(!validateCheckSum((rawData.substring(0, rawData.lastIndexOf("&crc=")+5)),(int)rawData.charAt(rawData.length()-2),(int)rawData.charAt(rawData.length()-1))){
						//throw new HillromException("Invalid Checksum : "+chargerJsonData.getOrDefault(CRC, new JSONObject()).toString());	
					return chargerJsonData;
					}
				}
			}else{
				if(!validateCheckSum((rawData.substring(0, rawData.lastIndexOf("&crc=")+5)),(int)rawData.charAt(rawData.length()-2),(int)rawData.charAt(rawData.length()-1))){
					//throw new HillromException("Invalid Checksum : "+chargerJsonData.getOrDefault(CRC, new JSONObject()).toString());	
				return chargerJsonData;
				}
			}
		}
		
		return chargerJsonData;
	}
	
	private boolean validateCheckSum(String rawData,int secondlast_digit,int last_digit) throws HillromException {
		log.error("Raw Data inside validate check sum : " + rawData);
		
		String buffer = rawData;
		int crc_value = 0;
		String sOut = "";


		
		for(int i=0;i<buffer.length() - 2;i++)
		{
			sOut = sOut + (int)buffer.charAt(i) + " ";
		    crc_value = crc_value + (int)buffer.charAt(i);
		}
		
		log.error("second last digit : " + secondlast_digit);		
		log.error("last digit : " + last_digit);


		log.error("decimals till &crc= : "+sOut);

		log.error("calculated total : " + crc_value);
		
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
			
		
		
		//Example for 307 Lsb = 51 and msb = 1
		//
		// 8296 - Once complement = 57239 + 1 = 57240
		// 7186 - 58349 + 1 = 58350
		// 11864  - 53671 + 1 = 53672

		
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
