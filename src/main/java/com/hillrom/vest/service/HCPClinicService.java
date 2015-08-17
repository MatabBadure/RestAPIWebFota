package com.hillrom.vest.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.UserExtensionRepository;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class HCPClinicService {

    private final Logger log = LoggerFactory.getLogger(HCPClinicService.class);

    @Inject
    private ClinicRepository clinicRepository;
    
    @Inject
    private UserExtensionRepository userExtensionRepository;
    
    public UserExtension dissociateClinicFromHCP(Long id, List<Map<String, String>> clinicList) {
    	UserExtension hcpUser = userExtensionRepository.getOne(id);
    	for(Map<String, String> clinicId : clinicList) {
    		Clinic clinic = clinicRepository.getOne(clinicId.get("id"));
    		if(clinic.getUsers().contains(hcpUser)){
    			clinic.getUsers().remove(hcpUser);
    		}
    		clinicRepository.save(clinic);
    		if(hcpUser.getClinics().contains(clinic)){
    			hcpUser.getClinics().remove(clinic);
    		}
    	}
    	return hcpUser;
    }
}

