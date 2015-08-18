package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringUtils;
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
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSearchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.PatientProtocolService;
import com.hillrom.vest.service.PatientVestDeviceService;
import com.hillrom.vest.service.TherapySessionService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

	private final Logger log = LoggerFactory.getLogger(UserResource.class);

	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserSearchRepository userSearchRepository;
	
	@Inject
	private UserService userService;
	
	@Inject
	private PatientVestDeviceService patientVestDeviceService;

	@Inject
	private PatientProtocolService patientProtocolService;

	@Inject
	private TherapySessionService therapySessionService;
	/**
	 * GET /users -> get all users.
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<User> getAll() {
		log.debug("REST request to get all Users");
		return userRepository.findAll();
	}

	/**
	 * GET /users/:login -> get the "login" user.
	 */
	@RequestMapping(value = "/users/{email}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	ResponseEntity<User> getUser(@PathVariable String email) {
		log.debug("REST request to get User : {}", email);
		return userRepository.findOneByEmail(email)
				.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/user/patient/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> searchHcp(
			@RequestParam(required = true, value = "searchString") String searchString,
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "sort_by", required = false) String sortBy,
			@RequestParam(value = "asc", required = false) Boolean isAscending)
			throws URISyntaxException {
		String queryString = new StringBuilder("'%").append(searchString)
				.append("%'").toString();
		Map<String, Boolean> sortOrder = new HashMap<>();
		if (StringUtils.isNotBlank(sortBy)) {
			isAscending = (isAscending != null) ? isAscending : true;
			if(sortBy.equalsIgnoreCase("email"))
				sortOrder.put("user." + sortBy, isAscending);
			else	
				sortOrder.put(sortBy, isAscending);
		}
		Page<PatientUserVO> page = userSearchRepository.findPatientBy(
				queryString, PaginationUtil.generatePageRequest(offset, limit),
				sortOrder);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
				page, "/user/patient/search", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);

	}

	@RequestMapping(value = "/user/{id}/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PatientUserVO> getPatientUser(@PathVariable Long id) {
		log.debug("REST request to get PatientUser : {}", id);
		Optional<PatientUserVO> patientUser = userService.getPatientUser(id);
		if(patientUser.isPresent()){
			return new ResponseEntity<>(patientUser.get(),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/**
     * PUT  /patient/:id/linkvestdevice -> link vest device with patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/linkvestdevice",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> linkVestDeviceWithPatient(@PathVariable Long id, @RequestBody Map<String, String> deviceData) {
    	log.debug("REST request to link vest device with patient user : {}", id);
        JSONObject jsonObject = patientVestDeviceService.linkVestDeviceWithPatient(id, deviceData);
        if (jsonObject.containsKey("ERROR")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        }
    }
    
    /**
     * GET  /patient/:id/vestdevice -> get linked vest device with patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/vestdevice",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> getLinkedVestDeviceWithPatient(@PathVariable Long id) {
    	log.debug("REST request to link vest device with patient user : {}", id);
        JSONObject jsonObject = patientVestDeviceService.getLinkedVestDeviceWithPatient(id);
        if (jsonObject.containsKey("ERROR")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        }
    }
    
    /**
     * DELETE  /patient/:id/deactivatevestdevice/:serialNumber -> deactivate vest device with {serialNumber} from patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/deactivatevestdevice/{serialNumber}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> deactivateVestDeviceFromPatient(@PathVariable Long id, @PathVariable String serialNumber) {
    	log.debug("REST request to deactivate vest device with serial number {} from patient user : {}", serialNumber, id);
        JSONObject jsonObject = patientVestDeviceService.deactivateVestDeviceFromPatient(id, serialNumber);
        if (jsonObject.containsKey("ERROR")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        }
    }
	
	@RequestMapping(value="/user/{id}/changeSecurityQuestion",method=RequestMethod.PUT)
	public ResponseEntity<?> updateSecurityQuestion(@PathVariable Long id,@RequestBody(required=true)Map<String,String> params){
		log.debug("REST request to update Security Question and Answer {}",id,params);
		JSONObject jsonObject = userService.updateSecurityQuestion(id,params);
		if(jsonObject.containsKey("ERROR")){
			return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
     * POST  /patient/:id/protocol -> add protocol with patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> addProtocolToPatient(@PathVariable Long id, @RequestBody Map<String, String> protocolData) {
    	log.debug("REST request to add protocol with patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
	    	PatientProtocolData protocol = patientProtocolService.addProtocolToPatient(id, protocolData);
	    	if (Objects.isNull(protocol)) {
	        	jsonObject.put("message", MessageConstants.HR_245);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_241);
	        	jsonObject.put("protocol", protocol);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
	        }
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    /**
     * PUT  /patient/:id/protocol -> update protocol with patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> updateProtocolToPatient(@PathVariable Long id, @RequestBody Map<String, String> protocolData) {
    	log.debug("REST request to update protocol with patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
	    	PatientProtocolData protocol = patientProtocolService.updateProtocolToPatient(id, protocolData);
	    	if (Objects.isNull(protocol)) {
	        	jsonObject.put("message", MessageConstants.HR_245);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_242);
	        	jsonObject.put("protocol", protocol);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    /**
     * GET  /patient/:id/protocol -> get protocol for patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> getProtocolsAssociatedWithPatient(@PathVariable Long id) {
    	log.debug("REST request to get protocol for patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
	    	PatientProtocolData protocol = patientProtocolService.getProtocolsAssociatedWithPatient(id);
	        if (Objects.isNull(protocol)) {
	        	jsonObject.put("message", MessageConstants.HR_245);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_243);
	        	jsonObject.put("protocol", protocol);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    /**
     * DELETE  /patient/:id/protocol -> delete protocol for patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> deactivateProtocolFromPatient(@PathVariable Long id) {
    	log.debug("REST request to delete protocol for patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
	    	String message = patientProtocolService.deactivateProtocolFromPatient(id);
	        if (Objects.isNull(message)) {
	        	jsonObject.put("message", MessageConstants.HR_245);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", message);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }

    @RequestMapping(value = "/users/{id}/therapyData",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByPatientUserIdAndDate(@PathVariable Long id,
    		@RequestParam(required=false)Long from,
    		@RequestParam(required=false)Long to,
    		@RequestParam(required=false)String groupBy,
    		@RequestParam(required=false)Long date){
    	if(Objects.nonNull(date)){
    		return new ResponseEntity(therapySessionService.findByPatientUserIdAndDate(id, date),HttpStatus.OK);
    	}else{
    		return new ResponseEntity<>(therapySessionService.findByPatientUserIdAndDateRange(id, from, to, groupBy), HttpStatus.OK);
    	}
    }
}
