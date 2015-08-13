package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.HcpVO;
import com.hillrom.vest.repository.HillRomUserVO;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserSearchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.ClinicPatientService;
import com.hillrom.vest.service.HCPClinicService;
import com.hillrom.vest.service.PatientHCPService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.UserExtensionDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

import net.minidev.json.JSONObject;

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
			if (newUser != null) {
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
        UserExtension userExtension;
		try {
			userExtension = userService.updateUser(id, userExtensionDTO, baseUrl);
			if (jsonObject.containsKey("ERROR")) {
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
        User user;
		try {
			
			user = userService.getUser(id);
			if (jsonObject.containsKey("ERROR")) {
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
        JSONObject jsonObject = hcpClinicService.dissociateClinicFromHCP(id, clinicList);
        if (jsonObject.containsKey("message")) {
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
	        	jsonObject.put("message", MessageConstants.HR_222);//"HealthCare Professional fetched successfully."
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
        List<User> users;
		try {
			users = patientHCPService.associateHCPToPatient(id, hcpList);
		
	        if (jsonObject.containsKey("ERROR")) {
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_231);//HCPs are associated with patient successfully.
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
        List<User> hcpUsers = new LinkedList<>();
		try {
			hcpUsers = patientHCPService.getAssociatedHCPUserForPatient(id);
		
	        if (jsonObject.containsKey("ERROR")) {
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", "Associated HCPs with patient fetched successfully.");
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
			jsonObject = clinicPatientService.getAssociatedClinicsForPatient(id);
			if (jsonObject.containsKey("ERROR")) {
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
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
			jsonObject = clinicPatientService.associateClinicsToPatient(id, clinicList);
			 if (jsonObject.containsKey("ERROR")) {
		        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		        } else {
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
			jsonObject = clinicPatientService.dissociateClinicsToPatient(id, clinicList);
			if (jsonObject.containsKey("ERROR")) {
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
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
        UserExtension userExtension;
        
		try {
			userExtension = userService.createCaregiverUser(id, userExtensionDTO, baseUrl);
			if (jsonObject.containsKey("ERROR")) {
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_201);
                jsonObject.put("user", userExtension);
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
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> deleteCaregiver(@PathVariable Long patientUserId, @PathVariable Long id) {
        log.debug("REST request to delete caregiver : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = userService.deleteCaregiverUser(patientUserId, id);
			if (jsonObject.containsKey("ERROR")) {
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.FORBIDDEN);
	        } else {
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
        
    }
}
