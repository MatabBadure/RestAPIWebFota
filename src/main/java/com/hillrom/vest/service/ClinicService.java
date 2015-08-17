package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.web.rest.dto.ClinicDTO;


/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ClinicService {

    private final Logger log = LoggerFactory.getLogger(ClinicService.class);

    @Inject
    private ClinicRepository clinicRepository;
    
    @Inject
    private UserService userService;

    public JSONObject createClinic(ClinicDTO clinicDTO) {
    	JSONObject jsonObject = new JSONObject();
    	Clinic newClinic = new Clinic();
    	// Assigns the next clinic HillromId from Stored Procedure
    	newClinic.setId(clinicRepository.id());
    	if(clinicDTO.getParent()) {
    		newClinic.setParent(clinicDTO.getParent());
    	}
    	if(StringUtils.isNotBlank(clinicDTO.getParentClinic().get("id"))) {
    		Clinic parentClinic = clinicRepository.getOne(clinicDTO.getParentClinic().get("id"));
   			parentClinic.setParent(true);
   			clinicRepository.save(parentClinic);
   			newClinic.setParentClinic(parentClinic);
    	}
    	assignUpdatedValues(clinicDTO, newClinic);
		clinicRepository.save(newClinic);
        if(StringUtils.isNotBlank(newClinic.getId())) {
        	jsonObject.put("message", "Clinic created successfully.");
            jsonObject.put("Clinic", newClinic);
            return jsonObject;
        } else {
	      	jsonObject.put("ERROR", "Unable to create Clinic.");
	        return jsonObject;
        }
    }

    public JSONObject updateClinic(String id, ClinicDTO clinicDTO) {
    	JSONObject jsonObject = new JSONObject();
    	Clinic clinic = clinicRepository.getOne(id);
        if(clinic == null) {
	      	jsonObject.put("ERROR", "No such clinic found.");
        } else if(StringUtils.isNotBlank(clinic.getId())) {
        	assignUpdatedValues(clinicDTO, clinic);
        	if(clinicDTO.getParent()) {
	        	List<String> existingChildClinicIds = new ArrayList<String>();
	        	List<String> newChildClinicIds = new ArrayList<String>();
	        	for(Clinic childClinic : clinic.getChildClinics()) {
	        		existingChildClinicIds.add(childClinic.getId().toString());
	        	}
	        	for(Map<String, String> childClinic : clinicDTO.getChildClinicList()) {
	        		newChildClinicIds.add(childClinic.get("id"));
	        	}
	        	List<String> clinicsToBeRemoved = RandomUtil.getDifference(existingChildClinicIds, newChildClinicIds);
	        	
	        	//TODO : to be refactored with clinicRepository.findAll(clinicsToBeRemoved)
	        	for(String clinicId : clinicsToBeRemoved) {
	        		Clinic childClinic = clinicRepository.getOne(clinicId);
	        		
	        		childClinic.setParentClinic(null);
	        		clinicRepository.save(childClinic);
	        		clinic.getChildClinics().remove(childClinic);
	        	}
        	} else if(!clinicDTO.getParentClinic().isEmpty() && StringUtils.isNotBlank(clinicDTO.getParentClinic().get("id"))) {
        		if(!id.equals(clinicDTO.getParentClinic().get("id"))) {
        			Clinic parentClinic = clinicRepository.getOne(clinicDTO.getParentClinic().get("id"));
        			parentClinic.setParent(true);
        			clinicRepository.save(parentClinic);
        			clinic.setParentClinic(parentClinic);       			
        		} else {
        			jsonObject.put("ERROR", "Clinic can't be parent of his own.");
        			return jsonObject;
        		} 
        	} else {
       			clinicRepository.save(clinic);
        	} 
    		clinicRepository.save(clinic);
        	jsonObject.put("message", "Clinic updated successfully.");
            jsonObject.put("Clinic", clinic);
            if(clinicDTO.getParent()) {
            	jsonObject.put("ChildClinic", clinic.getChildClinics());
            }
        } else {
	      	jsonObject.put("ERROR", "Unable to update Clinic.");
        }
    	return jsonObject;
    }
    
    public JSONObject deleteClinic(String id) {
    	JSONObject jsonObject = new JSONObject();
    	Clinic existingClinic = clinicRepository.findOne(id);
		if(existingClinic != null) {
			if(existingClinic.getClinicAdminId() != null) {
				jsonObject.put("ERROR", "Unable to delete Clinic. Clinic admin exists.");
			} else if(existingClinic.getUsers().size() > 0) {
				jsonObject.put("ERROR", "Unable to delete Clinic. Healthcare Professionals are associated with it.");
			} else {
				if(existingClinic.isParent()) {
					existingClinic.getChildClinics().forEach(childClinic -> {
						childClinic.setParentClinic(null);
					});
					clinicRepository.save(existingClinic.getChildClinics());
					existingClinic.setParent(false);
				}
				clinicRepository.delete(existingClinic);
				jsonObject.put("message", "Clinic deleted successfully.");
			}
		} else {
			jsonObject.put("ERROR", "No such clinic exists.");
		}
		return jsonObject;
    }

	/**
	 * @param clinicDTO
	 * @param clinic
	 */
	private void assignUpdatedValues(ClinicDTO clinicDTO, Clinic clinic) {
		if (clinicDTO.getName() != null)
			clinic.setName(clinicDTO.getName());
		if (clinicDTO.getAddress() != null)
			clinic.setAddress(clinicDTO.getAddress());
		if (clinicDTO.getCity() != null)
			clinic.setCity(clinicDTO.getCity());
		if (clinicDTO.getState() != null)
			clinic.setState(clinicDTO.getState());
		if (clinicDTO.getZipcode() != null)
			clinic.setZipcode(clinicDTO.getZipcode());
		if (clinicDTO.getPhoneNumber() != null)
			clinic.setPhoneNumber(clinicDTO.getPhoneNumber());
		if (clinicDTO.getFaxNumber() != null)
			clinic.setFaxNumber(clinicDTO.getFaxNumber());
		if (clinicDTO.getHillromId() != null)
			clinic.setHillromId(clinicDTO.getHillromId());
		if (clinicDTO.getClinicAdminId() != null)
			clinic.setClinicAdminId(clinicDTO.getClinicAdminId());
	}

	public JSONObject getHCPUsers(List<String> idList) {
		JSONObject jsonObject = new JSONObject();
		Set<UserExtension> hcpUserList = new HashSet<>();
		for(String id : idList){
	    	Clinic clinic = clinicRepository.getOne(id);
	        if(clinic == null) {
		      	jsonObject.put("ERROR", "Invalid clinic id found.");
		      	return jsonObject;
	        } else {
	        	hcpUserList.addAll(clinic.getUsers());
	        }
		}
		jsonObject.put("hcpUsers", hcpUserList);
		if(hcpUserList.isEmpty()){
			jsonObject.put("message", "No associated HCPs.");
		} else {
	      	jsonObject.put("message", "Associated HCPs fetched successfully.");
        }
        return jsonObject;
    }
	
	public Set<UserExtension> getAssociatedPatientUsers(List<String> idList) throws HillromException {
		Set<UserExtension> patientUserList = new HashSet<>();
		for(String id : idList){
	    	Clinic clinic = clinicRepository.getOne(id);
	        if(clinic == null) {
	        	throw new HillromException(ExceptionConstants.HR_547);
	        } else {
	        	clinic.getClinicPatientAssoc().forEach(clinicPatientAssoc -> {
	        		patientUserList.add((UserExtension) userService.getUserObjFromPatientInfo(clinicPatientAssoc.getPatient()));
	        	});
	        }
		}
		return patientUserList;
    }
}
