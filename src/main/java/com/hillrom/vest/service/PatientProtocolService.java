package com.hillrom.vest.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.PatientProtocolDataPK;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientProtocolRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.util.RelationshipLabelConstants;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PatientProtocolService {

    private final Logger log = LoggerFactory.getLogger(PatientProtocolService.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private PatientProtocolRepository patientProtocolRepository;
    
    @Inject
    private PatientInfoRepository patientInfoRepository;
    
    public JSONObject addProtocolToPatient(Long id, Map<String, String> protocolData) {
    	JSONObject jsonObject = new JSONObject();
    	User patientUser = userRepository.findOne(id);
		if(patientUser != null) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		 	if(patientInfo != null){
		 		Optional<PatientProtocolData> currentAssoc = patientProtocolRepository.findOneByPatientIdAndActiveStatus(patientInfo.getId(), true);
		 		if(currentAssoc.isPresent()){
	 				currentAssoc.get().setActive(false);
	 				patientProtocolRepository.save(currentAssoc.get());
		 		}
		 		PatientProtocolData patientProtocolAssoc = new PatientProtocolData(new PatientProtocolDataPK(patientInfo), 
		 				Integer.parseInt(protocolData.get("treatmentsPerDay")), Integer.parseInt(protocolData.get("minutesPerTreatment")), 
		 				protocolData.get("frequencies"), Integer.parseInt(protocolData.get("minimumMinutesOfUsePerDay")), true, protocolData.get("type"));
		 		patientProtocolRepository.save(patientProtocolAssoc);
		 		jsonObject.put("message", "Custom protocol is created successfully.");
		 		jsonObject.put("patient", patientProtocolAssoc);
		 	} else {
		 		jsonObject.put("ERROR", "No such patient exist");
		 	}
		} else {
			jsonObject.put("ERROR", "No such user exist");
		}
    	return jsonObject;
    }

    public JSONObject getProtocolsAssociatedWithPatient(Long id) {
    	JSONObject jsonObject = new JSONObject();
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<PatientProtocolData> protocolList = patientProtocolRepository.findByPatientId(patientInfo.getId());
	     		if(protocolList.isEmpty()){
	     			jsonObject.put("message", "No custom protocol found for patient.");
	     		} else {
	     			jsonObject.put("message", "Custom protocols for patient fetched successfully.");
	     			jsonObject.put("protocolList", protocolList);
	     		}
	     	} else {
	     		jsonObject.put("ERROR", "No such patient exist");
	     	}
    	} else {
     		jsonObject.put("ERROR", "No such user exist");
     	}
    	return jsonObject;
    }

	private PatientInfo getPatientInfoObjFromPatientUser(User patientUser) {
		PatientInfo patientInfo = null;
		for(UserPatientAssoc patientAssoc : patientUser.getUserPatientAssoc()){
			if(RelationshipLabelConstants.SELF.equals(patientAssoc.getRelationshipLabel())){
				patientInfo = patientAssoc.getPatient();
			}
		}
		return patientInfo;
	}
	
	public JSONObject deactivateProtocolFromPatient(Long id, Long protocolId) {
    	JSONObject jsonObject = new JSONObject();
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		Optional<PatientProtocolData> patientDeviceAssoc = patientProtocolRepository.findOneByPatientIdAndProtocolId(patientInfo.getId(), protocolId);
	     		if(patientDeviceAssoc.isPresent()){
	     			if(patientDeviceAssoc.get().isActive()) {
		     			patientDeviceAssoc.get().setActive(false);
		     			patientProtocolRepository.delete(patientDeviceAssoc.get());
		     			jsonObject.put("message", "Custom protocol for patient is deactivated successfully.");
	     			} else {
	     				jsonObject.put("ERROR", "Custom protocol is already in Inactive mode.");
	     			}
	     		} else {
	     			jsonObject.put("ERROR", "Invalid Protocol id.");
	     		}
	     	} else {
	     		jsonObject.put("ERROR", "No such patient exist");
	     	}
    	} else {
     		jsonObject.put("ERROR", "No such user exist");
     	}
    	return jsonObject;
    }
}

