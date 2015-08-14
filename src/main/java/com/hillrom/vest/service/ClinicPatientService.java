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

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.ClinicPatientAssocPK;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.repository.ClinicPatientRepository;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ClinicPatientService {

    private final Logger log = LoggerFactory.getLogger(ClinicPatientService.class);

    @Inject
    private UserPatientRepository userPatientRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ClinicRepository clinicRepository;
    
    @Inject
    private ClinicPatientRepository clinicPatientRepository;
    
    @Inject
    private PatientInfoRepository patientInfoRepository;
    
    public JSONObject associateClinicsToPatient(Long id, List<Map<String, String>> clinicList) {
    	JSONObject jsonObject = new JSONObject();
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<ClinicPatientAssoc> clinicPatientAssocList = new ArrayList<>();
	     		List<UserPatientAssoc> userPatientAssocList = new ArrayList<>();
	     		getAssocObjLists(clinicList, patientInfo, clinicPatientAssocList, userPatientAssocList);
		    	clinicPatientRepository.save(clinicPatientAssocList);
		    	userPatientRepository.save(userPatientAssocList);
		    	jsonObject.put("message", "Clinics are associated with patient successfully.");
		    	jsonObject.put("clinics", getAssociatedClinicsList(patientInfo));
	     	} else {
	     		jsonObject.put("ERROR", "No such patient exist");
	     	}
    	} else {
     		jsonObject.put("ERROR", "No such user exist");
     	}
    	return jsonObject;
    }
    
    public JSONObject getAssociatedClinicsForPatient(Long id) {
    	JSONObject jsonObject = new JSONObject();
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
    		PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
	     	if(patientInfo != null){
		    	List<Clinic> clinics = getAssociatedClinicsList(patientInfo);
		    	jsonObject.put("message", "Associated clinics with patient fetched successfully.");
		    	jsonObject.put("clinics", clinics);
	     	} else {
	     		jsonObject.put("ERROR", "No such patient exist");
	     	}
    	} else {
     		jsonObject.put("ERROR", "No such user exist");
     	}
    	return jsonObject;
    }

	private List<Clinic> getAssociatedClinicsList(PatientInfo patientInfo) {
		patientInfoRepository.saveAndFlush(patientInfo);
		List<Clinic> clinics = new LinkedList<>();
		for(ClinicPatientAssoc clinicPatientAssoc : patientInfo.getClinicPatientAssoc()){
			clinics.add(clinicPatientAssoc.getClinic());
		}
		return clinics;
	}
	
    public JSONObject dissociateClinicsToPatient(Long id, List<Map<String, String>> clinicList) {
    	JSONObject jsonObject = new JSONObject();
    	User patientUser = userRepository.findOne(id);
    	if(patientUser != null) {
	    	PatientInfo patientInfo = getPatientInfoObjeFromPatientUser(patientUser);
	     	if(patientInfo != null){
	     		List<ClinicPatientAssoc> clinicPatientAssocList = new ArrayList<>();
	     		List<UserPatientAssoc> userPatientAssocList = new ArrayList<>();
		    	getAssocObjLists(clinicList, patientInfo, clinicPatientAssocList, userPatientAssocList);
		    	if (userPatientAssocList.size() > 0) userPatientRepository.delete(userPatientAssocList);
		    	if (clinicPatientAssocList.size() > 0) clinicPatientRepository.delete(clinicPatientAssocList);
		    	jsonObject.put("message", "Clinics are associated with patient successfully.");
		    	jsonObject.put("clinics", getAssociatedClinicsList(patientInfo));
	     	} else {
	     		jsonObject.put("ERROR", "No such patient exist");
	     	}
    	} else {
     		jsonObject.put("ERROR", "No such user exist");
     	}
    	return jsonObject;
    }

	private PatientInfo getPatientInfoObjeFromPatientUser(User patientUser) {
		PatientInfo patientInfo = null;
		for(UserPatientAssoc patientAssoc : patientUser.getUserPatientAssoc()){
			if(RelationshipLabelConstants.SELF.equals(patientAssoc.getRelationshipLabel())){
				patientInfo = patientAssoc.getPatient();
			}
		}
		return patientInfo;
	}

	private void getAssocObjLists(List<Map<String, String>> clinicList,
			PatientInfo patientInfo,
			List<ClinicPatientAssoc> clinicPatientAssocList,
			List<UserPatientAssoc> userPatientAssocList) {
		for(Map<String, String> clinicAssocDetails : clinicList) {
			Clinic clinic = clinicRepository.findOne(clinicAssocDetails.get("id"));
			if(clinic != null) {
				ClinicPatientAssoc clinicPatientAssoc = new ClinicPatientAssoc(new ClinicPatientAssocPK(patientInfo, clinic), clinicAssocDetails.get("mrnId"), clinicAssocDetails.get("notes"));
				clinicPatientAssocList.add(clinicPatientAssoc);
				if (clinic.getClinicAdminId() != null) {
					User clinicAdminUser = userRepository.findOne(clinic.getClinicAdminId());
					UserPatientAssoc clinicAdminPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, clinicAdminUser), AuthoritiesConstants.CLINIC_ADMIN, RelationshipLabelConstants.CLINIC_ADMIN);
					userPatientAssocList.add(clinicAdminPatientAssoc);
				}
			}
		}
	}
}

