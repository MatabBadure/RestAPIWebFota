package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.web.rest.dto.ClinicDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ClinicService {

    private final Logger log = LoggerFactory.getLogger(ClinicService.class);

    @Inject
    private ClinicRepository clinicRepository;

    public Clinic createClinic(ClinicDTO clinicDTO) {
    	Clinic newParentClinic = new Clinic();
    	clinicRepository.findOneByName(clinicDTO.getName())
		.map(parentClinic -> {
			newParentClinic.setId(parentClinic.getId());
			newParentClinic.setName(parentClinic.getName());
			newParentClinic.setAddress(parentClinic.getAddress());
			newParentClinic.setZipcode(parentClinic.getZipcode());
			newParentClinic.setCity(parentClinic.getCity());
			newParentClinic.setState(parentClinic.getState());
			newParentClinic.setPhoneNumber(parentClinic.getPhoneNumber());
			newParentClinic.setHillromId(parentClinic.getHillromId());
			newParentClinic.setDeleted(parentClinic.isDeleted());
			return newParentClinic;
		}).orElseGet(() -> {
			newParentClinic.setName(clinicDTO.getName());
			newParentClinic.setAddress(clinicDTO.getAddress());
			newParentClinic.setZipcode(clinicDTO.getZipcode());
			newParentClinic.setCity(clinicDTO.getCity());
			newParentClinic.setState(clinicDTO.getState());
			newParentClinic.setPhoneNumber(clinicDTO.getPhoneNumber());
			newParentClinic.setHillromId(clinicDTO.getHillromId());
			newParentClinic.setDeleted(false);
			clinicRepository.save(newParentClinic);
			return newParentClinic;
        });
    	for(Map<String, String> childClinicName : clinicDTO.getChildClinics()) {
    		Clinic childClinic = new Clinic();
    		childClinic.setName(childClinicName.get("name"));
    		childClinic.setParentClinic(newParentClinic);
    		childClinic.setDeleted(false);
    		clinicRepository.save(childClinic);
    		newParentClinic.getChildClinics().add(childClinic);
    	}
    	return newParentClinic;
    }

    public Clinic updateClinic(Long id, ClinicDTO clinicDTO) {
        Clinic clinic = clinicRepository.getOne(id);
        if (clinic != null) {
        	assignUpdatedValues(clinicDTO, clinic);
        	List<String> existingChildClinicIds = new ArrayList<String>();
        	List<String> newChildClinicIds = new ArrayList<String>();
        	for(Clinic childClinic : clinic.getChildClinics()) {
        		existingChildClinicIds.add(childClinic.getId().toString());
        	}
        	for(Map<String, String> childClinic : clinicDTO.getChildClinics()) {
        		newChildClinicIds.add(childClinic.get("id"));
        	}
        	List<String> clinicsToBeAdded = RandomUtil.getDifference(newChildClinicIds, existingChildClinicIds);
        	List<String> clinicsToBeRemoved = RandomUtil.getDifference(existingChildClinicIds, newChildClinicIds);
    		for(String clinicId : clinicsToBeAdded) {
        		Clinic childClinic = clinicRepository.getOne(Long.parseLong(clinicId));
        		childClinic.setParentClinic(clinic);
        		clinicRepository.save(childClinic);
        		clinic.getChildClinics().add(childClinic);
        	}
    		for(String clinicId : clinicsToBeRemoved) {
        		Clinic childClinic = clinicRepository.getOne(Long.parseLong(clinicId));
        		childClinic.setParentClinic(null);
        		clinicRepository.save(childClinic);
        		clinic.getChildClinics().remove(childClinic);
        	}
        }
    	return clinic;
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
		if (clinicDTO.getNpiNumber() != null)
			clinic.setNpiNumber(clinicDTO.getNpiNumber());
	}

}
