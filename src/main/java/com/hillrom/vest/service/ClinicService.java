package com.hillrom.vest.service;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.repository.ClinicRepository;
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
		return newClinic;
    }

    public Clinic updateClinic(Long id, ClinicDTO clinicDTO) {
        Clinic clinic = clinicRepository.getOne(id);
        if (clinic != null) {
        	assignUpdatedValues(clinicDTO, clinic);
        	if(clinicDTO.getParentClinic() != null) {
        		Clinic parentClinic = clinicRepository.getOne(clinicDTO.getParentClinic().get("id"));
       			parentClinic.setParent(true);
       			clinicRepository.save(parentClinic);
       			clinic.setParentClinic(parentClinic);
        	}
    		clinicRepository.save(clinic);
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
	}

}
