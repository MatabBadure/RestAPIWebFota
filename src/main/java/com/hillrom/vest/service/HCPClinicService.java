package com.hillrom.vest.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.web.rest.dto.ClinicDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.util.ClinicVOBuilder;

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
    		userExtensionRepository.saveAndFlush(hcpUser);
    	}
    	return hcpUser;
    }
    
    public Set<ClinicVO> getAssociatedClinicsForHCP(Long id) throws HillromException {
		UserExtension hcpUser = userExtensionRepository.findOne(id);
		Set<ClinicVO> clinics = new HashSet<>();
	    if(Objects.isNull(hcpUser)){
	    	throw new HillromException(ExceptionConstants.HR_512);
	    } else {
	    	for(Clinic clinic : hcpUser.getClinics()){
	    		clinics.add(ClinicVOBuilder.build(clinic));
	    	}
	    	return clinics;
	    }
    }
    
    public Set<UserExtension> associateHCPToClinic(String id, List<Map<String, String>> hcpList) throws HillromException {
    	Clinic clinic = clinicRepository.findOne(id);
    	if(Objects.nonNull(clinic)) {
	    	for(Map<String, String> hcpId : hcpList) {
	    		UserExtension hcpUser = userExtensionRepository.findOne(Long.parseLong(hcpId.get("id")));
	    		if(Objects.nonNull(hcpUser)) {
		    		clinic.getUsers().add(hcpUser);
		    		hcpUser.getClinics().add(clinic);
		    		clinicRepository.saveAndFlush(clinic);
		    		userExtensionRepository.saveAndFlush(hcpUser);
	    		} else {
	    			throw new HillromException(ExceptionConstants.HR_532);
	    		}
	    	}
	    	return clinic.getUsers();
    	} else {
     		throw new HillromException(ExceptionConstants.HR_544);
     	}
    }
    
	 //Get Clinic Ids Associated with HCP Users Flattened
    public String getFlattenedAssociatedClinicsIdForHCP(Long id) throws HillromException {
		UserExtension hcpUser = userExtensionRepository.findOne(id);
		StringBuilder clinicIdsString = new StringBuilder();
	    if(Objects.isNull(hcpUser)){
	    	throw new HillromException(ExceptionConstants.HR_512);
	    } else {
	    	for(Clinic clinic : hcpUser.getClinics()){
	    		clinicIdsString.append("'");
	    		clinicIdsString.append(clinic.getId());
	    		clinicIdsString.append("',");
	    	}
	    	if(clinicIdsString.indexOf(",") < 0)
	    		return "";
	    	else 
	    		return clinicIdsString.deleteCharAt(clinicIdsString.lastIndexOf(",")).toString();
	    }
    }
}

