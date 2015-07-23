package com.hillrom.vest.service;

import java.util.List;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.SearchCriteria;
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
    private SearchService<Clinic> clinicSearchService;

    public JSONObject createClinic(ClinicDTO clinicDTO) {
    	JSONObject jsonObject = new JSONObject();
    	Clinic newClinic = new Clinic();
    	if(clinicDTO.getParentClinic() != null) {
    		Clinic parentClinic = clinicRepository.getOne(clinicDTO.getParentClinic().get("id"));
   			parentClinic.setParent(true);
   			clinicRepository.save(parentClinic);
   			newClinic.setParentClinic(parentClinic);
    	}
    	assignUpdatedValues(clinicDTO, newClinic);
		newClinic.setParent(false);
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

    public JSONObject updateClinic(Long id, ClinicDTO clinicDTO) {
    	JSONObject jsonObject = new JSONObject();
    	Clinic clinic = clinicRepository.getOne(id);
        if(clinic == null) {
	      	jsonObject.put("ERROR", "No such clinic found.");
        } else if(clinic.getId() != null) {
        	assignUpdatedValues(clinicDTO, clinic);
        	if(clinicDTO.getParentClinic() != null) {
        		Clinic parentClinic = clinicRepository.getOne(clinicDTO.getParentClinic().get("id"));
       			parentClinic.setParent(true);
       			clinicRepository.save(parentClinic);
       			clinic.setParentClinic(parentClinic);
        	}
    		clinicRepository.save(clinic);
        	jsonObject.put("message", "Clinic updated successfully.");
            jsonObject.put("Clinic", clinic);
        } else {
	      	jsonObject.put("ERROR", "Unable to update Clinic.");
        }
    	return jsonObject;
    }
    
    public JSONObject deleteClinic(Long id) {
    	JSONObject jsonObject = new JSONObject();
    	Clinic existingClinic = clinicRepository.findOne(id);
		if(existingClinic != null) {
			if(existingClinic.getHillromId() != null) {
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
	}
	
	public List<Clinic> searchClinics(String queryString){
		SearchCriteria<Clinic> criteria = new SearchCriteria<>(Clinic.class, queryString, 0, 10);
		return clinicSearchService.findBy(criteria);
	}

}
