package com.hillrom.vest.service;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.minidev.json.JSONObject;
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
    
    public JSONObject dissociateClinicFromHCP(Long id, List<Map<String, String>> clinicList) {
    	JSONObject jsonObject = new JSONObject();
    	UserExtension hcpUser = userExtensionRepository.getOne(id);
    	for(Map<String, String> clinicId : clinicList) {
    		Clinic clinic = clinicRepository.getOne(Long.parseLong(clinicId.get("id")));
    		if(clinic.getUsers().contains(hcpUser)){
    			clinic.getUsers().remove(hcpUser);
    		}
    		clinicRepository.save(clinic);
    		if(hcpUser.getClinics().contains(clinic)){
    			hcpUser.getClinics().remove(clinic);
    		}
    	}
    	jsonObject.put("message", "HCP is dissociated with Clinics successfully.");
    	jsonObject.put("HCPUser", hcpUser);
    	return jsonObject;
    }
}

