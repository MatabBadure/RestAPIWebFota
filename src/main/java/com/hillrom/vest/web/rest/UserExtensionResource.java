package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.MailService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.web.rest.dto.UserExtensionDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

/**
 * REST controller for managing user.
 */
@RestController
@RequestMapping("/api")
public class UserExtensionResource {

    private final Logger log = LoggerFactory.getLogger(UserExtensionResource.class);

    @Inject
    private UserExtensionRepository userExtensionRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private PatientInfoRepository patientInfoRepository;
    
    @Inject
    private MailService mailService;
    
    @Inject
    private UserService userService;

    /**
     * POST  /user -> Create a new User.
     */
    @RequestMapping(value = "/user",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ACCT_SERVICES)
    public ResponseEntity<JSONObject> create(@RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to save User : {}", userExtensionDTO);
        JSONObject jsonObject = new JSONObject();
        if (AuthoritiesConstants.PATIENT.equals(userExtensionDTO.getRole())) {
        	return patientInfoRepository.findOneByHillromId(userExtensionDTO.getHillromId())
        			.map(user -> {
        				jsonObject.put("message", "HR Id already in use.");
            			return ResponseEntity.badRequest().body(jsonObject);
            		})
                    .orElseGet(() -> {
                    	if(userExtensionDTO.getEmail() != null) {
	                    	userRepository.findOneByEmail(userExtensionDTO.getEmail())
	            			.map(user -> {
	            				jsonObject.put("message", "e-mail address already in use");
	                			return ResponseEntity.badRequest().body(jsonObject);
	                		});
                    	}
                    	UserExtension user = userService.createPatientUser(userExtensionDTO);
                		if(user.getId() != null) {
                			if(userExtensionDTO.getEmail() != null) {
                				String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                				mailService.sendActivationEmail(user, baseUrl);
                			}
	                        jsonObject.put("message", "Patient User created successfully.");
	                        jsonObject.put("user", user);
	                        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
                		} else {
                			jsonObject.put("message", "Unable to create Patient.");
	                        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
                		}
                    });
        } else if (AuthoritiesConstants.HCP.equals(userExtensionDTO.getRole())) {
        	jsonObject.put("message", "e-mail address already in use");
        	return userRepository.findOneByEmail(userExtensionDTO.getEmail())
            		.map(user -> {
            			return ResponseEntity.badRequest().body(jsonObject);
            		})
                    .orElseGet(() -> {
                    	UserExtension user = userService.createDoctor(userExtensionDTO);
                    	if(user.getId() != null) {
		                    String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		                    mailService.sendActivationEmail(user, baseUrl);
		                    jsonObject.put("message", "Doctor created successfully.");
		                    jsonObject.put("user", user);
		                    return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
                    	} else {
                			jsonObject.put("message", "Unable to create Doctor.");
	                        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
                		}
                    });
        } else {
    		jsonObject.put("message", "Incorrect data.");
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.PARTIAL_CONTENT);
    	}
    }

    /**
     * PUT  /user/{id} -> Updates an existing user (patient).
     */
    @RequestMapping(value = "/user/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> update(@PathVariable Long id, @RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to update User : {}", userExtensionDTO);
        JSONObject jsonObject = new JSONObject();
        if (AuthoritiesConstants.PATIENT.equals(userExtensionDTO.getRole())) {
        	if(userExtensionDTO.getEmail() != null) {
            	userRepository.findOneByEmail(userExtensionDTO.getEmail())
    			.map(user -> {
    				jsonObject.put("message", "e-mail address already in use");
        			return ResponseEntity.badRequest().body(jsonObject);
        		});
        	}
           	UserExtension user = userService.updatePatientUser(id, userExtensionDTO);
    		if(user.getId() != null) {
    			if(!user.getEmail().equals(userExtensionDTO.getEmail()) && !user.getActivated()) {
    				String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    				mailService.sendActivationEmail(user, baseUrl);
    			}
                jsonObject.put("message", "Patient User updated successfully.");
                jsonObject.put("user", user);
                return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
    		} else {
    			jsonObject.put("message", "Unable to update Patient.");
                return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    		}
        } else {
    		jsonObject.put("message", "Incorrect data.");
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.PARTIAL_CONTENT);
    	}
    }

    /**
     * GET  /user -> get all the userExtensions.
     */
    @RequestMapping(value = "/user",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<UserExtension>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<UserExtension> page = userExtensionRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/userExtensions", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /user/:id -> get the "id" userExtension.
     */
    @RequestMapping(value = "/user/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserExtension> get(@PathVariable Long id) {
        log.debug("REST request to get UserExtension : {}", id);
        return Optional.ofNullable(userExtensionRepository.findOne(id))
            .map(userExtension -> new ResponseEntity<>(
                userExtension,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user/:id -> delete the "id" userExtension.
     */
    @RequestMapping(value = "/user/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> delete(@PathVariable Long id) {
        log.debug("REST request to delete UserExtension : {}", id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "No such user exists.");
        return Optional.ofNullable(userExtensionRepository.findOne(id))
                .map(user -> {
                	userExtensionRepository.delete(user);
                    jsonObject.put("message", "User deleted successfully.");
                    return ResponseEntity.ok().body(jsonObject);
                }).orElse(new ResponseEntity<JSONObject>(jsonObject, HttpStatus.NOT_FOUND));
    }
}
