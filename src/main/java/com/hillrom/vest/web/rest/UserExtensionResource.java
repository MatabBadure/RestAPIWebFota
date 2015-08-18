package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
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
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.HcpVO;
import com.hillrom.vest.repository.HillRomUserVO;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserSearchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.ClinicPatientService;
import com.hillrom.vest.service.HCPClinicService;
import com.hillrom.vest.service.PatientHCPService;
import com.hillrom.vest.service.RelationshipLabelService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
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
    private UserService userService;

    @Inject
    private HCPClinicService hcpClinicService;
    
    @Inject
    private PatientHCPService patientHCPService;
    
    @Inject
    private ClinicPatientService clinicPatientService;
    
    @Inject
    private UserSearchRepository userSearchRepository;
    
    @Inject
    private RelationshipLabelService relationshipLabelService;
    
    /**
     * POST  /user -> Create a new User.
     */
    @RequestMapping(value = "/user",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> create(@RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to save User : {}", userExtensionDTO);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        JSONObject jsonObject = new JSONObject();
		try {
			UserExtension newUser = userService.createUser(userExtensionDTO, baseUrl);
			if (Objects.nonNull(newUser)) {
				jsonObject.put("message", MessageConstants.HR_201);
				jsonObject.put("user", newUser);
				return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
	        } else {
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }

    /**
     * PUT  /user/:id -> Updates an existing user.
     */
    @RequestMapping(value = "/user/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> update(@PathVariable Long id, @RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to update User : {}", userExtensionDTO);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        JSONObject jsonObject = new JSONObject();
		try {
			UserExtension userExtension = userService.updateUser(id, userExtensionDTO, baseUrl);
			if (Objects.isNull(userExtension)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_518);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	            jsonObject.put("message", MessageConstants.HR_202);
                jsonObject.put("user", userExtension);
                return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }

    /**
     * GET  /user -> get all the users.
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
     * GET  /user/:id -> get the "id" user.
     */
    @RequestMapping(value = "/user/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> get(@PathVariable Long id) {
        log.debug("REST request to get UserExtension : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			User user = userService.getUser(id);
			if (Objects.isNull(user)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_514);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_203);//User fetched successfully
			    jsonObject.put("user", user);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }

    /**
     * DELETE  /user/:id -> delete the "id" user.
     */
    @RequestMapping(value = "/user/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> delete(@PathVariable Long id) {
        log.debug("REST request to delete UserExtension : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = userService.deleteUser(id);
			 if (jsonObject.containsKey("ERROR")) {
		        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.FORBIDDEN);
		        } else {
		            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.FORBIDDEN);
		}
       
    }
    
    /**
     * PUT  /user/:id/dissociateclinic -> dissociate clinic from the "id" user.
     */
    @RequestMapping(value = "/user/{id}/dissociateclinic",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> dissociateClinicFromHCP(@PathVariable Long id, @RequestBody List<Map<String, String>> clinicList) {
        log.debug("REST request to dissociate clinic from HCP : {}", id);
        JSONObject jsonObject = new JSONObject();
        UserExtension hcpUser = hcpClinicService.dissociateClinicFromHCP(id, clinicList);
        if (jsonObject.containsKey("message")) {
        	jsonObject.put("message", MessageConstants.HR_272);
        	jsonObject.put("HCPUser", hcpUser);
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * GET  /user/search -> get all HillromTeamUser.
     */
    @RequestMapping(value = "/user/search",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<HillRomUserVO>> search(@RequestParam(required=true,value = "searchString")String searchString,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending)
        throws URISyntaxException {
    	String queryString = new StringBuilder("'%").append(searchString).append("%'").toString();
    	Map<String,Boolean> sortOrder = new HashMap<>();
    	if(sortBy != null  && !sortBy.equals("")) {
    		isAscending =  (isAscending != null)?  isAscending : true;
    		sortOrder.put(sortBy, isAscending);
    	}
    	Page<HillRomUserVO> page = userSearchRepository.findHillRomTeamUsersBy(queryString,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/user/search", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /user/search -> get all HillromTeamUser.
     */
    @RequestMapping(value = "/user/hcp/search",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> searchHcp(@RequestParam(required=true,value = "searchString")String searchString,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending)
        throws URISyntaxException {
    	String queryString = new StringBuilder("'%").append(searchString).append("%'").toString();
    	Map<String,Boolean> sortOrder = new HashMap<>();
    	if(sortBy != null  && !sortBy.equals("")) {
    		isAscending =  (isAscending != null) ?  isAscending : true;
    		sortOrder.put(sortBy, isAscending);
    	}
    	Page<HcpVO> page = userSearchRepository.findHCPBy(queryString,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/user/hcp/search", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /user/:id/hcp -> get the "id" HCP user.
     */
    @RequestMapping(value = "/user/{id}/hcp",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> getHCPUser(@PathVariable Long id) {
        log.debug("REST request to get UserExtension : {}", id);
        JSONObject jsonObject = new JSONObject();
        UserExtension hcpUser;
		try {
			hcpUser = userService.getHCPUser(id);
			if (jsonObject.containsKey("ERROR")) {
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_253);
	            jsonObject.put("user", hcpUser);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * PUT  /patient/:id/associatehcp -> associate hcp to the "id" patient user.
     */
    @RequestMapping(value = "/patient/{id}/associatehcp",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> associateHCPToPatient(@PathVariable Long id, @RequestBody List<Map<String, String>> hcpList) {
        log.debug("REST request to associate HCP users with Patient : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<User> users = patientHCPService.associateHCPToPatient(id, hcpList);
	        if (users.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_534);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_271);
		    	jsonObject.put("hcpUsers", users);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
        } catch (HillromException e) {
        	jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * GET  /patient/:id/hcp -> get the HCP users associated with patient user.
     */
    @RequestMapping(value = "/patient/{id}/hcp",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> getAssociatedHCPUserForPatient(@PathVariable Long id) {
        log.debug("REST request to get associated HCP users with Patient : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<User> hcpUsers = patientHCPService.getAssociatedHCPUserForPatient(id);
	        if (hcpUsers.isEmpty()) {
	        	jsonObject.put("message", MessageConstants.HR_284);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_276);
		    	jsonObject.put("hcpUsers", hcpUsers);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
        } catch (HillromException e) {
        	jsonObject.put("ERROR", e.getMessage());
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * GET  /patient/:id/clinics -> get the clinics associated with patient user.
     */
    @RequestMapping(value = "/patient/{id}/clinics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> getAssociatedClinicsForPatient(@PathVariable Long id) {
        log.debug("REST request to get associated clinics with Patient : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<Clinic> clinics = clinicPatientService.getAssociatedClinicsForPatient(id);
			if (clinics.isEmpty()) {
				jsonObject.put("message", MessageConstants.HR_285);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_275);
		    	jsonObject.put("clinics", clinics);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR",e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
        
    }
    
    /**
     * PUT  /patient/:id/associateclinics -> associate clinics to the "id" patient user.
     */
    @RequestMapping(value = "/patient/{id}/associateclinics",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> associateClinicsToPatient(@PathVariable Long id, @RequestBody List<Map<String, String>> clinicList) {
        log.debug("REST request to associate clinic with Patient : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<Clinic> clinics = clinicPatientService.associateClinicsToPatient(id, clinicList);
			if (clinics.isEmpty()) {
				jsonObject.put("message", ExceptionConstants.HR_535);
		       	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		    } else {
	        	jsonObject.put("message", MessageConstants.HR_273);
		    	jsonObject.put("clinics", clinics);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR",e.getMessage());		
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * PUT  /patient/:id/dissociateclinics -> dissociate clinics to the "id" patient user.
     */
    @RequestMapping(value = "/patient/{id}/dissociateclinics",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> dissociateClinicsToPatient(@PathVariable Long id, @RequestBody List<Map<String, String>> clinicList) {
        log.debug("REST request to dissociate clinic with Patient : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<Clinic> clinics = clinicPatientService.dissociateClinicsToPatient(id, clinicList);
			if (jsonObject.containsKey("ERROR")) {
				jsonObject.put("message", ExceptionConstants.HR_536);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message",MessageConstants.HR_274);
		    	jsonObject.put("clinics", clinics);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
     
    }
    
	/**
     * POST  /patient/{id}/caregiver -> Create a caregiver for a patient with {id}. 
     */
    @RequestMapping(value = "/patient/{id}/caregiver",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> createCaregiver(@PathVariable Long id, @RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to save User : {}", userExtensionDTO);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        JSONObject jsonObject = new JSONObject();
		try {
			UserPatientAssoc caregiverAssoc = userService.createCaregiverUser(id, userExtensionDTO, baseUrl);
			if (Objects.nonNull(caregiverAssoc)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_561);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_261);
                jsonObject.put("caregiver", caregiverAssoc);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * DELETE  /patient/{patientUserId}/caregiver/{id} -> delete the caregiver with "id" from patient user.
     */
    @RequestMapping(value = "/patient/{patientUserId}/caregiver/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> deleteCaregiver(@PathVariable Long patientUserId, @PathVariable Long id) {
        log.debug("REST request to delete caregiver : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			String message = userService.deleteCaregiverUser(patientUserId, id);
			if (StringUtils.isBlank(message)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_566);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.FORBIDDEN);
	        } else {
	        	jsonObject.put("message", message);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
        
    }
    
    /**
     * GET  /patient/{id}/caregiver -> get the list of caregivers associated with patient user {id}.
     * @throws HillromException 
     */
    @RequestMapping(value = "/patient/{id}/caregiver",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> getCaregiversForPatient(@PathVariable Long id) {
        log.debug("REST request to delete caregiver : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<UserPatientAssoc> caregiverAssocList = userService.getCaregiversForPatient(id);
			if (caregiverAssocList.isEmpty()) {
				jsonObject.put("ERROR", ExceptionConstants.HR_567);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_263);
				jsonObject.put("caregivers", caregiverAssocList);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * PUT  /patient/{patientUserId}/caregiver/{caregiverUserId} -> update a caregiver {caregiverUserId} for a patient with {patientUserId}. 
     */
    @RequestMapping(value = "/patient/{patientUserId}/caregiver/{caregiverUserId}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> updateCaregiver(@PathVariable Long patientUserId, @PathVariable Long caregiverUserId, @RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to update caregiver User : {}", userExtensionDTO);
        JSONObject jsonObject = new JSONObject();
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        try {
			UserPatientAssoc caregiverAssoc = userService.updateCaregiverUser(patientUserId, caregiverUserId, userExtensionDTO, baseUrl);
			if (Objects.nonNull(caregiverAssoc)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_562);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_262);
				jsonObject.put("caregiver", caregiverAssoc);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
        } catch (HillromException e) {
        	jsonObject.put("ERROR", e.getMessage());
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * GET  /patient/{patientUserId}/caregiver/{caregiverUserId} -> GET a caregiver {caregiverUserId} for a patient with {patientUserId}. 
     */
    @RequestMapping(value = "/patient/{patientUserId}/caregiver/{caregiverUserId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> getCaregiver(@PathVariable Long patientUserId, @PathVariable Long caregiverUserId) {
        log.debug("REST request to get caregiver User : {}", caregiverUserId);
        JSONObject jsonObject = new JSONObject();
		try {
			UserPatientAssoc caregiverAssoc = userService.getCaregiverUser(patientUserId, caregiverUserId);
			if (Objects.isNull(caregiverAssoc)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_568);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_263);
				jsonObject.put("caregiver", caregiverAssoc);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * GET  /patient/relationships -> GET list of relationships of caregivers with a patient. 
     */
    @RequestMapping(value = "/patient/relationships",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> getRelationshipLabels() {
        log.debug("REST request to get list of relationships of caregivers with a patient User");
        JSONObject jsonObject = new JSONObject();
        try {
        	List<String> relationshipLabels = relationshipLabelService.getRelationshipLabels();
        	jsonObject.put("message", MessageConstants.HR_265);
        	jsonObject.put("relationshipLabels", relationshipLabels);
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
}
