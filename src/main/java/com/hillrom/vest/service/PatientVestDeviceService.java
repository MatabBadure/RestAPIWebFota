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
import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.PatientVestDevicePK;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.util.RelationshipLabelConstants;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PatientVestDeviceService {

    private final Logger log = LoggerFactory.getLogger(PatientVestDeviceService.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private PatientVestDeviceRepository patientVestDeviceRepository;
    
    @Inject
    private PatientInfoRepository patientInfoRepository;
    
    public JSONObject linkVestDeviceWithPatient(Long id, Map<String, String> deviceData) {
    	JSONObject jsonObject = new JSONObject();
    	List<PatientVestDeviceHistory> assocList = patientVestDeviceRepository.findBySerialNumber(deviceData.get("serialNumber"));
    	if(assocList.isEmpty()) {
    		assignDeviceToPatient(id, deviceData, jsonObject);
    	} else {
    		PatientVestDeviceHistory activeDevice = (PatientVestDeviceHistory) assocList.stream().filter(patientDevice -> patientDevice.isActive());
    		if(activeDevice != null){
    			User alreadyLinkedPatientuser = new User();
    			alreadyLinkedPatientuser = (User) activeDevice.getPatient().getUserPatientAssoc().stream().filter(userPatientAssoc -> RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel()));
    			jsonObject.put("ERROR", "This Vest device is already linked to patient.");
    			jsonObject.put("user", alreadyLinkedPatientuser);
    			return jsonObject;
    		} else {
    			assignDeviceToPatient(id, deviceData, jsonObject);
    		}
    	}
    	return jsonObject;
    }

	private void assignDeviceToPatient(Long id, Map<String, String> deviceData,
			JSONObject jsonObject) {
		User patientUser = userRepository.findOne(id);
		if(patientUser != null) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		 	if(patientInfo != null){
		 		patientInfo.setSerialNumber(deviceData.get("serialNumber"));
		 		patientInfo.setBluetoothId(deviceData.get("bluetoothId"));
		 		patientInfoRepository.save(patientInfo);
		 		Optional<PatientVestDeviceHistory> currentAssoc = patientVestDeviceRepository.findOneByPatientIdAndActiveStatus(patientInfo.getId(), true);
		 		if(currentAssoc.isPresent()){
	 				currentAssoc.get().setActive(false);
	 				patientVestDeviceRepository.save(currentAssoc.get());
		 		}
		 		PatientVestDeviceHistory patientVestDeviceAssoc = new PatientVestDeviceHistory(
		 				new PatientVestDevicePK(patientInfo, deviceData.get("serialNumber")), deviceData.get("bluetoothId"), deviceData.get("hubId"), true);
		 		patientVestDeviceRepository.save(patientVestDeviceAssoc);
		 		jsonObject.put("message", "Vest device is linked successfully.");
		 		jsonObject.put("patient", patientInfo);
		 	} else {
		 		jsonObject.put("ERROR", "No such patient exist");
		 	}
		} else {
			jsonObject.put("ERROR", "No such user exist");
		}
	}
    
    public JSONObject getLinkedVestDeviceWithPatient(Long id) {
    	JSONObject jsonObject = new JSONObject();
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<PatientVestDeviceHistory> deviceList = patientVestDeviceRepository.findByPatientId(patientInfo.getId());
	     		if(deviceList.isEmpty()){
	     			jsonObject.put("message", "No device linked with patient.");
	     		} else {
	     			jsonObject.put("message", "Vest devices linked with patient fetched successfully.");
	     			jsonObject.put("deviceList", deviceList);
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
}

