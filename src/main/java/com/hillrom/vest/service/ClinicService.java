package com.hillrom.vest.service;

import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.AuthorityRepository;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.web.rest.dto.ClinicDTO;

import net.minidev.json.JSONObject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scala.annotation.cloneable;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    	clinicRepository.findOneByName(clinicDTO.getParentClinicName())
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
			newParentClinic.setName(clinicDTO.getParentClinicName());
			newParentClinic.setAddress(clinicDTO.getAddress());
			newParentClinic.setZipcode(clinicDTO.getZipcode());
			newParentClinic.setCity(clinicDTO.getCity());
			newParentClinic.setState(clinicDTO.getState());
			newParentClinic.setPhoneNumber(clinicDTO.getPhoneNumber());
			newParentClinic.setHillromId(clinicDTO.getClinicAdminId());
			newParentClinic.setDeleted(false);
			clinicRepository.save(newParentClinic);
			return newParentClinic;
        });
    	for(String childClinicName : clinicDTO.getChildClinicList()) {
    		Clinic childClinic = new Clinic();
    		childClinic.setName(childClinicName);
    		childClinic.setAddress(clinicDTO.getAddress());
    		childClinic.setZipcode(clinicDTO.getZipcode());
    		childClinic.setCity(clinicDTO.getCity());
    		childClinic.setState(clinicDTO.getState());
    		childClinic.setPhoneNumber(clinicDTO.getPhoneNumber());
    		childClinic.setHillromId(clinicDTO.getClinicAdminId());
    		childClinic.setParentClinic(newParentClinic);
    		childClinic.setDeleted(false);
    		clinicRepository.save(childClinic);
    		newParentClinic.getChildClinics().add(childClinic);
    	}
    	return newParentClinic;
    }

    public void updateUserInformation(String firstName, String lastName, String email, String langKey) {
        clinicRepository.findOneByName(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
        	clinicRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

}
