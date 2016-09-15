package com.hillrom.vest.service;

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
				throw new HillromException("Missing Params : "+String.join(",",missingParams));
			}else{
				if(!validateCheckSum(chargerJsonData.getOrDefault(CRC, new JSONObject()).toString()))
					throw new HillromException("Invalid Checksum : "+chargerJsonData.getOrDefault(CRC, new JSONObject()).toString());	
			}
		}
		
		return chargerJsonData;
	}
	
	private boolean validateCheckSum(String checkSum) throws HillromException {
		String validCheckSum = "XXXX";
		if(checkSum.equalsIgnoreCase(validCheckSum)){
			return true;
		}else{
			return false;
		}
	}

}
