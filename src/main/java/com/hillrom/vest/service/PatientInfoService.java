package com.hillrom.vest.service;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.repository.PatientInfoRepository;

@Service
@Transactional
public class PatientInfoService {

	private final Logger log = LoggerFactory.getLogger(PatientInfoService.class);

    @Inject
    private PatientInfoRepository patientInfoRepository;
    
    
    public Optional<PatientInfo> findOneByHillromId(String hillRomId){
    	log.debug("hillRomId :: "+hillRomId);
    	Optional<PatientInfo> patientInfo = patientInfoRepository.findOneByHillromId(hillRomId);
    	return patientInfo;
    }
    
    public PatientInfo findOneById(String id){
    	log.debug("id :: "+id);
    	PatientInfo patientInfo = patientInfoRepository.findOneById(id);
    	return patientInfo;
    }
    
 
    public void update(PatientInfo patientInfo){
    	log.debug("hillRomId :: "+patientInfo);
    	patientInfoRepository.save(patientInfo);
    }
}
