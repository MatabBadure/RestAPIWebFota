package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
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
    
    public JSONObject associateHCPToPatient(Long id, List<Map<String, String>> hcpList) {
    	JSONObject jsonObject = new JSONObject();
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
	     		List<UserExtension> hcpUserList = new ArrayList<>();
		    	for(Map<String, String> hcpId : hcpList) {
		    		UserExtension hcpUser = userExtensionRepository.findOne(Long.parseLong(hcpId.get("id")));
		    		if(hcpUser != null) {
			    		UserPatientAssoc userPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, hcpUser), AuthoritiesConstants.HCP, RelationshipLabelConstants.HCP);
			    		hcpPatientAssocList.add(userPatientAssoc);
			    		hcpUserList.add(hcpUser);
		    		} else {
		    			jsonObject.put("ERROR", "Invalid HCP id");
		    			return jsonObject;
		    		}
		    	}
		    	userPatientRepository.save(hcpPatientAssocList);
		    	jsonObject.put("message", "HCPs are associated with patient successfully.");
		    	jsonObject.put("hcpList", hcpUserList);
	     	} else {
	     		jsonObject.put("ERROR", "No such patient exist");
	     	}
    	} else {
     		jsonObject.put("ERROR", "No such user exist");
     	}
    	return jsonObject;
    }
    
    public JSONObject getAssociatedHCPUserForPatient(Long id) {
    	JSONObject jsonObject = new JSONObject();
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
    		PatientInfo patientInfo = null;
	     	for(UserPatientAssoc patientAssoc : patientUser.getUserPatientAssoc()){
	    		if(RelationshipLabelConstants.SELF.equals(patientAssoc.getRelationshipLabel())){
	    			patientInfo = patientAssoc.getPatient();
	    		}
	    	}
	     	if(patientInfo != null){
		    	List<User> hcpUsers = new LinkedList<>();
		     	for(UserPatientAssoc userPatientAssoc : patientInfo.getUserPatientAssoc()){
		    		if(RelationshipLabelConstants.HCP.equals(userPatientAssoc.getRelationshipLabel())){
		    			hcpUsers.add(userPatientAssoc.getUser());
		    		}
		    	}
		    	jsonObject.put("message", "Associated HCPs with patient fetched successfully.");
		    	jsonObject.put("hcpUsers", hcpUsers);
	     	} else {
	     		jsonObject.put("ERROR", "No such patient exist");
	     	}
    	} else {
     		jsonObject.put("ERROR", "No such user exist");
     	}
    	return jsonObject;
    }
}

