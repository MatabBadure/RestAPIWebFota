package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PatientHCPService {

    private final Logger log = LoggerFactory.getLogger(PatientHCPService.class);

    @Inject
    private UserPatientRepository userPatientRepository;
    
    @Inject
    private UserExtensionRepository userExtensionRepository;
    
    @Inject
    private UserRepository userRepository;
    
    public List<User> associateHCPToPatient(Long id, List<Map<String, String>> hcpList) throws HillromException {
    	List<User> users = new LinkedList<>();
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = null;
	     	for(UserPatientAssoc patientAssoc : patientUser.getUserPatientAssoc()){
	    		if(RelationshipLabelConstants.SELF.equals(patientAssoc.getRelationshipLabel())){
	    			patientInfo = patientAssoc.getPatient();
	    		}
	    	}
	     	if(patientInfo != null){
	     		List<UserPatientAssoc> hcpPatientAssocList = new ArrayList<>();
		    	for(Map<String, String> hcpId : hcpList) {
		    		UserExtension hcpUser = userExtensionRepository.findOne(Long.parseLong(hcpId.get("id")));
		    		if(hcpUser != null) {
			    		UserPatientAssoc userPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, hcpUser), AuthoritiesConstants.HCP, RelationshipLabelConstants.HCP);
			    		hcpPatientAssocList.add(userPatientAssoc);
		    		} else {
		    			throw new HillromException(ExceptionConstants.HR_532);//Invalid HCP id
		    		}
		    	}
		    	userPatientRepository.save(hcpPatientAssocList);
		    	users = getAssociatedHCPUserList(patientInfo);
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);//No such user exist
     	}
    	return users;
    }
    
    public List<User> getAssociatedHCPUserForPatient(Long id) throws HillromException {
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
    		PatientInfo patientInfo = null;
	     	for(UserPatientAssoc patientAssoc : patientUser.getUserPatientAssoc()){
	    		if(RelationshipLabelConstants.SELF.equals(patientAssoc.getRelationshipLabel())){
	    			patientInfo = patientAssoc.getPatient();
	    		}
	    	}
	     	if(patientInfo != null){
		    	return getAssociatedHCPUserList(patientInfo);
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_523);//No such patient exist
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);//No such user exist
     	}
    }

	private List<User> getAssociatedHCPUserList(PatientInfo patientInfo) {
		List<User> hcpUsers = new LinkedList<>();
		for(UserPatientAssoc userPatientAssoc : patientInfo.getUserPatientAssoc()){
			if(RelationshipLabelConstants.HCP.equals(userPatientAssoc.getRelationshipLabel())){
				hcpUsers.add(userPatientAssoc.getUser());
			}
		}
		return hcpUsers;
	}
}

