package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
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

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.PatientNoEvent;
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
import com.hillrom.vest.service.ClinicService;
import com.hillrom.vest.service.HCPClinicService;
import com.hillrom.vest.service.PatientHCPService;
import com.hillrom.vest.service.PatientNoEventService;
import com.hillrom.vest.service.RelationshipLabelService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.CareGiverVO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.HcpClinicsVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
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
    
    @Inject
    private RelationshipLabelService relationshipLabelService;
    
    @Inject
    private ClinicService clinicService;
    
    @Inject
    private PatientNoEventService patientNoEventService;
    
    /**
     * POST  /user -> Create a new User.
     */
    @RequestMapping(value = "/user",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
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
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> dissociateClinicFromHCP(@PathVariable Long id, @RequestBody List<Map<String, String>> clinicList) {
        log.debug("REST request to dissociate clinic from HCP : {}", id);
        JSONObject jsonObject = new JSONObject();
        UserExtension hcpUser = hcpClinicService.dissociateClinicFromHCP(id, clinicList);
        if (Objects.nonNull(hcpUser)) {
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
    
    public ResponseEntity<List<HillRomUserVO>> search(@RequestParam(required=true,value = "searchString")String searchString,
    		@RequestParam(required=false, value = "filter")String filter,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending)
        throws URISyntaxException {
    	if(searchString.endsWith("_")){
    		   searchString = searchString.replace("_", "\\\\_");
    	}
    	String queryString = new StringBuilder("'%").append(searchString).append("%'").toString();
    	Map<String,Boolean> sortOrder = new HashMap<>();
    	if(sortBy != null  && !sortBy.equals("")) {
    		isAscending =  (isAscending != null)?  isAscending : true;
    		sortOrder.put(sortBy, isAscending);
    	}
    	Page<HillRomUserVO> page = userSearchRepository.findHillRomTeamUsersBy(queryString,filter,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/user/search", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /user/hcp/search -> get all HillromTeamUser.
     */
    @RequestMapping(value = "/user/hcp/search",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<?> searchHcp(@RequestParam(required=true,value = "searchString")String searchString,
    		@RequestParam(required=false,value = "filter")String filter,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending)
        throws URISyntaxException {
    	if(searchString.endsWith("_")){
    		   searchString = searchString.replace("_", "\\\\_");
    	}
    	String queryString = new StringBuilder("'%").append(searchString).append("%'").toString();
    	Map<String,Boolean> sortOrder = new HashMap<>();
    	if(sortBy != null  && !sortBy.equals("")) {
    		isAscending =  (isAscending != null) ?  isAscending : true;
    		sortOrder.put(sortBy, isAscending);
    	}
    	Page<HcpVO> page = userSearchRepository.findHCPBy(queryString,filter,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/user/hcp/search", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /user/hcpbypatientclinics/search -> // Get all HCPs associated with Clinics of a Patient.
     */
    @RequestMapping(value = "/patient/{patientId}/hcpbypatientclinics/search",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<?> searchAssociatedHcp(@PathVariable String patientId,
    		@RequestParam(required=true,value = "searchString")String searchString,
    		@RequestParam(required=false,value = "filter")String filter,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending)
        throws URISyntaxException {
    	if(searchString.endsWith("_")){
    		   searchString = searchString.replace("_", "\\\\_");
    	}
    	String queryString = new StringBuilder("'%").append(searchString).append("%'").toString();
    	Map<String,Boolean> sortOrder = new HashMap<>();
    	if(sortBy != null  && !sortBy.equals("")) {
    		isAscending =  (isAscending != null) ?  isAscending : true;
    		sortOrder.put(sortBy, isAscending);
    	}
    	Page<HcpVO> page = userSearchRepository.findHCPByPatientClinics(queryString,filter,patientId,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/patient/"+patientId+"/hcpbypatientclinics/search", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    
    /**
     * GET  user/clinicadmin/{id}/hcp/search -> search HCP associated with Clinic Admin
     */
    @RequestMapping(value = "user/clinicadmin/{id}/hcp/search",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<?> searchHcpForClinicAdmin(
    		@PathVariable Long id,
    		@RequestParam(required=true,value = "searchString")String searchString,
    		@RequestParam(required=false,value = "filter") String filter,
    		@RequestParam(value = "clinicId" , required = false) String clincId,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending)
        throws URISyntaxException {
    	if(searchString.endsWith("_")){
    		   searchString = searchString.replace("_", "\\\\_");
    	}
    	String queryString = new StringBuilder("'%").append(searchString).append("%'").toString();
    	Map<String,Boolean> sortOrder = new HashMap<>();
    	if(sortBy != null  && !sortBy.equals("")) {
    		isAscending =  (isAscending != null) ?  isAscending : true;
    		sortOrder.put(sortBy, isAscending);
    	}
    	Page<HcpVO> page = userSearchRepository.findHCPByClinicAdmin(id,clincId,queryString,filter,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "user/clinicadmin/"+id+"/hcp/search", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * GET  /user/:id/hcp -> get the "id" HCP user.
     */
    @RequestMapping(value = "/user/{id}/hcp",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<JSONObject> getHCPUser(@PathVariable Long id) {
        log.debug("REST request to get UserExtension : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			UserExtension hcpUser = userService.getHCPUser(id);
			if (Objects.isNull(hcpUser)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_533);
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
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> getAssociatedHCPUserForPatient(@PathVariable Long id) {
        log.debug("REST request to get associated HCP users with Patient : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<HcpClinicsVO> hcpUsers = patientHCPService.getAssociatedHCPUserForPatient(id);
	        if (hcpUsers.isEmpty()) {
	        	jsonObject.put("message", MessageConstants.HR_284);
	        } else {
	        	jsonObject.put("hcpUsers", hcpUsers);
	        }
	        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
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
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> getAssociatedClinicsForPatient(@PathVariable Long id) {
        log.debug("REST request to get associated clinics with Patient : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<ClinicVO> clinics = clinicPatientService.getAssociatedClinicsForPatient(id);
			if (clinics.isEmpty()) {
				jsonObject.put("message", MessageConstants.HR_285);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_275);
		    	jsonObject.put("clinics", clinics);
	        }
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
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
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> associateClinicsToPatient(@PathVariable Long id, @RequestBody List<Map<String, String>> clinicList) {
        log.debug("REST request to associate clinic with Patient : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<ClinicVO> clinics = clinicPatientService.associateClinicsToPatient(id, clinicList);
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
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> dissociateClinicsToPatient(@PathVariable Long id, @RequestBody List<Map<String, String>> clinicList) {
        log.debug("REST request to dissociate clinic with Patient : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<ClinicVO> clinics = clinicPatientService.dissociateClinicsToPatient(id, clinicList);
			if (!clinics.isEmpty()) {
				jsonObject.put("clinics", clinics);
			}
			jsonObject.put("message", MessageConstants.HR_274);
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
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
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> createCaregiver(@PathVariable Long id, @RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to save User : {}", userExtensionDTO);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        JSONObject jsonObject = new JSONObject();
		try {
			UserPatientAssoc caregiverAssoc = userService.createCaregiverUser(id, userExtensionDTO, baseUrl);
			if (Objects.isNull(caregiverAssoc)) {
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
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> getCaregiversForPatient(@PathVariable Long id) {
        log.debug("REST request to delete caregiver : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			List<CareGiverVO> caregiverList = userService.getCaregiversForPatient(id);
			if (caregiverList.isEmpty()) {
				jsonObject.put("message", MessageConstants.HR_266);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_263);
				jsonObject.put("caregivers", caregiverList);
	        }
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
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
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> updateCaregiver(@PathVariable Long patientUserId, @PathVariable Long caregiverUserId, @RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to update caregiver User : {}", userExtensionDTO);
        JSONObject jsonObject = new JSONObject();
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        try {
			UserPatientAssoc caregiverAssoc = userService.updateCaregiverUser(patientUserId, caregiverUserId, userExtensionDTO, baseUrl);
			if (Objects.isNull(caregiverAssoc)) {
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
     * PUT  /caregiver/{caregiverUserId} -> update a caregiver {caregiverUserId}. 
     */
    @RequestMapping(value = "/caregiver/{caregiverUserId}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> updateCaregiver( @PathVariable Long caregiverUserId, @RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        
        log.debug("REST request to update User : {}", userExtensionDTO);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        JSONObject jsonObject = new JSONObject();
		try {
			UserExtension userExtension = userService.updateCaregiverUser(caregiverUserId, userExtensionDTO, baseUrl);
			if (Objects.isNull(userExtension)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_562);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	            jsonObject.put("message", MessageConstants.HR_262);
                jsonObject.put("user", userExtension);
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
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> getCaregiver(@PathVariable Long patientUserId, @PathVariable Long caregiverUserId) {
        log.debug("REST request to get caregiver User : {}", caregiverUserId);
        JSONObject jsonObject = new JSONObject();
		try {
			UserPatientAssoc caregiverAssoc = userService.getCaregiverUser(patientUserId, caregiverUserId);
			if (Objects.isNull(caregiverAssoc)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_564);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_263);
				jsonObject.put("caregiver", caregiverAssoc);
	        }
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * GET  /caregiver/{caregiverUserId} -> GET a caregiver {caregiverUserId} . 
     */
    @RequestMapping(value = "/caregiver/{caregiverUserId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> getCaregiver(@PathVariable Long caregiverUserId) {
        log.debug("REST request to get caregiver User : {}", caregiverUserId);
        JSONObject jsonObject = new JSONObject();
		try {
			UserExtension caregiverUser = userService.getCaregiverUser(caregiverUserId);
			if (Objects.isNull(caregiverUser)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_564);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_263);
				jsonObject.put("caregiver", caregiverUser);
	        }
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
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
    
    /**
     * GET  /hcp/:id/patients -> get the patient users associated with hcp user.
     */
    @RequestMapping(value = "/hcp/{id}/patients",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> getAssociatedPatientUsersForHCP(@PathVariable Long id, @RequestParam(value = "filterByClinic",required = false) String filterByClinic) {
        log.debug("REST request to get associated patient users with HCP : {}", id);
        JSONObject jsonObject = new JSONObject();
        try {
	        if(StringUtils.isBlank(filterByClinic))
	        	filterByClinic = Constants.ALL;
	        List<Map<String,Object>> patientList= patientHCPService.getAssociatedPatientUsersForHCP(id, filterByClinic);
	        if (patientList.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_581);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_291);
	        	jsonObject.put("patientList", patientList);
	        }
	        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * GET  /hcp/:id/clinics -> get the clinics associated with hcp user.
     */
    @RequestMapping(value = "/hcp/{id}/clinics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.HCP})
    public ResponseEntity<?> getAssociatedClinicsForHCP(@PathVariable Long id) {
        log.debug("REST request to get associated clinics with HCP : {}", id);
        JSONObject jsonObject = new JSONObject();
        try {
        	Set<ClinicVO> clinics= hcpClinicService.getAssociatedClinicsForHCP(id);
	        if (clinics.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_582);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_292);
	        	jsonObject.put("clinics", clinics);
	        }
	        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * PUT  /clinics/:id/associatehcp -> associate hcp to the "id" clinic.
     */
    @RequestMapping(value = "/clinics/{id}/associatehcp",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> associateHCPToClinic(@PathVariable String id, @RequestBody List<Map<String, String>> hcpList) {
        log.debug("REST request to associate HCP users with clinic : {}", id);
        JSONObject jsonObject = new JSONObject();
        try {
	        Set<UserExtension> hcpUsers = hcpClinicService.associateHCPToClinic(id, hcpList);
	        if (hcpUsers.isEmpty()) {
	        	jsonObject.put("ERROR", ExceptionConstants.HR_583);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_293);
	        	jsonObject.put("hcpUsers", hcpUsers);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * GET  /user/all -> get the hcps.
     */
    @RequestMapping(value = "/user/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> getAllUsersByRole(@RequestParam(value = "role",required = false) String role) {
        log.debug("REST request to get all users with role : {}", role);
        JSONObject jsonObject = new JSONObject();
        try {
        	List<UserExtension> users= userService.getAllUsersBy(role);
	        if (users.isEmpty()) {
	        	jsonObject.put("ERROR", ExceptionConstants.HR_519);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_205);
	        	jsonObject.put("users", users);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * PUT  /patient/:id/dissociatehcp -> dissociate hcp from the "id" patient user.
     */
    @RequestMapping(value = "/patient/{id}/dissociatehcp",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> dissociateHCPToPatient(@PathVariable Long id, @RequestBody List<Map<String, String>> hcpList) {
        log.debug("REST request to dissociate hcp from Patient : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			String message = patientHCPService.dissociateHCPFromPatient(id, hcpList);
			jsonObject.put("message", message);
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
     
    }
    
    /**
     * GET  /user/:id/clinics -> get the clinics associated with clinic admin user.
     */
    @RequestMapping(value = "/user/{id}/clinics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<?> getAssociatedClinicsForClinicAdmin(@PathVariable Long id) {
        log.debug("REST request to get associated clinics with clinic admin user : {}", id);
        JSONObject jsonObject = new JSONObject();
        try {
        	Set<ClinicVO> clinics= clinicService.getAssociatedClinicsForClinicAdmin(id);
	        if (clinics.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_586);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_298);
	        	jsonObject.put("clinics", clinics);
	        }
	        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * GET  /user/:id/patients -> get the patients associated with caregiver user.
     */
    @RequestMapping(value = "/user/{id}/patients",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CARE_GIVER})
    public ResponseEntity<?> getAssociatedPatientsForCaregiver(@PathVariable Long id) {
        log.debug("REST request to get associated patients with caregiver : {}", id);
        JSONObject jsonObject = new JSONObject();
        try {
        	Map<String, List<CareGiverVO>> userPatientAssocs = userService.getAssociatedPatientsForCaregiver(id);
	        if (userPatientAssocs.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_587);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_299);
	        	jsonObject.put("caregivers", userPatientAssocs.get("caregivers"));
	        	jsonObject.put("patients", userPatientAssocs.get("patients"));
	        }
	        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * GET  /patient/:id/hcp -> get the hcp users associated with patient filter by Clinic.
     */
    @RequestMapping(value = "/patient/{id}/filteredhcp",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.HCP, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> getAssociatedHCPUsersForPatient(@PathVariable Long id, @RequestParam(value = "filterByClinic",required = false) String filterByClinic) {
        log.debug("REST request to get the hcp users associated with patient filter by Clinic : {}", id);
        JSONObject jsonObject = new JSONObject();
        try {
	        if(StringUtils.isBlank(filterByClinic))
	        	filterByClinic = Constants.ALL;
	        Set<UserExtension> hcpList= patientHCPService.getAssociatedHCPUsersForPatient(id, filterByClinic);
	        if (hcpList.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_588);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_290);
	        	jsonObject.put("hcpList", hcpList);
	        }
	        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * GET  /hcp/caregiver/:id/patients -> get the patients associated with HCP as caregiver
     */
    @RequestMapping(value = "/hcp/caregiver/{id}/patients",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.HCP})
    public ResponseEntity<JSONObject> getAssociatedPatientsForHCPasCaregiver(@PathVariable Long id) {
        log.debug("REST request to get the patients associated with HCP as caregiver : {}", id);
        JSONObject jsonObject = new JSONObject();
        try {
        	List<PatientUserVO> patientList = patientHCPService.getAssociatedPatientsForHCPasCaregiver(id);
	        if (patientList.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_589);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_300);
	        	jsonObject.put("patientUsers", patientList);
	        }
	        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/patient/{patientId}/firsttrasmissiondate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getfirstTransmissionDateForPatient(@PathVariable String patientId) {
        log.debug("REST request to get last transmission date for the patient : {}", patientId);
        JSONObject jsonObject = new JSONObject();
        LocalDate firstTransmissionDate;
        try {
        	firstTransmissionDate  = patientNoEventService.getPatientFirstTransmittedDate(patientId);
        	

        	jsonObject.put("firstTransmissionDate",
        			Objects.nonNull(firstTransmissionDate)?firstTransmissionDate.toString():null);
        	
	        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
}
