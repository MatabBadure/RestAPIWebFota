package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.ClinicPatientAssoc;
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
    
    @Inject
    private UserService userService;
    
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
		    	for(Map<String, String> hcpId : hcpList) {
		    		UserExtension hcpUser = userExtensionRepository.findOne(Long.parseLong(hcpId.get("id")));
		    		if(hcpUser != null) {
			    		UserPatientAssoc userPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, hcpUser), AuthoritiesConstants.HCP, RelationshipLabelConstants.HCP);
			    		hcpPatientAssocList.add(userPatientAssoc);
		    		} else {
		    			jsonObject.put("ERROR", "Invalid HCP id");
		    			return jsonObject;
		    		}
		    	}
		    	userPatientRepository.save(hcpPatientAssocList);
		    	jsonObject.put("message", "HCPs are associated with patient successfully.");
		    	jsonObject.put("hcpUsers", getAssociatedHCPUserList(patientInfo));
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
		    	List<User> hcpUsers = getAssociatedHCPUserList(patientInfo);
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

	private List<User> getAssociatedHCPUserList(PatientInfo patientInfo) {
		List<User> hcpUsers = new LinkedList<>();
		for(UserPatientAssoc userPatientAssoc : patientInfo.getUserPatientAssoc()){
			if(RelationshipLabelConstants.HCP.equals(userPatientAssoc.getRelationshipLabel())){
				hcpUsers.add(userPatientAssoc.getUser());
			}
		}
		return hcpUsers;
	}
	
	public List<Map<String,Object>> getAssociatedPatientUsersForHCP(Long id, String filterByClinicId) throws HillromException {
    	UserExtension hcpUser = userExtensionRepository.findOne(id);
    	if(hcpUser != null) {
    		List<PatientInfo> patientList = new LinkedList<>();
	     	for(UserPatientAssoc patientAssoc : hcpUser.getUserPatientAssoc()){
	    		if(RelationshipLabelConstants.HCP.equals(patientAssoc.getRelationshipLabel())){
	    			patientList.add(patientAssoc.getPatient());
	    		}
	    	}
	     	if(!patientList.isEmpty()){
	     		List<Map<String,Object>> responseList = new LinkedList<Map<String,Object>>();
	     		patientList.forEach(patientInfo -> {
	     			Map<String, Object> entity = new HashMap<>();
	     			entity.put("patientInfo", patientInfo);
	     			entity.put("patientUser", userService.getUserObjFromPatientInfo(patientInfo));
	     			Set<ClinicPatientAssoc> filteredList = new HashSet<>();
	     			if(!Constants.ALL.equals(filterByClinicId)) {
	     				if(!patientInfo.getClinicPatientAssoc().isEmpty()) {
		     				filteredList = (Set<ClinicPatientAssoc>) patientInfo.getClinicPatientAssoc().stream()
		     						.filter(clinicPatientAssoc
		     								-> clinicPatientAssoc.getClinic()
		     								.getId().equals(filterByClinicId))
		     								.collect(Collectors.toSet());
		     			}
	     			} else {
	     				filteredList = patientInfo.getClinicPatientAssoc();
	     			}
	     			List<Clinic> clinics = new LinkedList<>();
	     			filteredList.forEach(cpa -> {
	     				clinics.add(cpa.getClinic());
	     			});
	     			entity.put("clinics", clinics);
	     			responseList.add(entity);
	     		});
		    	return responseList;
	     	} else {
	     		throw new HillromException(ExceptionConstants.HR_571);
	     	}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_512);
     	}
    }
}

