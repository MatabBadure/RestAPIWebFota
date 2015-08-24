package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.security.acl.NotOwnerException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientProtocolData;

import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.TherapySession;

import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSearchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.AdherenceCalculationService;
import com.hillrom.vest.service.PatientProtocolService;
import com.hillrom.vest.service.PatientVestDeviceService;
import com.hillrom.vest.service.TherapySessionService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.dto.ProtocolDTO;
import com.hillrom.vest.web.rest.dto.TherapyDataVO;
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
	
	@Inject
	private AdherenceCalculationService adherenceCalculationService;
	
	@Inject
	private PatientComplianceRepository complianceRepository;
	
	@Inject
	private NotificationRepository notificationRepository;

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
    public ResponseEntity<JSONObject> linkVestDeviceWithPatient(@PathVariable Long id, @RequestBody Map<String, Object> deviceData) {
    	log.debug("REST request to link vest device with patient user : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			Object responseObj = patientVestDeviceService.linkVestDeviceWithPatient(id, deviceData);
			if (responseObj instanceof User) {
				jsonObject.put("ERROR", ExceptionConstants.HR_572);
				jsonObject.put("user", (User) responseObj);
				return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
			} else {
				jsonObject.put("message", MessageConstants.HR_282);
				jsonObject.put("user", (PatientVestDeviceHistory) responseObj);
				return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
			}
		} catch (HillromException e) {
			jsonObject.put("ERROR",e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
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
    	JSONObject jsonObject = new JSONObject();
		try {
			List<PatientVestDeviceHistory> deviceList = patientVestDeviceService.getLinkedVestDeviceWithPatient(id);
			if(deviceList.isEmpty()){
     			jsonObject.put("message",MessageConstants.HR_281); //No device linked with patient.
     			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
     		} else {
     			jsonObject.put("message", MessageConstants.HR_282);//Vest devices linked with patient fetched successfully.
     			jsonObject.put("deviceList", deviceList);
     			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
     		}
		} catch (HillromException e) {
			jsonObject.put("ERROR",e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
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
    	JSONObject jsonObject = new JSONObject();
    	try {
			String message = patientVestDeviceService.deactivateVestDeviceFromPatient(id, serialNumber);
			if (StringUtils.isBlank(message)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_573);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", message);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
	
	@RequestMapping(value="/user/{id}/changeSecurityQuestion",method=RequestMethod.PUT)
	public ResponseEntity<?> updateSecurityQuestion(@PathVariable Long id,@RequestBody(required=true)Map<String,String> params){
		log.debug("REST request to update Security Question and Answer {}",id,params);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = userService.updateSecurityQuestion(id,params);
		} catch (HillromException e) {
			jsonObject.put("ERROR",e.getMessage());
			return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
		}
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
    public ResponseEntity<JSONObject> addProtocolToPatient(@PathVariable Long id, @RequestBody ProtocolDTO protocolDTO) {
    	log.debug("REST request to add protocol with patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
    		List<PatientProtocolData> protocolList = patientProtocolService.addProtocolToPatient(id, protocolDTO);
	    	if (protocolList.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_559);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_241);
	        	jsonObject.put("protocol", protocolList);
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
    public ResponseEntity<JSONObject> updateProtocolToPatient(@PathVariable Long id, @RequestBody List<PatientProtocolData> ppdList) {
    	log.debug("REST request to update protocol with patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
    		List<PatientProtocolData> protocolList = patientProtocolService.updateProtocolToPatient(id, ppdList);
	    	if (protocolList.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_560);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_242);
	        	jsonObject.put("protocol", protocolList);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    /**
     * GET  /patient/:id/protocol -> get all protocol for patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> getAllProtocolsAssociatedWithPatient(@PathVariable Long id) {
    	log.debug("REST request to get protocol for patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
    		List<PatientProtocolData> protocolList = patientProtocolService.getAllProtocolsAssociatedWithPatient(id);
    		if (protocolList.isEmpty()) {
	        	jsonObject.put("message", MessageConstants.HR_245);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_243);
	        	jsonObject.put("protocol", protocolList);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    /**
     * GET  /patient/:id/protocol/:protocolId -> get protocol details with {protocolId} for patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol/{protocolId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> getProtocolDetails(@PathVariable Long id, @PathVariable String protocolId) {
    	log.debug("REST request to get protocol details with {} for patient user : {}", protocolId, id);
    	JSONObject jsonObject = new JSONObject();
    	try {
    		List<PatientProtocolData> protocolList = patientProtocolService.getProtocolDetails(id, protocolId);
    		if (protocolList.isEmpty()) {
	        	jsonObject.put("ERROR", ExceptionConstants.HR_551);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_243);
	        	jsonObject.put("protocol", protocolList);
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
    @RequestMapping(value = "/patient/{id}/protocol/{protocolId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> deleteProtocolForPatient(@PathVariable Long id, @PathVariable String protocolId) {
    	log.debug("REST request to delete protocol for patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
	    	String message = patientProtocolService.deleteProtocolForPatient(id, protocolId);
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
    public ResponseEntity<JSONObject> getTherapyByPatientUserIdAndDate(@PathVariable Long id,
    		@RequestParam(required=false)Long from,
    		@RequestParam(required=false)Long to,
    		@RequestParam(required=false)String groupBy,
    		@RequestParam(required=false)Long date){
    	JSONObject jsonObject = new JSONObject();
    	if(Objects.nonNull(date)){
    		List<TherapySession> therapySessions = therapySessionService.findByPatientUserIdAndDate(id, date);
    		if(therapySessions.size() > 0){
    			ProtocolConstants protocol = adherenceCalculationService.getProtocolByPatientUserId(id);
    			jsonObject.put("recommended", protocol);
    			jsonObject.put("actual", therapySessions);
    		}
    		return new ResponseEntity<>(jsonObject,HttpStatus.OK);
    	}else if(Objects.nonNull(from) && Objects.nonNull(to) && Objects.nonNull(groupBy) ){
    		List<TherapyDataVO> therapyData = therapySessionService.findByPatientUserIdAndDateRange(id, from, to, groupBy);
    		if(therapyData.size() > 0){
    			ProtocolConstants protocol = adherenceCalculationService.getProtocolByPatientUserId(id);
    			jsonObject.put("recommended", protocol);
    			jsonObject.put("actual", therapyData);
    		}
    		return new ResponseEntity<>(jsonObject,HttpStatus.OK);
    	}else{
    		jsonObject.put("ERROR", "Required Params missing : [date or from&to&groupBy]");
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    @RequestMapping(value = "/users/{id}/compliance",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientCompliance> getComplianceScoreByPatientUserIdAndDate(@PathVariable Long id,
    		@RequestParam(value="date",required=false)Long timestamp){
    	LocalDate date = null;
    	if(Objects.isNull(timestamp)){
    		date = LocalDate.now();
    	}else{
    		date = LocalDate.fromDateFields(new Date(timestamp));
    	}
    	PatientCompliance compliance = complianceRepository.findByPatientUserIdAndDate(id, date);
    	if(Objects.nonNull(compliance))
    		return new ResponseEntity<>(compliance,HttpStatus.OK);
    	else
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/users/{id}/notifications",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Notification>> getNotificationsByPatientUserId(@PathVariable Long id,
    		@RequestParam(value="date",required=false)Long timestamp, 
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException{
    	LocalDate date = null;
    	if(Objects.isNull(timestamp)){
    		date = LocalDate.now();
    	}else{
    		date = LocalDate.fromDateFields(new Date(timestamp));
    	}
    	Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
    	Page<Notification> page = notificationRepository.findByPatientUserIdAndDateAndIsAcknowledged(id, date, false, pageable);
    	HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users/"+id+"/notifications", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/users/{userId}/notifications/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> acknowledgeNotification(@PathVariable Long userId,
    		@PathVariable Long id,@RequestBody(required=true)Map<String,String> params){
    	JSONObject json = new JSONObject();
    	Optional<Notification> notificationFromDB = Optional.of(notificationRepository.findOne(id));
	    	if(notificationFromDB.isPresent()){
	    		Notification notification = notificationFromDB.get();
	    		if(notification.getPatientUser().getId().equals(userId) 
	    				&& SecurityUtils.getCurrentLogin().equalsIgnoreCase(notification.getPatientUser().getEmail())){
	    			boolean isAcknowledged  = "TRUE".equalsIgnoreCase(params.get("isAcknowledged")) ? true : false;
	    			notification.setAcknowledged(isAcknowledged);
	    			notificationRepository.save(notification);
	    			json.put("notification", notification);
	    			return new ResponseEntity<>(json,HttpStatus.OK);
	    		}else{
	    			json.put("ERROR", ExceptionConstants.HR_403);
	    			return new ResponseEntity<>(json,HttpStatus.FORBIDDEN);
	    		}
	    	}else{
	    		json.put("ERROR", ExceptionConstants.HR_591);
	    		return new ResponseEntity<>(json,HttpStatus.NOT_FOUND);
	    	}    
    	}
    
    @RequestMapping(value = "/users/{id}/missedTherapyCount",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getMissedTherapyCount(@PathVariable Long id){
    	JSONObject json = new JSONObject();
    	json.put("count",therapySessionService.getMissedTherapyCountByPatientUserId(id));
    	return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }
}
