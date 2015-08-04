package com.hillrom.vest.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.HillromIdGenerator;
import com.hillrom.vest.service.util.RandomUtil;
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
    private HillromIdGenerator hillromIdGenerator;

    public JSONObject createClinic(ClinicDTO clinicDTO) {
    	JSONObject jsonObject = new JSONObject();
    	Clinic newClinic = new Clinic();
    	// Assigns the next clinic HillromId from Stored Procedure
    	newClinic.setId(hillromIdGenerator.getNextClinicHillromId());
    	if(clinicDTO.getParent()) {
    		newClinic.setParent(clinicDTO.getParent());
    	}
    	if(clinicDTO.getParentClinic().get("id") != null) {
    		Clinic parentClinic = clinicRepository.getOne(Long.parseLong(clinicDTO.getParentClinic().get("id")));
   			parentClinic.setParent(true);
   			clinicRepository.save(parentClinic);
   			newClinic.setParentClinic(parentClinic);
    	}
    	assignUpdatedValues(clinicDTO, newClinic);
		clinicRepository.save(newClinic);
        if(newClinic.getId() != null) {
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
        } else if(clinic.getId() != null) {
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
	        	for(String clinicId : clinicsToBeRemoved) {
	        		Clinic childClinic = clinicRepository.getOne(clinicId);
	        		childClinic.setParentClinic(null);
	        		clinicRepository.save(childClinic);
	        		clinic.getChildClinics().remove(childClinic);
	        	}
        	} else if(clinicDTO.getParentClinic().size() != 0 && clinicDTO.getParentClinic().get("id") != null) {
        		if(id != Long.parseLong(clinicDTO.getParentClinic().get("id"))) {
        			Clinic parentClinic = clinicRepository.getOne(Long.parseLong(clinicDTO.getParentClinic().get("id")));
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
					for(Clinic childClinic : existingClinic.getChildClinics()) {
						childClinic.setParentClinic(null);
						clinicRepository.save(childClinic);
					}
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

}
