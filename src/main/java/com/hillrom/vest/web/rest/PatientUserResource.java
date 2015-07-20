package com.hillrom.vest.web.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.MailService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.web.rest.dto.UserExtensionDTO;

@RestController
@RequestMapping("/api")
public class PatientUserResource {
	
	private final Logger log = LoggerFactory.getLogger(PatientUserResource.class);
	
	@Inject
    private PatientInfoRepository patientInfoRepository;
	
	@Inject
	private UserService userService;
	
	@Inject
	private MailService mailService;

	/**
     * POST  /patientuser -> Create a new Patient User.
     */
    @RequestMapping(value = "/patientuser",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ACCT_SERVICES)
    public ResponseEntity<JSONObject> create(@RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to save Patient User : {}", userExtensionDTO);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "HR Id already in use.");
        return patientInfoRepository.findOneByHillromId(userExtensionDTO.getHillromId())
        		.map(user -> {
        			return ResponseEntity.badRequest().body(jsonObject);
        		})
                .orElseGet(() -> {
                	if (AuthoritiesConstants.PATIENT.equals(userExtensionDTO.getRole())) {
                		UserExtension user = userService.createPatientUser(userExtensionDTO);
                		if(user.getId() != null) {
	                        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
	                        mailService.sendActivationEmail(user, baseUrl);
	                        jsonObject.put("message", "Patient User created successfully.");
	                        jsonObject.put("user", user);
	                        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
                		} else {
                			jsonObject.put("message", "Unable to create Patient.");
	                        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
                		}
                	} else {
                		jsonObject.put("message", "Incorrect data.");
                		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.PARTIAL_CONTENT);
                	}
                });
    }
}
