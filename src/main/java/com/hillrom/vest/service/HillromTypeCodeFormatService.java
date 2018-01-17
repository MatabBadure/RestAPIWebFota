package com.hillrom.vest.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.HillromTypeCodeFormat;
import com.hillrom.vest.repository.HillromTypeCodeFormatRepository;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.TimeZoneUtil;

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

	// fetches the time zones from TimeZoneUtil
	public Map<String, String> getTimeZoneList(){
		Map<String,String> timeZones = new LinkedHashMap<String,String>();
		try {
			timeZones = TimeZoneUtil.getTimezones();
			return timeZones;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
