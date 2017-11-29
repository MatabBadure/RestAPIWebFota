package com.hillrom.vest.service;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AdvancedSearchRepository;
import com.hillrom.vest.web.rest.dto.AdvancedClinicDTO;
import com.hillrom.vest.web.rest.dto.AdvancedHcpDTO;
import com.hillrom.vest.web.rest.dto.AdvancedPatientDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.HcpVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;

@Service
@Transactional
public class AdvancedSearchService {
	
	private final Logger log = LoggerFactory.getLogger(AdvancedSearchService.class);

    
    @Inject
	private AdvancedSearchRepository advancedSearchRepository;

	public Page<ClinicVO> advancedSearchClinics(AdvancedClinicDTO advancedClinicDTO, Pageable pageable,Map<String, Boolean> sortOrder) throws HillromException {
		Page<ClinicVO> page = advancedSearchRepository.advancedSearchClinics(advancedClinicDTO, pageable,sortOrder);
    	return page;
	}
	
	public Page<PatientUserVO> advancedSearchPatients(AdvancedPatientDTO advancedPatientDTO, Pageable pageable,Map<String, Boolean> sortOrder) throws HillromException {
		Page<PatientUserVO> page = advancedSearchRepository.advancedSearchPatients(advancedPatientDTO, pageable,sortOrder);
    	return page;
	}
	
	public Page<HcpVO> advancedSearchHcps(AdvancedHcpDTO advancedHcpDTO, Pageable pageable,Map<String, Boolean> sortOrder) throws HillromException {
		Page<HcpVO> page = advancedSearchRepository.advancedSearchHcps(advancedHcpDTO, pageable,sortOrder);
    	return page;
	}
}
