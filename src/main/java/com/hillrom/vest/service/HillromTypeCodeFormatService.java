package com.hillrom.vest.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.HillromTypeCodeFormat;
import com.hillrom.vest.repository.HillromTypeCodeFormatRepository;
import com.hillrom.vest.service.util.DateUtil;

@Service
@Transactional
public class HillromTypeCodeFormatService {
	
	@Inject
    private HillromTypeCodeFormatRepository hillromTypeCodeFormatRepository;   

	public List<String> findCodeValuesList(String codeType) {
		List<String> typeCodeList = hillromTypeCodeFormatRepository.findCodeValuesList(codeType);
		return typeCodeList;
	}
	public List<HillromTypeCodeFormat> getDiagnosisTypeCode(String searchString) {
			List<HillromTypeCodeFormat> typeCodeList = hillromTypeCodeFormatRepository.findDiagnosisTypeCode(searchString);
			return typeCodeList;
	}
	

	// fetch the generic time zones from DateUtil 
	public Map<String,String> getGenericTimeZonesList(){
		Map<String,String> timeZones = new HashMap<String,String>();
		try {
			timeZones =  DateUtil.getTimeZoneList();
			Map<String,String> treeMap = new TreeMap<String,String>(timeZones); // to sort the keys(time zones) in alphabetical order
			return treeMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	

}
