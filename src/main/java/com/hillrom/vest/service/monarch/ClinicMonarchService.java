package com.hillrom.vest.service.monarch;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.EntityUserAssoc;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.EntityUserRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSearchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.ClinicDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.util.ClinicVOBuilder;

//start: HILL-2004
import com.hillrom.vest.service.util.DateUtil;
//end: HILL-2004

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ClinicMonarchService {

    private final Logger log = LoggerFactory.getLogger(ClinicMonarchService.class);

    @Inject
    private ClinicRepository clinicRepository;
    
    @Inject
    private UserMonarchService userMonarchService;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private UserExtensionRepository userExtensionRepository;
    
    @Inject
    private UserSearchRepository userSearchRepository;
    
    @Inject
    private EntityUserRepository entityUserRepository;
    
    public List<Map<String,Object>> getAssociatedPatientUsers(List<String> idList) throws HillromException {
		List<Map<String,Object>> patientUserList = new LinkedList<>();
		for(String id : idList){
	    	Clinic clinic = clinicRepository.getOne(id);
	        if(Objects.isNull(clinic)) {
	        	throw new HillromException(ExceptionConstants.HR_547);
	        } else {
	        	clinic.getClinicPatientAssoc().forEach(clinicPatientAssoc -> {
	        		Map<String, Object> patientMap = new HashMap<>();
	        		UserExtension patientUser = (UserExtension) userMonarchService.getUserObjFromPatientInfo(clinicPatientAssoc.getPatient());
	        		patientMap.put("patient", patientUser);
	        		patientMap.put("mrnId", clinicPatientAssoc.getMrnId());
	        		patientMap.put("status", clinicPatientAssoc.getActive() & !patientUser.isDeleted());
	        		List<UserPatientAssoc> hcpAssocList = new LinkedList<>();
	    	     	for(UserPatientAssoc patientAssoc : clinicPatientAssoc.getPatient().getUserPatientAssoc()){
	    	    		if(AuthoritiesConstants.HCP.equals(patientAssoc.getUserRole())){
	    	    			hcpAssocList.add(patientAssoc);
	    	    		}
	    	    	}
	    	     	Collections.sort(hcpAssocList);
	    	     	if(!hcpAssocList.isEmpty())
	    	     		patientMap.put("hcp",hcpAssocList.get(0).getUser());
	    	     	else patientMap.put("hcp",null);
	    	     	patientUserList.add(patientMap);
	        	});
	        }
		}
		return patientUserList;
	}
	
	

	



}
