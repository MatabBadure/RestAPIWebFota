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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scala.annotation.cloneable;

import javax.inject.Inject;

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
    	Clinic newClinic = new Clinic();
    	newClinic.setName(clinicDTO.getName());
    	newClinic.setAddress(clinicDTO.getAddress());
    	newClinic.setZipcode(clinicDTO.getZipcode());
    	newClinic.setCity(clinicDTO.getCity());
    	newClinic.setState(clinicDTO.getState());
    	newClinic.setPhoneNumber(clinicDTO.getPhoneNumber());
    	newClinic.setHillromId(clinicDTO.getClinicAdminId());
    	newClinic.setDeleted(false);
    	if(clinicDTO.getParentClinicName() != null){
    		clinicRepository.findOneByName(clinicDTO.getParentClinicName())
    		.map(parentClinic -> {
    			System.out.println("PArentClinic Found: "+parentClinic);
    			newClinic.setParentClinic(parentClinic);
    			return newClinic;
    		});
    	}
    	System.out.println("PArentClinic Not found !! " +clinicDTO.getParentClinicName());
    	clinicRepository.save(newClinic);
    	return newClinic;
    }

    public void updateUserInformation(String firstName, String lastName, String email, String langKey) {
        clinicRepository.findOneByName(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
        	clinicRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

}
