package com.hillrom.vest.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.HillromTypeCodeFormat;
import com.hillrom.vest.repository.HillromTypeCodeFormatRepository;

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
	
}
