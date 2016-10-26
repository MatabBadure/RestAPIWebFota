package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.YYYY_MM_DD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.AdherenceReset;
import com.hillrom.vest.domain.EntityUserAssoc;
import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AdherenceResetRepository;
import com.hillrom.vest.repository.EntityUserRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSearchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.AdherenceResetDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.util.ClinicVOBuilder;
import org.joda.time.DateTime; 

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class AdherenceResetService {

    private final Logger log = LoggerFactory.getLogger(ClinicService.class);

    @Inject
    private AdherenceResetRepository adherenceResetRepository;
    
    @Inject
    private UserService userService;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private UserExtensionRepository userExtensionRepository;
    
    @Inject
    private UserSearchRepository userSearchRepository;
    
    @Inject
    private EntityUserRepository entityUserRepository;
    
    //hill-1847
    public AdherenceReset findOneByPatientUserIdAndCreatedByAndResetDate(Long patientUserId, Long createdById, DateTime resetDate){
    	Optional<AdherenceReset> adherenceReset = adherenceResetRepository.findOneByPatientUserIdAndCreatedByAndResetDate(patientUserId, createdById, resetDate);
    	if(adherenceReset.isPresent())
			return adherenceReset.get();
		return null;
	}
    //hill-1847
    public AdherenceReset createAdherenceReset(String patientId, Long patientUserId, DateTime resetDate, Integer resetScore, 
												LocalDate resetStartDate, String justification, Long createdById) throws HillromException {
    	
    	AdherenceReset existAdherenceReset = findOneByPatientUserIdAndCreatedByAndResetDate(patientUserId, createdById, resetDate);
    	
		//hill-1847
    	
    		existAdherenceReset = new AdherenceReset();
    		
    		User patientUser = userRepository.findOne(patientUserId);
    		existAdherenceReset.setPatientUser(patientUser);
    		
    		PatientInfo patient = userService.getPatientInfoObjFromPatientUser(patientUser);
    		existAdherenceReset.setPatient(patient);
    		
    		existAdherenceReset.setResetDate(resetDate);
    		existAdherenceReset.setResetScore(resetScore);
    		existAdherenceReset.setResetStartDate(resetStartDate);
    		existAdherenceReset.setJustification(justification);
    		existAdherenceReset.setCreatedBy(createdById);
    		
    		adherenceResetRepository.save(existAdherenceReset);
    	//hill-1847
    	return existAdherenceReset;
    }

    


}
